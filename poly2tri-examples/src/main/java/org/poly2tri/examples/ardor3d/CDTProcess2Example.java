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
package org.poly2tri.examples.ardor3d;

import java.util.concurrent.Callable;

import org.poly2tri.examples.ardor3d.base.P2TSimpleExampleBase;
import org.poly2tri.triangulation.Triangulatable;
import org.poly2tri.triangulation.TriangulationProcess;
import org.poly2tri.triangulation.TriangulationProcessEvent;
import org.poly2tri.triangulation.TriangulationProcessListener;
import org.poly2tri.triangulation.tools.ardor3d.ArdorMeshMapper;
import org.poly2tri.triangulation.util.PolygonGenerator;

import com.ardor3d.framework.FrameHandler;
import com.ardor3d.input.logical.LogicalLayer;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.renderer.state.WireframeState;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.util.GameTaskQueueManager;
import com.ardor3d.util.ReadOnlyTimer;
import com.ardor3d.util.geom.BufferUtils;
import com.google.inject.Inject;

/**
 * Example of how to use threaded triangulation 
 * and polling
 * 
 * @author Thomas Åhlén, thahlen@gmail.com
 *
 */
public class CDTProcess2Example extends P2TSimpleExampleBase 
{
    private TriangulationProcess _process;
    private Mesh _mesh;
    private int count;
    
    public static void main(final String[] args) 
    {
        start(CDTProcess2Example.class);
    }

    @Inject
    public CDTProcess2Example( LogicalLayer logicalLayer, FrameHandler frameHandler )
    {
        super( logicalLayer, frameHandler );
    }

    @Override
    protected void updateExample(final ReadOnlyTimer timer) 
    {
        if( count%100 == 0 )
        {
            _process.triangulate( PolygonGenerator.RandomCircleSweep( 50, 100 ) );
        }
        count++;
        
        // Poll if triangulation is done yet
        if( _process.isDone() )
        {
            ArdorMeshMapper.updateTriangleMesh( _mesh, _process.getContext().getTriangulatable().getTriangles() );            
        }
    }

    @Override
    protected void initExample()
    {
        super.initExample();
        
        Node node = new Node();
        node.setRenderState( new WireframeState() );
        _node.attachChild( node );
        
        _process = new TriangulationProcess();
        _process.triangulate( PolygonGenerator.RandomCircleSweep( 50, 100 ) );
        
        _mesh = new Mesh();
        _mesh.setDefaultColor( ColorRGBA.BLUE );
        _mesh.getMeshData().setVertexBuffer( BufferUtils.createFloatBuffer( 0 ) );
        node.attachChild( _mesh );  
    }    
}
