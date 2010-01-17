package org.poly2tri.position;

import org.poly2tri.triangulation.TriangulationPoint;

public abstract interface CoordinateTransformer
{
    public abstract double toX( TriangulationPoint p );
    public abstract double toY( TriangulationPoint p );
    public abstract double toZ( TriangulationPoint p );    
    public abstract float toXf( TriangulationPoint p );
    public abstract float toYf( TriangulationPoint p );
    public abstract float toZf( TriangulationPoint p );
    
    public abstract void transform( TriangulationPoint p, TriangulationPoint store );
    public abstract void transform( TriangulationPoint p );
}
