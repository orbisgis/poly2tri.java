package org.poly2tri.triangulation.point.ardor3d;

import java.util.ArrayList;
import java.util.List;

import org.poly2tri.triangulation.TriangulationPoint;

import com.ardor3d.math.Vector3;

public class ArdorVector3Point extends TriangulationPoint
{
    private final Vector3 _p;
    
    public ArdorVector3Point( Vector3 point )
    {
        _p = point;
    }
    
    public final double getX()
    {
        return _p.getX();
    }
    public final double getY()
    {
        return _p.getY();
    }
    public final double getZ()
    {
        return _p.getZ();
    }
    
    public final float getXf()
    {
        return _p.getXf();
    }
    public final float getYf()
    {
        return _p.getYf();
    }
    public final float getZf()
    {
        return _p.getZf();
    }

    @Override
    public void set( double x, double y, double z )
    {
        _p.set( x, y, z );
    }

    public static List<TriangulationPoint> toPoints( Vector3[] vpoints )
    {
        ArrayList<TriangulationPoint> points = new ArrayList<TriangulationPoint>(vpoints.length);
        for( int i=0; i<vpoints.length; i++ )
        {
            points.add( new ArdorVector3Point(vpoints[i]) );
        }        
        return points;
    }

    public static List<TriangulationPoint> toPoints( ArrayList<Vector3> vpoints )
    {
        int i=0;
        ArrayList<TriangulationPoint> points = new ArrayList<TriangulationPoint>(vpoints.size());
        for( Vector3 point : vpoints )
        {
            points.add( new ArdorVector3Point(point) );
        }        
        return points;
    }

    public static List<TriangulationPoint> toPolygonPoints( Vector3[] vpoints )
    {
        ArrayList<TriangulationPoint> points = new ArrayList<TriangulationPoint>(vpoints.length);
        for( int i=0; i<vpoints.length; i++ )
        {
            points.add( new ArdorVector3PolygonPoint(vpoints[i]) );
        }        
        return points;
    }

    public static List<TriangulationPoint> toPolygonPoints( ArrayList<Vector3> vpoints )
    {
        int i=0;
        ArrayList<TriangulationPoint> points = new ArrayList<TriangulationPoint>(vpoints.size());
        for( Vector3 point : vpoints )
        {
            points.add( new ArdorVector3PolygonPoint(point) );
        }        
        return points;
    }
}
