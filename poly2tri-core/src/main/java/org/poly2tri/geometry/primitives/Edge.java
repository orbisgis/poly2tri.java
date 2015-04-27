package org.poly2tri.geometry.primitives;

import org.poly2tri.triangulation.delaunay.DelaunayTriangle;

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
     * Reverse p and q
     */
    public void reverse() {
        final A r = p;
        p = q;
        q = r;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Edge)) return false;

        Edge edge = (Edge) o;

        return p.equals(edge.p) && q.equals(edge.q);

    }

    @Override
    public int hashCode() {
        int result = p.hashCode();
        result = 31 * result + q.hashCode();
        return result;
    }

    /**
     * Puts the line segment into a normalized form.
     * This is useful for using line segments in maps and indexes when
     * topological equality rather than exact equality is desired.
     * A segment in normalized form has the first point smaller
     * than the second (according to the standard ordering on {@link org.poly2tri.geometry.primitives.Point}).
     */
    public void normalize()
    {
        if (p.compareTo(q) < 0) reverse();
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


    /**
     * From jts CGAlgorithms.distancePointLine
     * Computes the 2D distance from a point p to a line segment AB
     *
     * Note: NON-ROBUST!
     *
     * @param o
     *          the point to compute the distance for
     * @param A
     *          one point of the line
     * @param B
     *          another point of the line (must be different to A)
     * @return the distance from p to line segment AB
     */
    public double distance(Point o)
    {
        // if start = end, then just compute distance to one of the endpoints
        if (p.getX() == q.getX() && p.getY() == q.getY())
            return o.distance(p);

        // otherwise use comp.graphics.algorithms Frequently Asked Questions method
    /*
     * (1) r = AC dot AB
     *         ---------
     *         ||AB||^2
     *
     * r has the following meaning:
     *   r=0 P = A
     *   r=1 P = B
     *   r<0 P is on the backward extension of AB
     *   r>1 P is on the forward extension of AB
     *   0<r<1 P is interior to AB
     */

        double len2 = (q.getX() - p.getX()) * (q.getX() - p.getX()) + (q.getY() - p.getY()) * (q.getY() - p.getY());
        double r = ((o.getX() - p.getX()) * (q.getX() - p.getX()) + (o.getY() - p.getY()) * (q.getY() - p.getY()))
                / len2;

        if (r <= 0.0)
            return o.distance(p);
        if (r >= 1.0)
            return o.distance(q);

    /*
     * (2) s = (Ay-Cy)(Bx-Ax)-(Ax-Cx)(By-Ay)
     *         -----------------------------
     *                    L^2
     *
     * Then the distance from C to P = |s|*L.
     *
     * This is the same calculation as {@link #distancePointLinePerpendicular}.
     * Unrolled here for performance.
     */
        double s = ((p.getY() - o.getY()) * (q.getX() - p.getX()) - (p.getX() - o.getX()) * (q.getY() - p.getY()))
                / len2;
        return Math.abs(s) * Math.sqrt(len2);
    }

}
