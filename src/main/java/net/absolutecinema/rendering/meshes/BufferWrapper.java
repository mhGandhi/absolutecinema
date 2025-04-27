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

    public BufferWrapper(int pFieldsSize){
        vao = new Vao();
        vbo = new Vbo();
        lastIndex = -1;
        vertCount = 0;
        lastOffset = 0;

        this.stride = pFieldsSize;
    }

    public void uploadToVBO(FloatBuffer pBuffer){
        vbo.bind();
        GraphicsWrapper.uploadToVBO(pBuffer);

        vertCount = pBuffer.limit() / stride;
        //MemoryUtil.memFree(pBuffer);
    }

    public void addField(int pSize, boolean pNormalize){
        if(lastOffset==this.stride){
            LOGGER.warn("Fields already full ("+stride+"/"+stride+") - returning");
            return;
        } else if (lastOffset+pSize>this.stride) {
            LOGGER.err("Field of size "+pSize+" would overload field capacity "+this.stride+" by "+(lastOffset+pSize-this.stride)+" - returning");
            return;
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
        bindVAO();
        GraphicsWrapper.drawTriangles(vertCount);
    }

}
