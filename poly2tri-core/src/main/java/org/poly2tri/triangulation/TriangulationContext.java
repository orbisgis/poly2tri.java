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

import java.util.ArrayList;
import java.util.List;

import org.poly2tri.triangulation.delaunay.DelaunayTriangle;
import org.poly2tri.triangulation.sets.PointSet;

public abstract class TriangulationContext<A extends TriangulationDebugContext>
{
    public enum TriangulationMode
    {
        DT,CDT,Polygon
    }
    
    protected A _debug;
    protected boolean _debugEnabled = false;
    
    protected ArrayList<DelaunayTriangle> _triList = new ArrayList<DelaunayTriangle>();

    protected PointSet _pointSet;
    protected ArrayList<TriangulationPoint> _points = new ArrayList<TriangulationPoint>(200);
    protected TriangulationMode _triangulationMode;

    private boolean _terminated = false;
    private boolean _waitUntilNotified;

    private int _stepTime = -1;
    private int _stepCount = 0;
    public int getStepCount() { return _stepCount; }

    public void done()
    {
        _stepCount++;
    }

    public abstract void prepareTriangulation();
    
    public void addToList( DelaunayTriangle triangle )
    {
        _triList.add( triangle );
    }

    public List<DelaunayTriangle> getTriangles()
    {
        return _triList;
    }
    
    public List<TriangulationPoint> getPoints()
    {
        return _points;
    }

    public synchronized void suspend(String message)
    {
        if( _debugEnabled )
        {
            try
            {
                synchronized( this )
                {
                    _stepCount++;
                    if( _stepTime > 0 )
                    {
                        wait( (int)_stepTime );
                        /** Can we resume execution or are we being read */ 
                        if( _waitUntilNotified )
                        {
                            wait();
                        }
                    }
                    else
                    {
                        wait();
                    }
                    // We have been notified
                    _waitUntilNotified = false;
                }
            }
            catch( InterruptedException e )
            {
                suspend("Triangulation was interrupted");
            }
        }
        if( _terminated )
        {
            throw new RuntimeException( "Triangulation process terminated before completion");
        }
    }
    
    public void clear()
    {
        _points.clear();
        _terminated = false;
        if( _debug != null )
        {
            _debug.clear();
        }
        _stepCount=0;
    }

    public void setTriangulationMode( TriangulationMode triangulationMode )
    {
        _triangulationMode = triangulationMode;
    }
    
    public TriangulationMode getTriangulationMode()
    {
        return _triangulationMode;
    }
    
    public void setPointSet( PointSet ps ) 
    { 
        // Make sure we got a clear triangle list before starting
        ps.clearTriangulation();
        _pointSet = ps; 
    }
    public PointSet getPointSet() { return _pointSet; }

    public synchronized boolean waitUntilNotified(boolean b)
    {
        return _waitUntilNotified = b;
    }

    public void terminateTriangulation()
    {
        _terminated=true;
    }

    public boolean isDebugEnabled()
    {
        return _debugEnabled;
    }
    
    public abstract void isDebugEnabled( boolean b );

    public A getDebugContext()
    {
        return _debug;
    }
    
    public abstract class DebugContext
    {
    }
}
