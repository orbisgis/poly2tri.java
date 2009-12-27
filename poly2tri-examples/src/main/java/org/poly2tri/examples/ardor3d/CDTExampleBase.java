package org.poly2tri.examples.ardor3d;

import java.net.URISyntaxException;
import java.nio.FloatBuffer;

import org.lwjgl.opengl.Display;
import org.poly2tri.Poly2Tri;
import org.poly2tri.triangulation.TriangulationProcess;
import org.poly2tri.triangulation.sets.PointSet;
import org.poly2tri.triangulation.sets.PolygonSet;
import org.poly2tri.triangulation.tools.ardor3d.ArdorMeshMapper;

import com.ardor3d.example.ExampleBase;
import com.ardor3d.framework.Canvas;
import com.ardor3d.framework.FrameHandler;
import com.ardor3d.framework.lwjgl.LwjglCanvas;
import com.ardor3d.image.Texture;
import com.ardor3d.image.Image.Format;
import com.ardor3d.input.Key;
import com.ardor3d.input.logical.InputTrigger;
import com.ardor3d.input.logical.KeyPressedCondition;
import com.ardor3d.input.logical.LogicalLayer;
import com.ardor3d.input.logical.TriggerAction;
import com.ardor3d.input.logical.TwoInputStates;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.IndexMode;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.renderer.state.BlendState;
import com.ardor3d.renderer.state.TextureState;
import com.ardor3d.renderer.state.WireframeState;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.MeshData;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Point;
import com.ardor3d.scenegraph.hint.LightCombineMode;
import com.ardor3d.scenegraph.shape.Quad;
import com.ardor3d.ui.text.BasicText;
import com.ardor3d.util.ReadOnlyTimer;
import com.ardor3d.util.TextureManager;
import com.ardor3d.util.geom.BufferUtils;
import com.ardor3d.util.resource.ResourceLocatorTool;
import com.ardor3d.util.resource.SimpleResourceLocator;

public abstract class CDTExampleBase extends ExampleBase
{
    protected TriangulationProcess _process;
    protected CDTSweepMesh _cdtSweepMesh;
    protected CDTSweepPoints _cdtSweepPoints;
    protected Quad _logotype;

    protected Node _node;

    protected int _width,_height;
    
    protected PolygonSet _polygonSet;
    private long _processTimestamp;

    /** Text fields used to present info about the example. */
    protected final BasicText _exampleInfo[] = new BasicText[7];

    public CDTExampleBase( LogicalLayer logicalLayer, FrameHandler frameHandler )
    {
        super( logicalLayer, frameHandler );
    }

    @Override
    protected void initExample()
    {
        _canvas.setVSyncEnabled( true );
        
        _width = Display.getDisplayMode().getWidth();
        _height = Display.getDisplayMode().getHeight();

        try {
            SimpleResourceLocator srl = new SimpleResourceLocator(ExampleBase.class.getClassLoader().getResource("org/poly2tri/examples/data/"));
            ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_MODEL, srl);
            SimpleResourceLocator sr2 = new SimpleResourceLocator(ExampleBase.class.getClassLoader().getResource("org/poly2tri/examples/textures/"));
            ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_TEXTURE, sr2);
        } catch (final URISyntaxException ex) {
            ex.printStackTrace();
        }

        // Warmup the triangulation code for better performance 
        // when we need triangulation during runtime
        Poly2Tri.warmup();
        
        _process = new TriangulationProcess();

        _logotype = new Quad("box", 128, 128 );
        _logotype.setTranslation( 74, _height - 74, 0 );
        _logotype.getSceneHints().setLightCombineMode( LightCombineMode.Off );
        _logotype.getSceneHints().setRenderBucketType( RenderBucketType.Ortho );
        BlendState bs = new BlendState();
        bs.setBlendEnabled( true );
        bs.setEnabled( true );
        bs.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
        bs.setDestinationFunction(BlendState.DestinationFunction.OneMinusSourceAlpha);
        _logotype.setRenderState( bs );
        TextureState ts = new TextureState();
        ts.setEnabled(true);
        ts.setTexture(TextureManager.load("poly2tri_logotype_256x256.png", 
                      Texture.MinificationFilter.Trilinear,
                      Format.GuessNoCompression, true));
        _logotype.setRenderState(ts);
        _root.attachChild( _logotype );

        
        _node = new Node();
        _node.getSceneHints().setLightCombineMode( LightCombineMode.Off );
        _root.attachChild( _node );        
                
        _cdtSweepPoints = new CDTSweepPoints();
        _cdtSweepMesh = new CDTSweepMesh();
        _node.attachChild( _cdtSweepPoints.getSceneNode() );        
        _node.attachChild( _cdtSweepMesh.getSceneNode() );        
        
        _canvas.getCanvasRenderer().getCamera().setLocation(0, 0, 65);
        

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

    /**
     * Update text information.
     */
    protected void updateText() 
    {
        _exampleInfo[0].setText("");
        _exampleInfo[1].setText("[Home] Toggle wireframe");
        _exampleInfo[2].setText("[End] Toggle vertex points");
    }
    
    protected void updateExample(final ReadOnlyTimer timer) 
    {
        if( _process.isDone() && _processTimestamp != _process.getTimestamp() )
        {
            _processTimestamp = _process.getTimestamp();
            updateMesh();
            _exampleInfo[0].setText("[" + _process.getTriangulationTime() + "ms] " + _polygonSet.pointCount() + " points" );
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
        if( _polygonSet != null )
        {
            _cdtSweepMesh.update( _polygonSet );
            _cdtSweepPoints.update( _polygonSet );
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

    
    abstract class SceneElement<A>
    {
        protected Node m_node;
        
        public SceneElement(String name)
        {
            m_node = new Node(name);
            m_node.getSceneHints().setAllPickingHints( false );            
        }

        public abstract void update( A element );

        public Node getSceneNode()
        {
            return m_node;
        }
    }

    class CDTSweepPoints extends SceneElement<PointSet>
    {
        private Point m_point = new Point();
        private boolean _pointsVisible = true;
        
        public CDTSweepPoints()
        {
            super("Mesh");

            m_point.setDefaultColor( ColorRGBA.RED );
            m_point.setPointSize( 1 );
            m_point.setTranslation( 0, 0, 0.01 );
            m_node.attachChild( m_point );

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
                m_node.attachChild( m_point );
                _pointsVisible = true;
            }
        }
        
        @Override
        public void update( PointSet ps )
        {   
            ArdorMeshMapper.updateVertexBuffer( m_point, ps.getPoints() );
        }
    }

    class CDTSweepMesh extends SceneElement<PolygonSet>
    {
        private Mesh m_mesh = new Mesh();
        private WireframeState _ws = new WireframeState();
        
        public CDTSweepMesh()
        {
            super("Mesh");

            MeshData md;
            m_mesh.setDefaultColor( ColorRGBA.BLUE );
            m_mesh.setRenderState( _ws );
            m_node.attachChild( m_mesh );

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
        public void update( PolygonSet ps )
        {            
            if( ps != null )
            {
                ArdorMeshMapper.updateTriangleMesh( m_mesh, ps );
            }
        }
    }

}
