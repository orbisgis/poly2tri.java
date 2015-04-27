package org.poly2tri.geometry.primitives;

public abstract class Point implements Comparable<Point>
{
    public abstract double getX();
    public abstract double getY();
    public abstract double getZ();

    public abstract float getXf();
    public abstract float getYf();
    public abstract float getZf();
    
    public abstract void set( double x, double y, double z );

    protected static int calculateHashCode( double x, double y, double z)
    {
        int result = 17;

        final long a = Double.doubleToLongBits(x);
        result += 31 * result + (int) (a ^ (a >>> 32));

        final long b = Double.doubleToLongBits(y);
        result += 31 * result + (int) (b ^ (b >>> 32));

        final long c = Double.doubleToLongBits(z);
        result += 31 * result + (int) (c ^ (c >>> 32));

        return result;
        
    }

    @Override
    public int hashCode() {
        return calculateHashCode(getX(), getY(), getZ());
    }

    @Override
    public int compareTo(Point other) {
        if(other == null) {
            return 1;
        }
        if (getX() < other.getX()) return -1;
        if (getX() > other.getX()) return 1;
        if (getY() < other.getY()) return -1;
        if (getY() > other.getY()) return 1;
        return 0;
    }

    /**
     * Compute distance to another point
     * @param p2 Other point
     * @return Distance
     */
    public double distance(Point p2) {
        double dx = getX() - p2.getX();
        double dy = getY() - p2.getY();
        double dz = getZ() - p2.getZ();

        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}
