package org.poly2tri.examples.ardor3d.base;

import java.nio.FloatBuffer;
import java.util.List;

import org.poly2tri.geometry.polygon.PolygonSet;
import org.poly2tri.triangulation.TriangulationAlgorithm;
import org.poly2tri.triangulation.TriangulationPoint;
import org.poly2tri.triangulation.TriangulationProcess;
import org.poly2tri.triangulation.delaunay.DelaunayTriangle;
import org.poly2tri.triangulation.delaunay.sweep.DTSweepContext;
import org.poly2tri.triangulation.tools.ardor3d.ArdorMeshMapper;

import com.ardor3d.framework.Canvas;
import com.ardor3d.framework.FrameHandler;
import com.ardor3d.input.Key;
import com.ardor3d.input.logical.InputTrigger;
import com.ardor3d.input.logical.KeyPressedCondition;
import com.ardor3d.input.logical.LogicalLayer;
import com.ardor3d.input.logical.TriggerAction;
import com.ardor3d.input.logical.TwoInputStates;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.IndexMode;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.renderer.state.WireframeState;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.MeshData;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Point;
import com.ardor3d.scenegraph.hint.LightCombineMode;
import com.ardor3d.ui.text.BasicText;
import com.ardor3d.util.ReadOnlyTimer;
import com.ardor3d.util.geom.BufferUtils;

public abstract class P2TExampleBase extends P2TSimpleExampleBase
{
    protected TriangulationProcess _process;
    protected CDTSweepMesh _cdtSweepMesh;
    protected CDTSweepPoints _cdtSweepPoints;
    
    protected PolygonSet _polygonSet;
    private long _processTimestamp;

    /** Text fields used to present info about the example. */
    protected final BasicText _exampleInfo[] = new BasicText[7];

    public P2TExampleBase( LogicalLayer logicalLayer, FrameHandler frameHandler )
    {
        super( logicalLayer, frameHandler );
    }

    @Override
    protected void initExample()
    {
        super.initExample();

        // Warmup the triangulation code for better performance 
        // when we need triangulation during runtime
//        Poly2Tri.warmup();
        
        _process = new TriangulationProcess(TriangulationAlgorithm.DTSweep);
                        
        _cdtSweepPoints = new CDTSweepPoints();
        _cdtSweepMesh = new CDTSweepMesh();
        _node.attachChild( _cdtSweepPoints.getSceneNode() );        
        _node.attachChild( _cdtSweepMesh.getSceneNode() );        
        
        final Node textNodes = new Node("Text");
        textNodes.getSceneHints().setRenderBucketType(RenderBucketType.Ortho);
        textNodes.getSceneHints().setLightCombineMode(LightCombineMode.Off);
        _root.attachChild( textNodes );
        
        for (int i = 0; i < _exampleInfo.length; i++) 
        {
            _exampleInfo[i] = BasicText.createDefaultTextLabel("Text", "", 16);
            _exampleInfo[i].setTranslation(new Vector3(10, (_exampleInfo.length-i-1) * 20 + 10, 0));
            textNodes.attachChild(_exampleInfo[i]);
        }
        updateText();
    
    }

    protected DTSweepContext getContext()
    {
        return (DTSweepContext)_process.getContext();
    }
    
    /**
     * Update text information.
     */
    protected void updateText() 
    {
        _exampleInfo[0].setText("");
        _exampleInfo[1].setText("[Home] Toggle wireframe");
        _exampleInfo[2].setText("[End] Toggle vertex points");
    }
    
    @Override
    protected void updateExample(final ReadOnlyTimer timer) 
    {
        if( _process.isDone() && _processTimestamp != _process.getTimestamp() )
        {
            _processTimestamp = _process.getTimestamp();
            updateMesh();
            _exampleInfo[0].setText("[" + _process.getTriangulationTime() + "ms] " + _process.getPointCount() + " points" );
        }
    }

    public void exit() 
    {
        super.exit();
        _process.shutdown();
    }

    protected void triangulate()
    {
        _process.triangulate( _polygonSet );
    }
    
    protected void updateMesh()
    {        
        if( _process.getContext().getTriangulatable() != null )
        {
            if( _process.getContext().isDebugEnabled() )
            {
                if( _process.isDone() )
                {
                    _cdtSweepMesh.update( _process.getContext().getTriangulatable().getTriangles() );
                    _cdtSweepPoints.update( _process.getContext().getTriangulatable().getPoints() );                    
                }
                else
                {
                    _cdtSweepMesh.update( _process.getContext().getTriangles() );
                    _cdtSweepPoints.update( _process.getContext().getPoints() );
                }
            }
            else
            {
                _cdtSweepMesh.update( _polygonSet.getPolygons().get(0).getTriangles() );                
                _cdtSweepPoints.update( _polygonSet.getPolygons().get(0).getPoints() );
            }
        }
    }


    @Override
    public void registerInputTriggers()
    {
        super.registerInputTriggers();
        
        _controlHandle.setMoveSpeed( 10 );
                
        // HOME - toogleWireframe
        _logicalLayer.registerTrigger( new InputTrigger( new KeyPressedCondition( Key.HOME ), new TriggerAction() {
            public void perform( final Canvas canvas, final TwoInputStates inputState, final double tpf )
            {
                _cdtSweepMesh.toogleWireframe();
            }
        } ) );          
        
        // END - tooglePoints
        _logicalLayer.registerTrigger( new InputTrigger( new KeyPressedCondition( Key.END ), new TriggerAction() {
            public void perform( final Canvas canvas, final TwoInputStates inputState, final double tpf )
            {
                _cdtSweepPoints.toogleVisibile();                
            }
        } ) );          
    }

    
    protected abstract class SceneElement<A>
    {
        protected Node _node;
        
        public SceneElement(String name)
        {
            _node = new Node(name);
            _node.getSceneHints().setAllPickingHints( false );            
        }

        public abstract void update( A element );

        public Node getSceneNode()
        {
            return _node;
        }
    }

    protected class CDTSweepPoints extends SceneElement<List<TriangulationPoint>>
    {
        private Point m_point = new Point();
        private boolean _pointsVisible = true;
        
        public CDTSweepPoints()
        {
            super("Mesh");

            m_point.setDefaultColor( ColorRGBA.RED );
            m_point.setPointSize( 1 );
            m_point.setTranslation( 0, 0, 0.01 );
            _node.attachChild( m_point );

            MeshData md = m_point.getMeshData();
            int size = 1000;
            FloatBuffer vertBuf = BufferUtils.createFloatBuffer( (int)size*3 );            
            md.setVertexBuffer( vertBuf );
        }        
        
        public void toogleVisibile()
        {
            if( _pointsVisible )
            {
                m_point.removeFromParent();
                _pointsVisible = false;
            }
            else
            {
                _node.attachChild( m_point );
                _pointsVisible = true;
            }
        }
        
        @Override
        public void update( List<TriangulationPoint> list )
        {   
            ArdorMeshMapper.updateVertexBuffer( m_point, list );
        }
    }

    protected class CDTSweepMesh extends SceneElement<List<DelaunayTriangle>>
    {
        private Mesh m_mesh = new Mesh();
        private WireframeState _ws = new WireframeState();
        
        public CDTSweepMesh()
        {
            super("Mesh");

            MeshData md;
            m_mesh.setDefaultColor( ColorRGBA.BLUE );
            m_mesh.setRenderState( _ws );
            _node.attachChild( m_mesh );

            md = m_mesh.getMeshData();
            int size = 1000;
            FloatBuffer vertBuf = BufferUtils.createFloatBuffer( (int)size*3*3 );            
            md.setVertexBuffer( vertBuf );
            md.setIndexMode( IndexMode.Triangles );            
        }        
        
        public void toogleWireframe()
        {
            if( _ws.isEnabled() )
            {
                _ws.setEnabled( false );
            }
            else
            {
                _ws.setEnabled( true );                
            } 
        }
        
        @Override
        public void update( List<DelaunayTriangle> triangles )
        {            
            ArdorMeshMapper.updateTriangleMesh( m_mesh, triangles );
        }
    }

}
