package org.poly2tri.examples.ardor3d.misc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.poly2tri.examples.ardor3d.CDTModelExample.ExampleModels;
import org.poly2tri.polygon.Polygon;
import org.poly2tri.polygon.ardor3d.ArdorPolygon;
import org.poly2tri.triangulation.TriangulationPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ardor3d.math.Vector3;

public class PolygonLoader
{
    private final static Logger logger = LoggerFactory.getLogger( PolygonLoader.class );

    public static Polygon loadModel( ExampleModels model, double scale ) throws FileNotFoundException, IOException
    {
        String line;
        ArrayList<Vector3> points = new ArrayList<Vector3>();
        InputStream istream = PolygonLoader.class.getClassLoader().getResourceAsStream( model.getFilename() );
        if( istream == null )
        {
            throw new FileNotFoundException( "Couldn't find " + model );
        }
        InputStreamReader ir = new InputStreamReader( istream );
        BufferedReader reader = new BufferedReader( ir );
        while( ( line = reader.readLine() ) != null )
        {
            StringTokenizer tokens = new StringTokenizer( line, " " );
            points.add( new Vector3( Float.valueOf( tokens.nextToken() ).floatValue(), 
                                     Float.valueOf( tokens.nextToken() ).floatValue(),
                                     0f ));
        }
        if( points.isEmpty() )
        {
            throw new IOException( "no data in file " + model );
        }

        // Rescale models so they are centered at 0,0 and don't fall outside the
        // unit square
        double maxX, maxY, minX, minY;
        maxX = minX = points.get( 0 ).getX();
        maxY = minY = points.get( 0 ).getY();
        for( Vector3 p : points )
        {
            maxX = p.getX() > maxX ? p.getX() : maxX;
            maxY = p.getY() > maxY ? p.getY() : maxY;
            minX = p.getX() < minX ? p.getX() : minX;
            minY = p.getY() < minY ? p.getY() : minY;
        }

        double width, height, xScale, yScale;
        width = maxX - minX;
        height = maxY - minY;
        xScale = scale * 1f / width;
        yScale = scale * 1f / height;

        // System.out.println("scale/height=" + SCALE + "/" + height );
        // System.out.println("scale=" + yScale);

        for( Vector3 p : points )
        {
            p.subtractLocal( maxX - width / 2, maxY - height / 2, 0 );
            p.multiplyLocal( xScale < yScale ? xScale : yScale );
        }
        return new ArdorPolygon( points);
    }

    public static void saveModel( String path, TriangulationPoint[] points )
    {
        FileWriter writer = null;
        BufferedWriter w = null;
        String file = path+System.currentTimeMillis()+".dat";
        try
        {
            
            writer = new FileWriter(file);
            w = new BufferedWriter(writer);
            for( TriangulationPoint p : points )
            {
                w.write( Float.toString( p.getXf() ) +" "+ Float.toString( p.getYf() ));
                w.newLine();
            }
            logger.info( "Saved polygon\n" + file );
        }
        catch( IOException e )
        {
            logger.error( "Failed to save model" );
        }
        finally
        {
            if( w != null )
            {
                try
                {
                    w.close();
                }
                catch( IOException e2 )
                {                    
                }
            }
        }
    }

}
