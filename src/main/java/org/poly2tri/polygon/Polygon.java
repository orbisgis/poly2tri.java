package org.poly2tri.polygon;

import java.util.Arrays;
import java.util.List;

import org.poly2tri.triangulation.TriangulationPoint;

public class Polygon
{
    protected List<TriangulationPoint> _points;
    protected int[] _index;
    
    public Polygon()
    {
    }

    public Polygon( List<TriangulationPoint> points )
    {
        _points = points;
    }

    public Polygon( List<TriangulationPoint> points, int[] index )
    {
        _points = points;
        _index = index;
    }
    
    public Polygon( TriangulationPoint[] points )
    {        
        _points = Arrays.asList( points );
    }

    public List<TriangulationPoint> getPoints()
    {
        return _points;
    }

    public int[] getIndex()
    {
        return _index;
    }
}
