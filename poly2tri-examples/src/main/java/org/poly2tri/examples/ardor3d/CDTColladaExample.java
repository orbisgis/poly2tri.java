/* Poly2Tri
 * Copyright (c) 2009-2010, Poly2Tri Contributors
 * http://code.google.com/p/poly2tri/
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of Poly2Tri nor the names of its contributors may be
 *   used to endorse or promote products derived from this software without specific
 *   prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.poly2tri.examples.ardor3d;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.poly2tri.examples.ardor3d.base.P2TExampleBase;
import org.poly2tri.geometry.polygon.Polygon;
import org.poly2tri.geometry.polygon.PolygonPoint;
import org.poly2tri.geometry.polygon.PolygonSet;
import org.poly2tri.triangulation.delaunay.sweep.DTSweepConstraint;
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

        Line line = new Line();
        line.getMeshData().setIndexMode( IndexMode.LineLoop );
        line.setDefaultColor( ColorRGBA.GREEN );
        line.setScale( 0.13 );
        line.setTranslation( 0, -12, 0.005 );
        _node.attachChild( line );
        ArdorMeshMapper.updateVertexBuffer( line, _polygonSet.getPolygons().get(0).getPoints() );
    }
    
    /**
     * Need to recreate a polygon without holes from a triangulated 2d mesh
     * only support IndexMode.triangles
     * 
     * There are probably much better ways to recreate a polygon from a mesh
     * but this is what I came up with 
     * 
     * @param mesh
     * @return
     */
    private static Polygon meshToPolygon( Mesh mesh )
    {
        ArrayList<DTSweepConstraint> edges;
        HashMap<PolygonPoint,PolygonPoint> pointMap = new HashMap<PolygonPoint,PolygonPoint>(mesh.getMeshData().getVertexBuffer().limit()/3);

        createConstraints( pointMap, mesh );        
        edges = findOuterEdges( pointMap );

        return createPolygon( pointMap, edges );
    }

    private static void createConstraints( HashMap<PolygonPoint,PolygonPoint> pointMap,
                                           Mesh mesh )
    {
        PolygonPoint p1,p2,p3;
        FloatBuffer fB;
        fB = mesh.getMeshData().getVertexBuffer();
        int size = fB.limit()/3;
        for( int i=0; i<size; i += 3 )
        {
            p1 = new PolygonPoint( fB.get( 3*i ),
                                   fB.get( 3*i+1 ) );
            p1 = find( pointMap, p1 );
            p2 = new PolygonPoint( fB.get( 3*(i+1) ),
                                   fB.get( 3*(i+1)+1 ) );
            p2 = find( pointMap, p2 );
            p3 = new PolygonPoint( fB.get( 3*(i+2) ),
                                   fB.get( 3*(i+2)+1 ) );
            p3 = find( pointMap, p3 );

            new DTSweepConstraint( p1, p2 );
            new DTSweepConstraint( p2, p3 );
            new DTSweepConstraint( p3, p1 );
        }
    }
    
    private static ArrayList<DTSweepConstraint> findOuterEdges( HashMap<PolygonPoint,PolygonPoint> pointMap )
    {
        DTSweepConstraint edge;
        boolean outerEdge;
        
        ArrayList<DTSweepConstraint> list = new ArrayList<DTSweepConstraint>();
        for( PolygonPoint a : pointMap.values() )
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
    
    /**
     * TODO: handle holes
     * @param pointMap
     * @param edges
     * @return
     */
    private static Polygon createPolygon( HashMap<PolygonPoint,PolygonPoint> pointMap,
                                          ArrayList<DTSweepConstraint> edges )
    {
        PolygonPoint p,ppQ,ppP=null;
        for( DTSweepConstraint e : edges )
        {
            ppP = (PolygonPoint)e.p; 
            ppQ = (PolygonPoint)e.q;
            if( ppP.getNext() == null )
            {
                ppP.setNext( ppQ );
                if( ppQ.getPrevious() != null )
                {
                    lastToFirst( ppQ );
                }
                ppQ.setPrevious( ppP );
            }
            else
            {
                ppP.setPrevious( ppQ );
                if( ppQ.getNext() != null )
                {
                    firstToLast( ppQ );
                }
                ppQ.setNext( ppP );
            }
        }
        
        ArrayList<PolygonPoint> points = new ArrayList<PolygonPoint>();
        p = ppP;
        do
        {
            points.add( p );
            p = p.getNext();
            
        } while( p != ppP );
        
        return new Polygon( points );        
    }

    private static void lastToFirst( PolygonPoint p )
    {
        PolygonPoint previous;
        previous = p.getPrevious(); 
        p.setNext( previous );
        if( previous != null )
        {
            lastToFirst( previous );
            previous.setPrevious( p );
        }
    }
    
    private static void firstToLast( PolygonPoint p )
    {
        PolygonPoint next;
        next = p.getNext(); 
        p.setPrevious( next );
        if( next != null )
        {
            firstToLast( next );
            next.setNext( p );
        }
    }
    
    private static PolygonPoint find( HashMap<PolygonPoint,PolygonPoint> map, PolygonPoint p )
    {
        PolygonPoint a;
        a = map.get( p );
        if( a == null )
        {
            map.put( p, p );
            a = p;
        }
        return a;
    }    
}
