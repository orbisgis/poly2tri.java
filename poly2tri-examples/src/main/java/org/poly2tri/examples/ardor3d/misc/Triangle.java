/**
 * Copyright (c) 2008-2009 Ardor Labs, Inc.
 *
 * This file is part of Ardor3D.
 *
 * Ardor3D is free software: you can redistribute it and/or modify it 
 * under the terms of its license which may be found in the accompanying
 * LICENSE file or at <http://www.ardor3d.com/LICENSE>.
 */

package org.poly2tri.examples.ardor3d.misc;

import java.nio.FloatBuffer;

import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.util.geom.BufferUtils;

public class Triangle extends Mesh 
{

    private static final long serialVersionUID = 1L;

    public Triangle() 
    {
        this( "Triangle" );
    }

    public Triangle(final String name ) 
    {
        this( name, 
              new Vector3( Math.cos( Math.toRadians(  90 ) ), Math.sin( Math.toRadians(  90 ) ), 0 ),
              new Vector3( Math.cos( Math.toRadians( 210 ) ), Math.sin( Math.toRadians( 210 ) ), 0 ),
              new Vector3( Math.cos( Math.toRadians( 330 ) ), Math.sin( Math.toRadians( 330 ) ), 0 ));
    }

    public Triangle(final String name, 
                    ReadOnlyVector3 a, 
                    ReadOnlyVector3 b, 
                    ReadOnlyVector3 c ) 
    {
        super(name);
        initialize(a,b,c);
    }

    /**
     * <code>resize</code> changes the width and height of the given quad by altering its vertices.
     * 
     * @param width
     *            the new width of the <code>Quad</code>.
     * @param height
     *            the new height of the <code>Quad</code>.
     */
//    public void resize( double radius ) 
//    {
//        _meshData.getVertexBuffer().clear();
//        _meshData.getVertexBuffer().put((float) (-width / 2)).put((float) (height / 2)).put(0);
//        _meshData.getVertexBuffer().put((float) (-width / 2)).put((float) (-height / 2)).put(0);
//        _meshData.getVertexBuffer().put((float) (width / 2)).put((float) (-height / 2)).put(0);
//        _meshData.getVertexBuffer().put((float) (width / 2)).put((float) (height / 2)).put(0);
//    }

    /**
     * <code>initialize</code> builds the data for the <code>Quad</code> object.
     * 
     * @param width
     *            the width of the <code>Quad</code>.
     * @param height
     *            the height of the <code>Quad</code>.
     */
    private void initialize(ReadOnlyVector3 a, ReadOnlyVector3 b, ReadOnlyVector3 c ) 
    {
        final int verts = 3;
        _meshData.setVertexBuffer(BufferUtils.createVector3Buffer(3));
        _meshData.setNormalBuffer(BufferUtils.createVector3Buffer(3));
        final FloatBuffer tbuf = BufferUtils.createVector2Buffer(3);
        _meshData.setTextureBuffer(tbuf, 0);

        _meshData.setIndexBuffer(BufferUtils.createIntBuffer(3));

        Vector3 ba = Vector3.fetchTempInstance();
        Vector3 ca = Vector3.fetchTempInstance();
        ba.set( b ).subtractLocal( a );
        ca.set( c ).subtractLocal( a );
        ba.crossLocal( ca ).normalizeLocal();
        
        _meshData.getNormalBuffer().put(ba.getXf()).put(ba.getYf()).put(ba.getZf());
        _meshData.getNormalBuffer().put(ba.getXf()).put(ba.getYf()).put(ba.getZf());
        _meshData.getNormalBuffer().put(ba.getXf()).put(ba.getYf()).put(ba.getZf());

        Vector3.releaseTempInstance( ba );
        Vector3.releaseTempInstance( ca );
        
        tbuf.put(0).put(1);
        tbuf.put(0).put(0);
        tbuf.put(1).put(0);

        _meshData.getIndexBuffer().put(0);
        _meshData.getIndexBuffer().put(1);
        _meshData.getIndexBuffer().put(2);

        _meshData.getVertexBuffer().put(a.getXf()).put(a.getYf()).put(a.getZf());
        _meshData.getVertexBuffer().put(b.getXf()).put(b.getYf()).put(b.getZf());
        _meshData.getVertexBuffer().put(c.getXf()).put(c.getYf()).put(c.getZf());
    }
}