package org.poly2tri.examples.ardor3d;

import java.util.concurrent.Callable;

import org.poly2tri.examples.ardor3d.base.P2TSimpleExampleBase;
import org.poly2tri.triangulation.Triangulatable;
import org.poly2tri.triangulation.TriangulationProcess;
import org.poly2tri.triangulation.TriangulationProcessEvent;
import org.poly2tri.triangulation.TriangulationProcessListener;
import org.poly2tri.triangulation.tools.ardor3d.ArdorMeshMapper;
import org.poly2tri.triangulation.util.PolygonGenerator;

import com.ardor3d.framework.FrameHandler;
import com.ardor3d.input.logical.LogicalLayer;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.renderer.state.WireframeState;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.util.GameTaskQueueManager;
import com.ardor3d.util.ReadOnlyTimer;
import com.ardor3d.util.geom.BufferUtils;
import com.google.inject.Inject;

/**
 * Example of how to use threaded triangulation 
 * and listening
 * 
 * @author Thomas Åhlén, thahlen@gmail.com
 *
 */
public class CDTProcessExample extends P2TSimpleExampleBase implements TriangulationProcessListener
{
    private TriangulationProcess _process;
    private Mesh _mesh;
    private int count;
    
    public static void main(final String[] args) 
    {
        start(CDTProcessExample.class);
    }

    @Inject
    public CDTProcessExample( LogicalLayer logicalLayer, FrameHandler frameHandler )
    {
        super( logicalLayer, frameHandler );
    }

    @Override
    protected void updateExample(final ReadOnlyTimer timer) 
    {
        if( count%100 == 0 )
        {
            _process.triangulate( PolygonGenerator.RandomCircleSweep( 50, 100 ) );
        }
        count++;
    }

    @Override
    protected void initExample()
    {
        super.initExample();
        
        Node node = new Node();
        node.setRenderState( new WireframeState() );
        _node.attachChild( node );
        
        _process = new TriangulationProcess();
        _process.addListener( this );
        
        _mesh = new Mesh();
        _mesh.setDefaultColor( ColorRGBA.BLUE );
        _mesh.getMeshData().setVertexBuffer( BufferUtils.createFloatBuffer( 0 ) );
        node.attachChild( _mesh );  
    }
    
    @Override
    public void triangulationEvent( TriangulationProcessEvent e, final Triangulatable unit )
    {
        GameTaskQueueManager manager;
        if( e == TriangulationProcessEvent.Done )
        {
            final Mesh mesh = _mesh;

            manager = GameTaskQueueManager.getManager(_canvas.getCanvasRenderer().getRenderContext());
            Callable<Object> callable = new Callable<Object>() 
            {
                @Override
                public Object call() throws Exception
                {
                    ArdorMeshMapper.updateTriangleMesh( mesh, unit.getTriangles() );
                    return null;
                }            
            };
            manager.update( callable );
        }
    }
}
