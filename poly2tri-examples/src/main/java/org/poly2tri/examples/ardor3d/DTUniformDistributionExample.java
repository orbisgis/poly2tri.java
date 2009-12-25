package org.poly2tri.examples.ardor3d;

import org.poly2tri.Poly2Tri;
import org.poly2tri.triangulation.sets.PointSet;
import org.poly2tri.triangulation.tools.ardor3d.ArdorMeshMapper;
import org.poly2tri.triangulation.util.PointGenerator;

import com.ardor3d.example.ExampleBase;
import com.ardor3d.framework.FrameHandler;
import com.ardor3d.input.logical.LogicalLayer;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.renderer.state.WireframeState;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.hint.LightCombineMode;
import com.google.inject.Inject;

public class DTUniformDistributionExample extends ExampleBase
{

    public static void main(final String[] args) 
    {
        start(DTUniformDistributionExample.class);
    }

    @Inject
    public DTUniformDistributionExample( LogicalLayer logicalLayer, FrameHandler frameHandler )
    {
        super( logicalLayer, frameHandler );
    }

    @Override
    protected void initExample()
    {       
        _canvas.setVSyncEnabled( true );
        
        _root.getSceneHints().setLightCombineMode( LightCombineMode.Off );
        _root.setRenderState( new WireframeState() );

        Mesh mesh = new Mesh();
        mesh.setDefaultColor( ColorRGBA.BLUE );
        _root.attachChild( mesh );
  
        PointSet ps = new PointSet( PointGenerator.uniformDistribution( 1000, 1000000 ) );
        Poly2Tri.triangulate( ps );
        ArdorMeshMapper.updateTriangleMesh( mesh, ps );
    }
}
