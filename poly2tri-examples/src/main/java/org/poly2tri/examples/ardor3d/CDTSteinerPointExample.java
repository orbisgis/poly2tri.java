package org.poly2tri.examples.ardor3d;

import java.util.ArrayList;

import org.poly2tri.Poly2Tri;
import org.poly2tri.examples.ardor3d.base.P2TSimpleExampleBase;
import org.poly2tri.geometry.polygon.Polygon;
import org.poly2tri.geometry.polygon.PolygonPoint;
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

        Mesh mesh;
        Node node = new Node();
        node.setRenderState( new WireframeState() );
        _node.attachChild( node );
        Polygon poly;
        
        poly = createSquare();
        mesh = new Mesh();
        mesh.setDefaultColor( ColorRGBA.GREEN );
        mesh.setTranslation( 0, 0, 0 );
        node.attachChild( mesh );

        Poly2Tri.triangulate( poly );
        ArdorMeshMapper.updateTriangleMesh( mesh, poly );
    }

    private Polygon createSquare()
    {
    	int r = 25, n = 500, m = 50;
    	
        PolygonPoint[] points = new PolygonPoint[n];
        for( int i=0; i<n; i++ )
        {
            points[i] = new PolygonPoint( r*Math.cos( (2.0*Math.PI*i)/n ),
                                          r*Math.sin( (2.0*Math.PI*i)/n ) );            
        }

        Polygon p = new Polygon(points);
        double w,dx,dy;
//        w = 2*r*Math.cos(0.25*Math.PI);
        w = 2*r;
        dx = w/(m+1);
        dy = w/(m+1);
        for( int j=0; j<m; ++j )
        {
            for( int i=0; i<m; ++i )
            {
            	p.addSteinerPoint( new TPoint( dx*(i+1) - 0.5*w, dy*(j+1) - 0.5*w ) );
            }
        }
        return p;
    }
}
