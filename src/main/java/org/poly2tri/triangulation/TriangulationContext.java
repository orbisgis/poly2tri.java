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

import org.poly2tri.triangulation.delaunay.DelaunayTriangle;
import org.poly2tri.triangulation.delaunay.sweep.AdvancingFrontNode;
import org.poly2tri.triangulation.delaunay.sweep.DTSweepConstraint;
import org.poly2tri.triangulation.sets.PointSet;


public abstract class TriangulationContext
{
    public enum TriangulationMode
    {
        DT,CDT,Polygon
    }
    
    private DebugContext _debug;
    
    protected PointSet _pointSet;
    protected ArrayList<TriangulationPoint> _points = new ArrayList<TriangulationPoint>(200);
    protected TriangulationMode _triangulationMode;

    private boolean _terminated = false;
    private boolean _debugEnabled = false;
    private boolean _waitUntilNotified;

    private int _stepTime = -1;
    private int _stepCount = 0;
    public int getStepCount() { return _stepCount; }

    public void done()
    {
        _stepCount++;
    }

    public abstract void prepareTriangulation();
    
    public ArrayList<TriangulationPoint> getPoints()
    {
        return _points;
    }

    public synchronized void suspend(String message)
    {
        if( !_debugEnabled ) return;
        try
        {
            synchronized( this )
            {
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
        _stepCount++;
        if( _terminated )
        {
            throw new RuntimeException( "Triangulation process terminated");
        }
    }

    public void clear()
    {
        _points.clear();
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
    
    public void isDebugEnabled( boolean b )
    {
        if( b )
        {
            _debug = new DebugContext();
        }
        _debugEnabled  = b;
    }

    public DebugContext getDebugContext()
    {
        return _debug;
    }
    
    public class DebugContext
    {
        /*
         * Fields used for visual representation of current triangulation
         */
        protected DelaunayTriangle _primaryTriangle;
        protected DelaunayTriangle _secondaryTriangle;
        protected TriangulationPoint _activePoint;
        protected AdvancingFrontNode _activeNode;
        protected DTSweepConstraint _activeConstraint;   
        
//      private Tuple2<TPoint,Double> m_circumCircle = new Tuple2<TPoint,Double>( new TPoint(), new Double(0) );
//      public Tuple2<TPoint,Double> getCircumCircle() { return m_circumCircle; }
        public DelaunayTriangle getPrimaryTriangle()
        {
            return _primaryTriangle;
        }

        public DelaunayTriangle getSecondaryTriangle()
        {
            return _secondaryTriangle;
        }

        public void setActiveConstraint( DTSweepConstraint e )
        {
            _activeConstraint = e;
            suspend("setWorkingSegment");
        }
        
        public DTSweepConstraint getActiveConstraint()
        {
            return _activeConstraint;
        }

        public void setPrimaryTriangle( DelaunayTriangle triangle )
        {
            _primaryTriangle = triangle;        
            suspend("setPrimaryTriangle");
        }

        public void setSecondaryTriangle( DelaunayTriangle triangle )
        {
            _secondaryTriangle = triangle;        
            suspend("setSecondaryTriangle");
        }

        public TriangulationPoint getActivePoint()
        {
            return _activePoint;
        }
        
        public void setActivePoint( TriangulationPoint point )
        {
            _activePoint = point;        
        }

        public void setActiveNode( AdvancingFrontNode node )
        {
            _activeNode = node;        
            suspend("setWorkingNode");
        }
        
        public AdvancingFrontNode getActiveNode()
        {
            return _activeNode;
        }
        
//      public void setWorkingCircumCircle( TPoint point, TPoint point2, TPoint point3 )
//      {
//          double dx,dy;
//          
//          CircleXY.circumCenter( point, point2, point3, m_circumCircle.a );
//          dx = m_circumCircle.a.getX()-point.getX();
//          dy = m_circumCircle.a.getY()-point.getY();
//          m_circumCircle.b = Double.valueOf( Math.sqrt( dx*dx + dy*dy ) );
//          
//      }

    }    
}
