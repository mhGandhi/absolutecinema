package net.absolutecinema.rendering.meshes;

import net.absolutecinema.rendering.GraphicsWrapper;

import java.nio.FloatBuffer;

public class BufferWrapper {
    private Vao vao;
    private Vbo vbo;
    private int lastIndex;
    private int stride;
    private int vertCount;

    public BufferWrapper(int pStride){
        vao = new Vao();
        vbo = new Vbo();
        lastIndex = -1;
        vertCount = 0;

        this.stride = pStride;
    }

    public void uploadToVBO(FloatBuffer pBuffer){
        vbo.bind();
        GraphicsWrapper.uploadToVBO(pBuffer);

        vertCount = pBuffer.limit() / stride;
        //MemoryUtil.memFree(pBuffer);
    }

    public void addVToV(int pSize, boolean pNormalize, int pOffset){
        vao.bind();
        vbo.bind();
        lastIndex++;
        GraphicsWrapper.assignVToV(lastIndex, pSize, pNormalize, this.stride, pOffset);
        GraphicsWrapper.enableVToV(lastIndex);
    }

    public void unbind(){
        GraphicsWrapper.unbindVAO();
        GraphicsWrapper.unbindVBO();
    }

    public void bindVAO(){
        this.vao.bind();
    }

    public void draw(){
        GraphicsWrapper.drawTriangles(vertCount);
    }

}
