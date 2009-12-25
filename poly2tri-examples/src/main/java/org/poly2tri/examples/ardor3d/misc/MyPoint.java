package org.poly2tri.examples.ardor3d.misc;

import org.poly2tri.triangulation.point.TPoint;

public class MyPoint extends TPoint
{
    int index;
    
    public MyPoint( double x, double y )
    {
        super( x, y );
    }

    public void setIndex(int i) 
    { 
        index = i; 
    }
    
    public int getIndex()
    {
        return index;
    }
    
    public boolean equals(Object other) 
    {
        if (!(other instanceof MyPoint)) return false;

        MyPoint p = (MyPoint)other;
        return getX() == p.getX() && getY() == p.getY();
    }

    public int hashCode() { return (int)getX() + (int)getY(); }
}
