package org.poly2tri.examples.ardor3d;

import java.util.List;

import org.poly2tri.Poly2Tri;
import org.poly2tri.examples.ardor3d.base.P2TSimpleExampleBase;
import org.poly2tri.triangulation.TriangulationPoint;
import org.poly2tri.triangulation.point.TPoint;
import org.poly2tri.triangulation.sets.ConstrainedPointSet;
import org.poly2tri.triangulation.tools.ardor3d.ArdorMeshMapper;
import org.poly2tri.triangulation.util.PointGenerator;

import com.ardor3d.framework.FrameHandler;
import com.ardor3d.input.logical.LogicalLayer;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.scenegraph.Mesh;
import com.google.inject.Inject;

public class CDTUniformDistributionExample extends P2TSimpleExampleBase
{

    public static void main(final String[] args) 
    {
        start(CDTUniformDistributionExample.class);
    }

    @Inject
    public CDTUniformDistributionExample( LogicalLayer logicalLayer, FrameHandler frameHandler )
    {
        super( logicalLayer, frameHandler );
    }

    @Override
    protected void initExample()
    {       
        super.initExample();

        Mesh mesh = new Mesh();
        mesh.setDefaultColor( ColorRGBA.BLUE );
        _node.attachChild( mesh );
  
        double scale = 100;
        int size = 1000;
        int index = (int)(Math.random()*size);
        List<TriangulationPoint> points = PointGenerator.uniformDistribution( size, scale );

        // Lets add a constraint that cuts the uniformDistribution in half
        points.add( new TPoint(0,scale/2) );
        points.add( new TPoint(0,-scale/2) );
        index = size; 
        
        ConstrainedPointSet cps = new ConstrainedPointSet( points, new int[]{ index, index+1 } );
        Poly2Tri.triangulate( cps );
        ArdorMeshMapper.updateTriangleMesh( mesh, cps );
    }
}
