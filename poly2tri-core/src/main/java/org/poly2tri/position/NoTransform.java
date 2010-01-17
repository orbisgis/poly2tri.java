package org.poly2tri.position;

import org.poly2tri.triangulation.TriangulationPoint;

public class NoTransform implements CoordinateTransformer
{

    @Override
    public double toX( TriangulationPoint p )
    {
        return p.getX();
    }

    @Override
    public double toY( TriangulationPoint p )
    {
        return p.getY();
    }

    @Override
    public double toZ( TriangulationPoint p )
    {
        return p.getZ();
    }

    @Override
    public float toXf( TriangulationPoint p )
    {
        return p.getXf();
    }

    @Override
    public float toYf( TriangulationPoint p )
    {
        return p.getYf();
    }

    @Override
    public float toZf( TriangulationPoint p )
    {
        return p.getZf();
    }

    @Override
    public void transform( TriangulationPoint p, TriangulationPoint store )
    {
        store.set( p.getX(), p.getY(), p.getZ() );
    }

    @Override
    public void transform( TriangulationPoint p )
    {
    }
}
