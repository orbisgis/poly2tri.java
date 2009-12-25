package org.poly2tri.polygon.ardor3d;

import java.util.ArrayList;

import org.poly2tri.polygon.Polygon;
import org.poly2tri.triangulation.point.ardor3d.ArdorVector3Point;

import com.ardor3d.math.Vector3;

public class ArdorPolygon extends Polygon
{
    public ArdorPolygon( Vector3[] points, int[] index )
    {
        super( ArdorVector3Point.toPoints( points ), index );
    }

    public ArdorPolygon( Vector3[] points )
    {
        super( ArdorVector3Point.toPoints( points ), null );
    }

    public ArdorPolygon( ArrayList<Vector3> points, int[] index )
    {
        super( ArdorVector3Point.toPoints( points ), index );
    }

    public ArdorPolygon( ArrayList<Vector3> points )
    {
        super( ArdorVector3Point.toPoints( points ), null );
    }
}
