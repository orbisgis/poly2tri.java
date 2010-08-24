package org.poly2tri.polygon.ardor3d;

import java.util.ArrayList;

import org.poly2tri.geometry.polygon.Polygon;
import org.poly2tri.triangulation.point.ardor3d.ArdorVector3Point;
import org.poly2tri.triangulation.point.ardor3d.ArdorVector3PolygonPoint;

import com.ardor3d.math.Vector3;

public class ArdorPolygon extends Polygon
{
    public ArdorPolygon( Vector3[] points )
    {
        super( ArdorVector3PolygonPoint.toPoints( points ) );
    }

    public ArdorPolygon( ArrayList<Vector3> points )
    {
        super( ArdorVector3PolygonPoint.toPoints( points ) );
    }
}
