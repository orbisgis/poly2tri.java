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
package org.poly2tri.triangulation.sets;

import java.util.ArrayList;
import java.util.List;

import org.poly2tri.triangulation.TriangulationPoint;
import org.poly2tri.triangulation.delaunay.DelaunayTriangle;

public class PointSet
{
    List<TriangulationPoint> _points;
    List<DelaunayTriangle> m_triangles;
    
    public PointSet( List<TriangulationPoint> points )
    {
        _points = new ArrayList<TriangulationPoint>();
        _points.addAll( points );
    }
    
    public List<TriangulationPoint> getPoints()
    {
        return _points;
    }
    
    public List<DelaunayTriangle> getTriangles()
    {
        if( m_triangles == null )
        {
            m_triangles = new ArrayList<DelaunayTriangle>( _points.size() );
        }
        return m_triangles;
    }
    
    public void addTriangle( DelaunayTriangle t )
    {
        if( m_triangles == null )
        {
            m_triangles = new ArrayList<DelaunayTriangle>( _points.size() );
        }
        m_triangles.add( t );
    }

    public void clearTriangulation()
    {
        if( m_triangles == null )
        {
            m_triangles = new ArrayList<DelaunayTriangle>( _points.size() );            
        }
        else
        {
            m_triangles.clear();            
        }
    }

    public void populate( ArrayList<TriangulationPoint> points )
    {
        points.addAll( _points );
    }
}
