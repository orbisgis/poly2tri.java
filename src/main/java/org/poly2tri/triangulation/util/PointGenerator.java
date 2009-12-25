package org.poly2tri.triangulation.util;

import java.util.ArrayList;

import org.poly2tri.triangulation.TriangulationPoint;
import org.poly2tri.triangulation.point.TPoint;

public class PointGenerator
{
    public static ArrayList<TriangulationPoint> uniformDistribution( double scale, int n )
    {
        ArrayList<TriangulationPoint> points = new ArrayList<TriangulationPoint>();
        for( int i=0; i<n; i++ )
        {
            points.add( new TPoint( scale*(0.5 - Math.random()), scale*(0.5 - Math.random()) ) );
        }
        return points;
    }
}
