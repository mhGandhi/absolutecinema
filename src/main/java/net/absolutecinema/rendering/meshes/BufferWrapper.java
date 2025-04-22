package net.absolutecinema.rendering.meshes;

import net.absolutecinema.rendering.GraphicsWrapper;

import java.nio.FloatBuffer;

public class BufferWrapper {
    private Vao vao;
    private Vbo vbo;
    private int lastIndex;

    public BufferWrapper(){
        vao = new Vao();
        vbo = new Vbo();
        lastIndex = -1;
    }

    public void uploadToVBO(FloatBuffer pBuffer){
        vbo.bind();
        GraphicsWrapper.uploadToVBO(pBuffer);
        //MemoryUtil.memFree(pBuffer);
    }

    public void addVToV(int pSize, boolean pNormalize, int pStride, int pOffset){
        vao.bind();
        vbo.bind();
        lastIndex++;
        GraphicsWrapper.assignVToV(lastIndex, pSize, pNormalize, pStride, pOffset);
        GraphicsWrapper.enableVToV(lastIndex);
    }

    public void unbind(){
        GraphicsWrapper.unbindVAO();
        GraphicsWrapper.unbindVBO();
    }

    public void bindVAO(){
        this.vao.bind();
    }

}
