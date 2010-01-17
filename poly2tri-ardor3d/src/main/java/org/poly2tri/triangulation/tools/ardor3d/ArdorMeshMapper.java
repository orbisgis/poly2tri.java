package org.poly2tri.triangulation.tools.ardor3d;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import org.poly2tri.polygon.Polygon;
import org.poly2tri.position.CoordinateTransformer;
import org.poly2tri.position.NoTransform;
import org.poly2tri.triangulation.TriangulationPoint;
import org.poly2tri.triangulation.delaunay.DelaunayTriangle;
import org.poly2tri.triangulation.point.TPoint;
import org.poly2tri.triangulation.sets.PointSet;

import com.ardor3d.renderer.IndexMode;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.util.geom.BufferUtils;

public class ArdorMeshMapper
{
    private static final CoordinateTransformer _pc = new NoTransform();
    
    public static void updateTriangleMesh( Mesh mesh, 
                                           List<DelaunayTriangle> triangles )
    {
        updateTriangleMesh( mesh, triangles, _pc );
    }
    
    public static void updateTriangleMesh( Mesh mesh, 
                                           List<DelaunayTriangle> triangles,
                                           CoordinateTransformer pc )
    {
        FloatBuffer vertBuf;
        TPoint point;

        mesh.getMeshData().setIndexMode( IndexMode.Triangles );

        if( triangles == null || triangles.size() == 0 )
        {
            return;
        }
        
        point = new TPoint(0,0,0);
        
        int size = 3*3*triangles.size();
        prepareVertexBuffer( mesh, size );

        vertBuf = mesh.getMeshData().getVertexBuffer();
        vertBuf.rewind();
        for( DelaunayTriangle t : triangles )
        {
            for( int i=0; i<3; i++ )
            {
                pc.transform( t.points[i], point );
                vertBuf.put(point.getXf());
                vertBuf.put(point.getYf());
                vertBuf.put(point.getZf());
            }
        }
    }
    
    /**
     * Will populate a given Mesh's vertex,index buffer and set IndexMode.Triangles<br>
     * Will also increase buffer sizes if needed by creating new buffers.
     * 
     * @param mesh
     * @param ps
     */
    public static void updateTriangleMesh( Mesh mesh, PointSet ps )
    {
        updateTriangleMesh( mesh, ps.getTriangles() );
    }

    public static void updateTriangleMesh( Mesh mesh, Polygon p )
    {
        updateTriangleMesh( mesh, p.getTriangles() );
    }

    public static void updateTriangleMesh( Mesh mesh, Polygon p, CoordinateTransformer pc )
    {
        updateTriangleMesh( mesh, p.getTriangles(), pc );
    }

    public static void updateVertexBuffer( Mesh mesh, List<TriangulationPoint> list )
    {
        FloatBuffer vertBuf;

        if( list.isEmpty() )
        {
            return;
        }
        
        int size = 3*list.size();                
        prepareVertexBuffer( mesh, size );
        
        vertBuf = mesh.getMeshData().getVertexBuffer();
        vertBuf.rewind();
        for( TriangulationPoint p : list )
        {
            vertBuf.put(p.getXf()).put(p.getYf()).put(0);
        }
    }
    
    public static void updateIndexBuffer( Mesh mesh, int[] index )
    {
        IntBuffer indexBuf;

        if( index == null || index.length == 0 )
        {
            return;
        }
                
        int size = index.length;
        prepareIndexBuffer( mesh, size );
        
        indexBuf = mesh.getMeshData().getIndexBuffer();
        indexBuf.rewind();
        for( int i=0; i<size; i+=2 )
        {
            indexBuf.put(index[i]).put( index[i+1] );
        }
    }

    private static void prepareVertexBuffer( Mesh mesh, int size )
    {
        FloatBuffer vertBuf;

        vertBuf = mesh.getMeshData().getVertexBuffer();        
        
        if( vertBuf == null || vertBuf.capacity() < size )
        {
            vertBuf = BufferUtils.createFloatBuffer( size );
            mesh.getMeshData().setVertexBuffer( vertBuf );
        }
        else
        {
            vertBuf.limit( size );
        }        
    }

    private static void prepareIndexBuffer( Mesh mesh, int size)
    {
        IntBuffer indexBuf = mesh.getMeshData().getIndexBuffer();

        if( indexBuf == null || indexBuf.capacity() < size )
        {
            indexBuf = BufferUtils.createIntBuffer( size );
            mesh.getMeshData().setIndexBuffer( indexBuf );
        }
        else
        {
            indexBuf.limit( size );
        }        
    }
}
