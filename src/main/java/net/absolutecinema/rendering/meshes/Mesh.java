package net.absolutecinema.rendering.meshes;

import net.absolutecinema.Buffers;

import java.nio.FloatBuffer;

public abstract class Mesh {
    protected final BufferWrapper gpuBuffer;

    public Mesh(BufferWrapper pBufferWrapper){
        this.gpuBuffer = pBufferWrapper;
    }

    public void draw(){
        gpuBuffer.draw();
    }

    public void assignVertices(float[] pVertices){
        FloatBuffer vertexBuffer = Buffers.floatBuffer(pVertices.length);
        vertexBuffer.put(pVertices).flip();
        gpuBuffer.uploadToVBO(vertexBuffer);
        Buffers.free(vertexBuffer);
    }
}
