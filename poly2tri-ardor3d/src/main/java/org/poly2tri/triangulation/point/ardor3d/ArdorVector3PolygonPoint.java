package org.poly2tri.triangulation.point.ardor3d;

import java.util.ArrayList;
import java.util.List;

import org.poly2tri.geometry.polygon.PolygonPoint;
import org.poly2tri.triangulation.TriangulationPoint;

import com.ardor3d.math.Vector3;

public class ArdorVector3PolygonPoint extends PolygonPoint
{
    private final Vector3 _p;
    
    public ArdorVector3PolygonPoint( Vector3 point )
    {
        super( point.getX(), point.getY() );
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

    public static List<PolygonPoint> toPoints( Vector3[] vpoints )
    {
        ArrayList<PolygonPoint> points = new ArrayList<PolygonPoint>(vpoints.length);
        for( int i=0; i<vpoints.length; i++ )
        {
            points.add( new ArdorVector3PolygonPoint(vpoints[i]) );
        }        
        return points;
    }

    public static List<PolygonPoint> toPoints( ArrayList<Vector3> vpoints )
    {
        int i=0;
        ArrayList<PolygonPoint> points = new ArrayList<PolygonPoint>(vpoints.size());
        for( Vector3 point : vpoints )
        {
            points.add( new ArdorVector3PolygonPoint(point) );
        }        
        return points;
    }
}
