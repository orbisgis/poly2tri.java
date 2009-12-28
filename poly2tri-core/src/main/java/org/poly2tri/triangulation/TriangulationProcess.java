/* Poly2Tri
 * Copyright (c) 2009-2010, Poly2Tri Contributors
 * http://code.google.com/p/poly2tri/
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of Poly2Tri nor the names of its contributors may be
 *   used to endorse or promote products derived from this software without specific
 *   prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.poly2tri.triangulation;

import java.lang.Thread.State;

import org.poly2tri.Poly2Tri;
import org.poly2tri.triangulation.TriangulationContext.TriangulationMode;
import org.poly2tri.triangulation.delaunay.sweep.DTSweep;
import org.poly2tri.triangulation.delaunay.sweep.DTSweepContext;
import org.poly2tri.triangulation.sets.ConstrainedPointSet;
import org.poly2tri.triangulation.sets.PointSet;
import org.poly2tri.triangulation.sets.PolygonSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * @author Thomas Åhlén, thahlen@gmail.com
 * 
 */
public class TriangulationProcess implements Runnable
{
    private final static Logger logger = LoggerFactory.getLogger( TriangulationProcess.class );

    private final TriangulationAlgorithm _algorithm;
    
    private TriangulationContext _tcx;
    private Thread               _thread;
    private boolean              _isTerminated = false;
    private long                 _timestamp = 0;
    private double               _triangulationTime = 0;

    private boolean _awaitingTermination;
    private boolean              _restart = false;
    private TriangulationMode    _triangulationMode;
    private PointSet             _pointSet;
    
    public int getStepCount()
    {
        return _tcx.getStepCount();
    }

    public long getTimestamp()
    {
        return _timestamp;
    }
    
    public double getTriangulationTime() 
    {
        return _triangulationTime;
    }
    
    /**
     * Uses SweepLine algorithm by default
     * @param algorithm
     */
    public TriangulationProcess()
    {
        this( TriangulationAlgorithm.DTSweep );
    }

    public TriangulationProcess( TriangulationAlgorithm algorithm )
    {
        _algorithm = algorithm;
        _tcx = Poly2Tri.createContext( algorithm );
    }
    
    /**
     * This retriangulates same set as previous triangulation
     * useful if you want to do consecutive triangulations with 
     * same data. Like when you when you want to do performance 
     * tests.
     */
//    public void triangulate()
//    {
//        start();
//    }
    
    /**
     * Triangulate a PointSet with eventual constraints 
     * 
     * @param cps
     */
    public void triangulate( PointSet ps )
    {
        _triangulationMode = TriangulationMode.DT;
        _pointSet = ps;        
        start();
    }

    /**
     * Triangulate a PointSet with eventual constraints 
     * 
     * @param cps
     */
    public void triangulate( ConstrainedPointSet cps )
    {
        _triangulationMode = TriangulationMode.CDT;
        _pointSet = cps;        
        start();
    }
    
    /**
     * Triangulate a PolygonSet
     * 
     * @param ps
     */
    public void triangulate( PolygonSet ps )
    {
        _triangulationMode = TriangulationMode.Polygon;
        _pointSet = ps;
        start();
    }

    private void start()
    {
        if( _thread == null || _thread.getState() == State.TERMINATED )
        {
            _isTerminated = false;
            _tcx.clear();
            _tcx.setTriangulationMode( _triangulationMode );
            _tcx.setPointSet( _pointSet );
            
            _thread = new Thread( this, _algorithm.name() + "." + _triangulationMode );
            _thread.start();
        }
        else
        {
            // Triangulation already running. Terminate it so we can start a new
            shutdown();
            _restart = true;
        }
    }

    public boolean isWaiting()
    {
        if( _thread != null && _thread.getState() == State.WAITING )
        {
            return true;
        }
        return false;
    }

    @Override
    public void run()
    {
        try
        {
            _tcx.prepareTriangulation();
            long time = System.nanoTime();
            switch( _algorithm )
            {
                case DTSweep:
                default:
                    DTSweep.triangulate( (DTSweepContext)_tcx );                    
            }
            _triangulationTime = ( System.nanoTime() - time ) / 1e6;
            logger.info( "Triangulation of {} points [{}ms]", _tcx._pointSet.getPoints().size(), _triangulationTime );
        }
        catch( RuntimeException e )
        {
            if( _awaitingTermination )
            {
                _awaitingTermination = false;
                logger.info( "Thread[{}] : {}", _thread.getName(), e.getMessage() );
            }
            else
            {
                e.printStackTrace();
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
            logger.info( "Triangulation exception {}", e.getMessage() );
        }
        finally
        {
            _timestamp = System.currentTimeMillis();
            _isTerminated = true;
            _thread = null;
        }
        
        // Autostart a new triangulation?
        if( _restart )
        {
            _restart = false;
            start();
        }
    }

    public void resume()
    {
        if( _thread != null )
        {
            // Only force a resume when process is waiting for a notification
            if( _thread.getState() == State.WAITING )
            {
                synchronized( _tcx )
                {
                    _tcx.notify();
                }
            }
            else if( _thread.getState() == State.TIMED_WAITING )
            {
                _tcx.waitUntilNotified( false );
            }
        }
    }

    public void shutdown()
    {
        _awaitingTermination = true;
        _tcx.terminateTriangulation();
        resume();
    }

    public TriangulationContext getContext()
    {
        return _tcx;
    }

    public boolean isDone()
    {
        return _isTerminated;
    }

    public void requestRead()
    {
        _tcx.waitUntilNotified( true );
    }

    public boolean isReadable()
    {
        if( _thread == null )
        {
            return true;
        }
        else
        {
            synchronized( _thread )
            {
                // FIXME: m_thread can be set to null after check and before call to
                // m_thread.getState() so we get null pointer exception
                if( _thread.getState() == State.WAITING )
                {
                    return true;
                }
                else if( _thread.getState() == State.TIMED_WAITING )
                {
                    // Make sure that it stays readable
                    return _tcx.waitUntilNotified( true );
                }
                return false;
            }
        }
    }

    public interface ProcessListener
    {

    }
}
