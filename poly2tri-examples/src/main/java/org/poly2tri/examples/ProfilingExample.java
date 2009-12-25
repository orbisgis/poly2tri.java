package org.poly2tri.examples;

import org.poly2tri.Poly2Tri;
import org.poly2tri.triangulation.sets.PointSet;
import org.poly2tri.triangulation.util.PointGenerator;

public class ProfilingExample
{
    public static void main(final String[] args) 
        throws Exception
    {
        PointSet ps = new PointSet( PointGenerator.uniformDistribution( 50, 500000 ) );
        for( int i=0; i<1; i++ )
        {
            Poly2Tri.triangulate( ps );
        }
        
        Thread.sleep( 10000000 );        
    }
    
    public void startProfiling()
        throws Exception
    {
    }
}
