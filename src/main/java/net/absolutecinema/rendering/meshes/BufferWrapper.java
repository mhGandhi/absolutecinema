package net.absolutecinema.rendering.meshes;

import net.absolutecinema.rendering.GraphicsWrapper;

import java.nio.FloatBuffer;

import static net.absolutecinema.AbsoluteCinema.LOGGER;

public class BufferWrapper {
    private final Vao vao;
    private final Vbo vbo;
    private int lastIndex;
    private int lastOffset;
    private final int stride;
    private int vertCount;

    public BufferWrapper(int pStride){
        vao = new Vao();
        vbo = new Vbo();
        lastIndex = -1;
        vertCount = 0;
        lastOffset = 0;

        this.stride = pStride;
    }

    public void uploadToVBO(FloatBuffer pBuffer){
        vbo.bind();
        GraphicsWrapper.uploadToVBO(pBuffer);

        vertCount = pBuffer.limit() / stride;
        //MemoryUtil.memFree(pBuffer);
    }

    public void addVToV(int pSize, boolean pNormalize){
        if(lastOffset==this.stride){
            LOGGER.err("Stride of "+this.stride+" already satisfied");
        } else if (lastOffset+pSize>this.stride) {
            LOGGER.err("Assignment of size "+pSize+" won't fit into stride of "+this.stride+" by "+(lastOffset+pSize-this.stride));
        }

        vao.bind();
        vbo.bind();
        lastIndex++;
        GraphicsWrapper.assignVToV(lastIndex, pSize, pNormalize, this.stride, lastOffset);
        lastOffset+=pSize;
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
