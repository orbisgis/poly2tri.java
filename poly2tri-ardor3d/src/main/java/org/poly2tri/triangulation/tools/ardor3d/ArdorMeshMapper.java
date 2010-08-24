package org.poly2tri.triangulation.tools.ardor3d;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.List;

import org.poly2tri.geometry.polygon.Polygon;
import org.poly2tri.geometry.primitives.Point;
import org.poly2tri.transform.coordinate.CoordinateTransform;
import org.poly2tri.transform.coordinate.NoTransform;
import org.poly2tri.triangulation.TriangulationPoint;
import org.poly2tri.triangulation.delaunay.DelaunayTriangle;
import org.poly2tri.triangulation.point.TPoint;
import org.poly2tri.triangulation.sets.PointSet;

import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.IndexMode;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.util.geom.BufferUtils;

public class ArdorMeshMapper
{
    private static final CoordinateTransform _ct = new NoTransform();
    
    public static void updateTriangleMesh( Mesh mesh, 
                                           List<DelaunayTriangle> triangles )
    {
        updateTriangleMesh( mesh, triangles, _ct );
    }
    
    public static void updateTriangleMesh( Mesh mesh, 
                                           List<DelaunayTriangle> triangles,
                                           CoordinateTransform pc )
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
    
    public static void updateFaceNormals( Mesh mesh,
                                            List<DelaunayTriangle> triangles )
    {
        updateFaceNormals( mesh, triangles, _ct );
    }

    public static void updateFaceNormals( Mesh mesh,
                                          List<DelaunayTriangle> triangles,
                                          CoordinateTransform pc )
    {
        FloatBuffer nBuf;
        Vector3 fNormal;
        HashMap<DelaunayTriangle,Vector3> fnMap;
        int size = 3*3*triangles.size();

        fnMap = calculateFaceNormals( triangles, pc );
        
        prepareNormalBuffer( mesh, size );
        nBuf = mesh.getMeshData().getNormalBuffer();
        nBuf.rewind();        
        for( DelaunayTriangle t : triangles )
        {
            fNormal = fnMap.get( t );
            for( int i=0; i<3; i++ )
            {
                nBuf.put(fNormal.getXf());
                nBuf.put(fNormal.getYf());
                nBuf.put(fNormal.getZf());
            }
        }        
    }

    public static void updateVertexNormals( Mesh mesh,
                                            List<DelaunayTriangle> triangles )
    {
        updateVertexNormals( mesh, triangles, _ct );
    }

    public static void updateVertexNormals( Mesh mesh,
                                            List<DelaunayTriangle> triangles,
                                            CoordinateTransform pc )
    {
        FloatBuffer nBuf;
        TriangulationPoint vertex;
        Vector3 vNormal, fNormal;
        HashMap<DelaunayTriangle,Vector3> fnMap;
        HashMap<TriangulationPoint,Vector3> vnMap = new HashMap<TriangulationPoint,Vector3>(3*triangles.size());
        int size = 3*3*triangles.size();

        fnMap = calculateFaceNormals( triangles, pc );
        
        // Calculate a vertex normal for each vertex
        for( DelaunayTriangle t : triangles )
        {
            fNormal = fnMap.get( t );
            for( int i=0; i<3; i++ )
            {
                vertex = t.points[i];
                vNormal = vnMap.get( vertex );
                if( vNormal == null )
                {
                    vNormal = new Vector3( fNormal.getX(), fNormal.getY(), fNormal.getZ() );
                    vnMap.put( vertex, vNormal );
                }
                else
                {
                    vNormal.addLocal( fNormal );
                }
            }
        }

        // Normalize all normals
        for( Vector3 normal : vnMap.values() )
        {
            normal.normalizeLocal();
        }
        
        prepareNormalBuffer( mesh, size );
        nBuf = mesh.getMeshData().getNormalBuffer();
        nBuf.rewind();        
        for( DelaunayTriangle t : triangles )
        {
            for( int i=0; i<3; i++ )
            {
                vertex = t.points[i];
                vNormal = vnMap.get( vertex );
                nBuf.put(vNormal.getXf());
                nBuf.put(vNormal.getYf());
                nBuf.put(vNormal.getZf());
            }
        }
    }
    
    private static HashMap<DelaunayTriangle,Vector3> calculateFaceNormals( List<DelaunayTriangle> triangles,
                                                                          CoordinateTransform pc )
    {
        HashMap<DelaunayTriangle,Vector3> normals = new HashMap<DelaunayTriangle,Vector3>(triangles.size());
        TPoint point = new TPoint(0,0,0);
        
        // calculate the Face Normals for all triangles
        float x1,x2,x3,y1,y2,y3,z1,z2,z3,nx,ny,nz,ux,uy,uz,vx,vy,vz;
        double norm;

        for( DelaunayTriangle t : triangles )
        {
            pc.transform( t.points[0], point );
            x1 = point.getXf();
            y1 = point.getYf();
            z1 = point.getZf();
            pc.transform( t.points[1], point );
            x2 = point.getXf();
            y2 = point.getYf();
            z2 = point.getZf();
            pc.transform( t.points[2], point );
            x3 = point.getXf();
            y3 = point.getYf();
            z3 = point.getZf();

            ux = x2 - x1;
            uy = y2 - y1;
            uz = z2 - z1;
            vx = x3 - x1;
            vy = y3 - y1;
            vz = z3 - z1;

            nx = uy*vz - uz*vy;
            ny = uz*vx - ux*vz;
            nz = ux*vy - uy*vx;
            norm = 1/Math.sqrt( nx*nx + ny*ny + nz*nz );
            nx *= norm;
            ny *= norm;
            nz *= norm;
            normals.put( t, new Vector3(nx,ny,nz) );
        }
        return normals;        
    }
    
    /**
     * For now very primitive!
     * 
     * Assumes a single texture and that the triangles form a xy-monotone surface
     * <p>
     * A continuous surface S in R3 is called xy-monotone, if every line parallel 
     * to the z-axis intersects it at a single point at most.
     * 
     * @param mesh
     * @param scale
     */
    public static void updateTextureCoordinates( Mesh mesh, 
                                                 List<DelaunayTriangle> triangles, 
                                                 double scale, 
                                                 double angle )
    {
        TriangulationPoint vertex;
        FloatBuffer tcBuf;
        float width,maxX,minX,maxY,minY,x,y,xn,yn;

        maxX = Float.NEGATIVE_INFINITY;
        minX = Float.POSITIVE_INFINITY;
        maxY = Float.NEGATIVE_INFINITY;
        minY = Float.POSITIVE_INFINITY;
        
        for( DelaunayTriangle t : triangles )
        {
            for( int i=0; i<3; i++ )
            {
                vertex = t.points[i];
                x = vertex.getXf();
                y = vertex.getYf();
                
                maxX = x > maxX ? x : maxX; 
                minX = x < minX ? x : minX; 
                maxY = y > maxY ? y : maxY; 
                minY = y < minY ? x : minY; 
            }
        }
        
        width = maxX-minX > maxY-minY ? maxX-minX : maxY-minY;
        width *= scale;
        
        tcBuf = prepareTextureCoordinateBuffer(mesh,0,2*3*triangles.size());
        tcBuf.rewind();
        
        for( DelaunayTriangle t : triangles )
        {
            for( int i=0; i<3; i++ )
            {
                vertex = t.points[i];
                x = vertex.getXf()-(maxX-minX)/2;
                y = vertex.getYf()-(maxY-minY)/2;

                xn = (float)(x*Math.cos(angle)-y*Math.sin(angle));
                yn = (float)(y*Math.cos(angle)+x*Math.sin(angle));
                tcBuf.put( xn/width );
                tcBuf.put( yn/width );
            }
        }
    }
    
    /**
     * Assuming:
     * 1. That given U anv V aren't collinear.
     * 2. That O,U and V can be projected in the XY plane 
     * 
     * @param mesh
     * @param triangles
     * @param o
     * @param u
     * @param v
     */
    public static void updateTextureCoordinates( Mesh mesh, 
                                                 List<DelaunayTriangle> triangles,
                                                 double scale,
                                                 Point o,
                                                 Point u, 
                                                 Point v )
    {
        FloatBuffer tcBuf;
        float x,y,a,b;        
        final float ox = (float)scale*o.getXf();
        final float oy = (float)scale*o.getYf();
        final float ux = (float)scale*u.getXf();
        final float uy = (float)scale*u.getYf();
        final float vx = (float)scale*v.getXf();
        final float vy = (float)scale*v.getYf();
        final float vCu = (vx*uy-vy*ux);
        final boolean doX = Math.abs( ux ) > Math.abs( uy );
        
        tcBuf = prepareTextureCoordinateBuffer(mesh,0,2*3*triangles.size());
        tcBuf.rewind();

        for( DelaunayTriangle t : triangles )
        {
            for( int i=0; i<3; i++ )
            {
                x = t.points[i].getXf()-ox;
                y = t.points[i].getYf()-oy;

                // Calculate the texture coordinate in the UV plane
                a = (uy*x - ux*y)/vCu;                
                if( doX )
                {
                    b = (x - a*vx)/ux;
                }
                else
                {
                    b = (y - a*vy)/uy;                    
                }                
                tcBuf.put( a );
                tcBuf.put( b );
            }
        }
    }

    //    FloatBuffer vBuf,tcBuf;
//    float width,maxX,minX,maxY,minY,x,y,xn,yn;
//
//    maxX = Float.NEGATIVE_INFINITY;
//    minX = Float.POSITIVE_INFINITY;
//    maxY = Float.NEGATIVE_INFINITY;
//    minY = Float.POSITIVE_INFINITY;
//    
//    vBuf = mesh.getMeshData().getVertexBuffer();
//    for( int i=0; i < vBuf.limit()-1; i+=3 )
//    {
//        x = vBuf.get(i);
//        y = vBuf.get(i+1);
//        
//        maxX = x > maxX ? x : maxX; 
//        minX = x < minX ? x : minX; 
//        maxY = y > maxY ? y : maxY; 
//        minY = y < minY ? x : minY; 
//    }
//    
//    width = maxX-minX > maxY-minY ? maxX-minX : maxY-minY;
//    width *= scale;
//    
//    vBuf = mesh.getMeshData().getVertexBuffer();
//    tcBuf = prepareTextureCoordinateBuffer(mesh,0,2*vBuf.limit()/3);
//    tcBuf.rewind();
//    
//    for( int i=0; i < vBuf.limit()-1; i+=3 )
//    {
//        x = vBuf.get(i)-(maxX-minX)/2;
//        y = vBuf.get(i+1)-(maxY-minY)/2;
//        xn = (float)(x*Math.cos(angle)-y*Math.sin(angle));
//        yn = (float)(y*Math.cos(angle)+x*Math.sin(angle));
//        tcBuf.put( xn/width );
//        tcBuf.put( yn/width );
//    }

    public static void updateTriangleMesh( Mesh mesh, PointSet ps, CoordinateTransform pc )
    {
        updateTriangleMesh( mesh, ps.getTriangles(), pc );
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

    public static void updateTriangleMesh( Mesh mesh, Polygon p, CoordinateTransform pc )
    {
        updateTriangleMesh( mesh, p.getTriangles(), pc );
    }

    public static void updateVertexBuffer( Mesh mesh, List<? extends Point> list )
    {
        FloatBuffer vertBuf;
        
        int size = 3*list.size();                
        prepareVertexBuffer( mesh, size );
        
        if( size == 0 )
        {
            return;
        }
        vertBuf = mesh.getMeshData().getVertexBuffer();
        vertBuf.rewind();
        for( Point p : list )
        {
            vertBuf.put(p.getXf()).put(p.getYf()).put(p.getZf());
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
            mesh.getMeshData().updateVertexCount();
        }        
    }

    private static void prepareNormalBuffer( Mesh mesh, int size )
    {
        FloatBuffer nBuf;

        nBuf = mesh.getMeshData().getNormalBuffer();        
        
        if( nBuf == null || nBuf.capacity() < size )
        {
            nBuf = BufferUtils.createFloatBuffer( size );
            mesh.getMeshData().setNormalBuffer( nBuf );
        }
        else
        {
            nBuf.limit( size );
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
    
    private static FloatBuffer prepareTextureCoordinateBuffer( Mesh mesh, int index, int size )
    {
        FloatBuffer tcBuf;

        tcBuf = mesh.getMeshData().getTextureBuffer( index );
        
        if( tcBuf == null || tcBuf.capacity() < size )
        {
            tcBuf = BufferUtils.createFloatBuffer( size );
            mesh.getMeshData().setTextureBuffer( tcBuf, index );
        }
        else
        {
            tcBuf.limit( size );
        }        
        return tcBuf;
    }
}
