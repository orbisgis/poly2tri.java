package org.poly2tri.examples.ardor3d;

import org.poly2tri.Poly2Tri;
import org.poly2tri.examples.ardor3d.base.P2TSimpleExampleBase;
import org.poly2tri.polygon.Polygon;
import org.poly2tri.polygon.PolygonPoint;
import org.poly2tri.triangulation.point.TPoint;
import org.poly2tri.triangulation.tools.ardor3d.ArdorMeshMapper;
import org.poly2tri.triangulation.util.PolygonGenerator;

import com.ardor3d.framework.FrameHandler;
import com.ardor3d.input.logical.LogicalLayer;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.renderer.state.WireframeState;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Node;
import com.google.inject.Inject;

public class CDTSteinerPointExample extends P2TSimpleExampleBase
{

    public static void main(final String[] args) 
    {
        start(CDTSteinerPointExample.class);
    }

    @Inject
    public CDTSteinerPointExample( LogicalLayer logicalLayer, FrameHandler frameHandler )
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
        Polygon poly;
        
        poly = createCirclePolygon( 32, 1.5 );

        // top left
        Mesh mesh = new Mesh();
        mesh.setDefaultColor( ColorRGBA.BLUE );
        mesh.setTranslation( -2, 2, 0 );
        node.attachChild( mesh );
  
        Poly2Tri.triangulate( poly );
        ArdorMeshMapper.updateTriangleMesh( mesh, poly );

        // bottom left
        mesh = new Mesh();
        mesh.setDefaultColor( ColorRGBA.RED );
        mesh.setTranslation( -2, -2, 0 );
        node.attachChild( mesh );

        poly.addSteinerPoint( new TPoint(0,0) );
        Poly2Tri.triangulate( poly );
        ArdorMeshMapper.updateTriangleMesh( mesh, poly );

        poly = PolygonGenerator.RandomCircleSweep2( 4, 200 );

        // top right
        mesh = new Mesh();
        mesh.setDefaultColor( ColorRGBA.BLUE );
        mesh.setTranslation( 2, 2, 0 );
        node.attachChild( mesh );

        Poly2Tri.triangulate( poly );
        ArdorMeshMapper.updateTriangleMesh( mesh, poly );

        // bottom right
        mesh = new Mesh();
        mesh.setDefaultColor( ColorRGBA.RED );
        mesh.setTranslation( 2, -2, 0 );
        node.attachChild( mesh );

        poly.addSteinerPoint( new TPoint(0,0) );
        Poly2Tri.triangulate( poly );
        ArdorMeshMapper.updateTriangleMesh( mesh, poly );

    }

    private Polygon createCirclePolygon( int n, double radius )
    {
        if( n < 3 ) n=3;
        
        PolygonPoint[] points = new PolygonPoint[n];
        for( int i=0; i<n; i++ )
        {
            points[i] = new PolygonPoint( radius*Math.cos( (2.0*Math.PI*i)/n ),
                                          radius*Math.sin( (2.0*Math.PI*i)/n ) );            
        }
        return new Polygon( points );
    }
}
