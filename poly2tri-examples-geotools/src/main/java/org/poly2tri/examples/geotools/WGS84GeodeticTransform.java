package org.poly2tri.examples.geotools;

import java.util.List;

import org.poly2tri.geometry.primitives.Point;
import org.poly2tri.transform.coordinate.CoordinateTransform;
import org.poly2tri.triangulation.TriangulationPoint;

/**
 * Converts a Point with Longitude,Latitude,Altitude representation to cartesian coordinates 
 * 
 * TODO:
 * 
 * WGS-84
 * The relation between Cartesian coordinates (X,Y,Z) and the curvilinear ones (lat,lon,h) is:
 
        X = (N+h) * cos(lat) * cos(lon)
        Y = (N+h) * cos(lat) * sin(lon)
        Z = (N*(1-e2)+h) * sin(lat)
 
        where
        
        N = prime vertical radius of curvature = a/sqrt(1-e2*sin(lat)^2)
        e2 = first eccentricity of the reference ellipsoid = 1 -b^2/a^2
        a = major semi-axis of reference ellipsoid = 6378137.000 m for WGS-84
        b = minor semi-axis of reference ellipsoid = 6356752.314 m for WGS-84
        
 * http://www.colorado.edu/geography/gcraft/notes/datum/gif/llhxyz.gif
 * @author Thomas Åhlén, thahlen@gmail.com
 */
public class WGS84GeodeticTransform implements CoordinateTransform
{
    private final static double PI_div180 = Math.PI/180;
    private double _radius = 1;
    
    public WGS84GeodeticTransform( double radius )
    {
        _radius = radius;
    }
    
//    @Override
//    public double toX( TriangulationPoint p )
//    {
//        return _radius*Math.cos( PI_div180*p.getY() )*Math.cos( PI_div180*p.getX() );
//    }
//
//    @Override
//    public double toY( TriangulationPoint p )
//    {
//        return _radius*Math.sin( PI_div180*p.getY() );
//    }
//
//    @Override
//    public double toZ( TriangulationPoint p )
//    {
//        return -_radius*Math.cos( PI_div180*p.getY() )*Math.sin( PI_div180*p.getX() );
//    }
//
//    @Override
//    public float toXf( TriangulationPoint p )
//    {
//        return (float)toX(p);
//    }
//
//    @Override
//    public float toYf( TriangulationPoint p )
//    {
//        return (float)toY(p);
//    }
//
//    @Override
//    public float toZf( TriangulationPoint p )
//    {
//        return (float)toZ(p);
//    }

    public void transform( TriangulationPoint p, TriangulationPoint store )
    {
        double x,y,z;
        double a,b,c;
        
        a = PI_div180*p.getY();
        b = PI_div180*p.getX();
        c = _radius*Math.cos( a );
        
        x = c*Math.cos( b );
        y = c*Math.sin( b );
        z = _radius*Math.sin( a );
     
        store.set( x, y, z );
    }

    public void transform( TriangulationPoint p )
    {
        this.transform( p, p );
    }

    @Override
    public void transform( Point p, Point store )
    {
        double x,y,z;
        double a,b,c;
        
        a = PI_div180*p.getY();
        b = PI_div180*p.getX();
        c = _radius*Math.cos( a );
        
        x = c*Math.cos( b );
        y = c*Math.sin( b );
        z = _radius*Math.sin( a );
     
        store.set( x, y, z );
    }

    @Override
    public void transform( Point p )
    {
        transform( p, p );
    }

    @Override
    public void transform( List<? extends Point> list )
    {
        for( Point p : list )
        {
            transform(p, p);
        }
    }

}
