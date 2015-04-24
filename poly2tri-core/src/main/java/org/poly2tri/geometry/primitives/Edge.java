package org.poly2tri.geometry.primitives;

import org.poly2tri.triangulation.TriangulationPoint;

public abstract class Edge<A extends Point>
{
    protected A p;
    protected A q;

    public A getP()
    {
        return p;
    }

    public A getQ()
    {
        return q;
    }

    /**
     * @param pt Other point
     * @return Angle between p-q and p-pt
     */
    public double getAngle(A pt) {
        return angle(p, q, pt);
    }


    public static <A extends Point> double angle( A p, A a, A b )
    {
        final double px = p.getX();
        final double py = p.getY();
        final double ax = a.getX() - px;
        final double ay = a.getY() - py;
        final double bx = b.getX() - px;
        final double by = b.getY() - py;
        return Math.atan2( ax*by - ay*bx, ax*bx + ay*by );
    }
}
