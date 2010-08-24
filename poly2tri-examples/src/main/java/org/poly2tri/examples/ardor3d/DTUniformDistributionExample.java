package org.poly2tri.examples.ardor3d;

import java.io.IOException;
import java.util.ArrayList;

import org.poly2tri.Poly2Tri;
import org.poly2tri.examples.ardor3d.base.P2TSimpleExampleBase;
import org.poly2tri.examples.ardor3d.misc.DataLoader;
import org.poly2tri.examples.ardor3d.misc.ExampleSets;
import org.poly2tri.triangulation.TriangulationAlgorithm;
import org.poly2tri.triangulation.TriangulationContext;
import org.poly2tri.triangulation.TriangulationPoint;
import org.poly2tri.triangulation.point.TPoint;
import org.poly2tri.triangulation.sets.PointSet;
import org.poly2tri.triangulation.tools.ardor3d.ArdorMeshMapper;
import org.poly2tri.triangulation.util.PointGenerator;

import com.ardor3d.framework.FrameHandler;
import com.ardor3d.input.logical.LogicalLayer;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.renderer.state.WireframeState;
import com.ardor3d.scenegraph.Mesh;
import com.google.inject.Inject;

public class DTUniformDistributionExample extends P2TSimpleExampleBase
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
        super.initExample();

        PointSet ps;
        Mesh mesh;
        
        mesh = new Mesh();
        mesh.setDefaultColor( ColorRGBA.BLUE );
        mesh.setRenderState( new WireframeState() );
        _node.attachChild( mesh );
  
        try
        {
            ps = DataLoader.loadPointSet( ExampleSets.Example2, 0.1 );
            ps = new PointSet( PointGenerator.uniformDistribution( 10000, 60 ) );
            Poly2Tri.triangulate( ps );
            ArdorMeshMapper.updateTriangleMesh( mesh, ps );
        }
        catch( IOException e )
        {}
        
    }
}
