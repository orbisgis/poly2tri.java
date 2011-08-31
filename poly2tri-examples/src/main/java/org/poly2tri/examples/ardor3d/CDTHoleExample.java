package org.poly2tri.examples.ardor3d;

import java.util.ArrayList;

import org.poly2tri.Poly2Tri;
import org.poly2tri.examples.ardor3d.base.P2TSimpleExampleBase;
import org.poly2tri.geometry.polygon.Polygon;
import org.poly2tri.geometry.polygon.PolygonPoint;
import org.poly2tri.triangulation.tools.ardor3d.ArdorMeshMapper;

import com.ardor3d.framework.FrameHandler;
import com.ardor3d.input.logical.LogicalLayer;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.renderer.state.WireframeState;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Node;
import com.google.inject.Inject;

public class CDTHoleExample extends P2TSimpleExampleBase
{

    public static void main(final String[] args) 
    {
        start(CDTHoleExample.class);
    }

    @Inject
    public CDTHoleExample( LogicalLayer logicalLayer, FrameHandler frameHandler )
    {
        super( logicalLayer, frameHandler );
    }

    @Override
    protected void initExample()
    {
        super.initExample();
        
        Node node = new Node();
        node.setRenderState( new WireframeState() );
        _node.attachChild( node );
        Polygon circle;
        Polygon hole;
        
        circle = createCirclePolygon( 64, 25, 1 );
        hole = createCirclePolygon( 32, 25, 0.25, -0.5, -0.5 );
        circle.addHole( hole );
        hole = createCirclePolygon( 64, 25, 0.5,  0.25, 0.25 );
        circle.addHole( hole );

        Mesh mesh = new Mesh();
        mesh.setDefaultColor( ColorRGBA.RED );
        mesh.setTranslation( 0, 0, 0.01 );
        node.attachChild( mesh );

        Mesh mesh2 = new Mesh();
        mesh2.setDefaultColor( ColorRGBA.BLUE );
        _node.attachChild( mesh2 );
  
        Poly2Tri.triangulate( circle );
        ArdorMeshMapper.updateTriangleMesh( mesh, circle );
        ArdorMeshMapper.updateTriangleMesh( mesh2, circle );
    }

    private Polygon createCirclePolygon( int n, double scale, double radius )
    {
        return createCirclePolygon( n, scale, radius, 0, 0 );
    }
    
    private Polygon createCirclePolygon( int n, 
                                         double scale, 
                                         double radius, 
                                         double x, 
                                         double y )
    {
        if( n < 3 ) n=3;
        
        PolygonPoint[] points = new PolygonPoint[n];
        for( int i=0; i<n; i++ )
        {
            points[i] = new PolygonPoint( scale*(x + radius*Math.cos( (2.0*Math.PI*i)/n )),
                                          scale*(y + radius*Math.sin( (2.0*Math.PI*i)/n ) ));            
        }
        return new Polygon( points );
    }
}
