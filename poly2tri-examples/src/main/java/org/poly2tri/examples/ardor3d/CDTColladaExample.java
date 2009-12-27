package org.poly2tri.examples.ardor3d;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import org.poly2tri.examples.ardor3d.base.P2TExampleBase;
import org.poly2tri.examples.ardor3d.misc.MyPoint;
import org.poly2tri.polygon.Polygon;
import org.poly2tri.triangulation.TriangulationPoint;
import org.poly2tri.triangulation.delaunay.sweep.DTSweepConstraint;
import org.poly2tri.triangulation.sets.PolygonSet;
import org.poly2tri.triangulation.tools.ardor3d.ArdorMeshMapper;

import com.ardor3d.extension.model.collada.jdom.ColladaImporter;
import com.ardor3d.extension.model.collada.jdom.data.ColladaStorage;
import com.ardor3d.framework.FrameHandler;
import com.ardor3d.input.logical.LogicalLayer;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.renderer.IndexMode;
import com.ardor3d.renderer.state.WireframeState;
import com.ardor3d.scenegraph.Line;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Node;
import com.google.inject.Inject;

/**
 * This example shows the difference in how Google SketchUp and
 * Poly2Tri triangulates a polygon.
 * <p>
 * A polygon was drawn in SketchUp and exported as Collada.
 * This code loads that triangle mesh and converts it back to a
 * polygon so Poly2Tri can retriangulate it.
 * 
 * @author Thomas Åhlén, thahlen@gmail.com
 *
 */
public class CDTColladaExample extends P2TExampleBase
{
    public static void main(final String[] args) 
    {
        start(CDTColladaExample.class);
    }

    @Inject
    public CDTColladaExample( LogicalLayer logicalLayer, FrameHandler frameHandler )
    {
        super( logicalLayer, frameHandler );
    }

    @Override
    protected void initExample()
    {
        super.initExample();
        
        final ColladaImporter colladaImporter = new ColladaImporter();
        final ColladaStorage storage = colladaImporter.readColladaFile("polygon.dae");
        Node colladaNode = storage.getScene();
        
        Mesh mesh = (Mesh)colladaNode.getChild( "geometry_triangles" );
        mesh.setRenderState( new WireframeState() );        
        mesh.setDefaultColor( ColorRGBA.LIGHT_GRAY );
        mesh.setScale( 0.13 );
        mesh.setTranslation( 0, 12, 0 );
        _node.attachChild(mesh);
                
        _polygonSet = new PolygonSet( meshToPolygon( mesh ) );
        triangulate();
        
        // The ExampleBase implements a Point and Mesh scene object that's get updated
        // from _polygonSet when triangulation process is done
        _cdtSweepMesh.getSceneNode().setScale( 0.13 );
        _cdtSweepMesh.getSceneNode().setTranslation( 0, -12, 0 );
        _cdtSweepPoints.getSceneNode().setScale( 0.13 );
        _cdtSweepPoints.getSceneNode().setTranslation( 0, -12, 0 );

//        Line line = new Line();
//        line.getMeshData().setIndexMode( IndexMode.Lines );
//        line.setDefaultColor( ColorRGBA.GREEN );
//        line.setScale( 0.13 );
//        line.setTranslation( 0, -12, 0.005 );
//        _node.attachChild( line );
//        ArdorMeshMapper.updateVertexBuffer( line, _polygonSet.getPoints() );
//        ArdorMeshMapper.updateIndexBuffer( line, _polygonSet.getEdgeIndex() );        
    }
    
    /**
     * Need to recreate a polygon from a triangulated 2d mesh
     * only support IndexMode.triangles
     * 
     * There are most likely much better ways to recreate a polygon
     * from a triangle mesh but I wanted something quick :) 
     * 
     * @param mesh
     * @return
     */
    private static Polygon meshToPolygon( Mesh mesh )
    {
        ArrayList<DTSweepConstraint> edges;
        HashMap<MyPoint,MyPoint> pointMap = new HashMap<MyPoint,MyPoint>(mesh.getMeshData().getVertexBuffer().limit()/3);

        createConstraints( pointMap, mesh );        

        edges = findOuterEdges( pointMap );

// TODO: was planing in going the final step and rearrange the points so
//       we get an ordered polygon without need for edgeIndexes
//        // Connect the points to the edges
//        for( DTSweepConstraint e : list )
//        {
//            e.p.addEdge( e );
//            e.q.addEdge( e );
//        }
//        // Lets get an ordered polygon
//        TriangulationPoint[] points = new TriangulationPoint[map.values().size()];
//        TriangulationPoint point, nextPoint ;
//        point = p;
//        int i=0;
//        do
//        {
//            points[i++] = p;
//            edge = point.getEdges().get(0);
//            if( p == edge.p )
//            {
//                q = edge.q;
//            }
//            else
//            {
//                
//            }
//            if( nextPoint.getEdges().get(0))
//        } while( nextPoint != firstPoint )
                               
        return createPolygon( pointMap, edges );
    }

    private static void createConstraints( HashMap<MyPoint,MyPoint> pointMap,
                                           Mesh mesh )
    {
        MyPoint p1,p2,p3;
        FloatBuffer fB;
        fB = mesh.getMeshData().getVertexBuffer();
        int size = fB.limit()/3;
        for( int i=0; i<size; i += 3 )
        {
            p1 = new MyPoint( fB.get( 3*i ),
                              fB.get( 3*i+1 ) );
            p1 = find( pointMap, p1 );
            p2 = new MyPoint( fB.get( 3*(i+1) ),
                              fB.get( 3*(i+1)+1 ) );
            p2 = find( pointMap, p2 );
            p3 = new MyPoint( fB.get( 3*(i+2) ),
                              fB.get( 3*(i+2)+1 ) );
            p3 = find( pointMap, p3 );

            new DTSweepConstraint( p1, p2 );
            new DTSweepConstraint( p2, p3 );
            new DTSweepConstraint( p3, p1 );
        }
    }
    
    private static ArrayList<DTSweepConstraint> findOuterEdges( HashMap<MyPoint,MyPoint> pointMap )
    {
        DTSweepConstraint edge;
        boolean outerEdge;
        
        ArrayList<DTSweepConstraint> list = new ArrayList<DTSweepConstraint>();
        for( MyPoint a : pointMap.values() )
        {
            if( a.getEdges() == null )
            {
                // Some points might not have edges since edges are assigned to 
                // the point with highest y value
                continue;
            }

            for(Iterator<DTSweepConstraint> it = a.getEdges().iterator(); it.hasNext(); )
            {
                edge = it.next();
                outerEdge = true;
                for( DTSweepConstraint e : a.getEdges() )
                {
                    if( edge != e )
                    {
                        if( edge.p == e.p && edge.q == e.q )
                        {         
                            outerEdge = false;
                        }
                    }                    
                }
                if( outerEdge )
                {
                    list.add( edge );
                    it.remove();
                }
            }
            a.getEdges().clear();
        }   
        return list;
    }
    
    private static Polygon createPolygon( HashMap<MyPoint,MyPoint> pointMap,
                                          ArrayList<DTSweepConstraint> edges )
    {
        int[] edgeIndex = new int[2*edges.size()];
        int i=0;
        for( DTSweepConstraint e : edges )
        {
            edgeIndex[i++] = pointMap.get( e.q ).getIndex();
            edgeIndex[i++] = pointMap.get( e.p ).getIndex();
        }
        
        TriangulationPoint[] points = new TriangulationPoint[pointMap.values().size()];
        for( MyPoint tp : pointMap.values() )
        {
            points[tp.getIndex()] = tp;
        }
        
        
        return new Polygon( Arrays.asList( points ), edgeIndex );        
    }
    
    private static MyPoint find( HashMap<MyPoint,MyPoint> map, MyPoint p )
    {
        MyPoint a;
        a = map.get( p );
        if( a == null )
        {
            p.setIndex(map.values().size());
            map.put( p, p );
            a = p;
        }
        return a;
    }    
}
