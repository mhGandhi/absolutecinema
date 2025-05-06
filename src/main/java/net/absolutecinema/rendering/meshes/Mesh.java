package net.absolutecinema.rendering.meshes;

import net.absolutecinema.Buffers;
import net.absolutecinema.rendering.GraphicsWrapper;
import net.absolutecinema.rendering.shader.FieldType;
import net.absolutecinema.rendering.shader.LayoutEntry;
import net.absolutecinema.rendering.shader.programs.ShaderProgram;

import java.nio.FloatBuffer;
import java.text.CollationElementIterator;
import java.util.Collection;
import java.util.List;

import static net.absolutecinema.AbsoluteCinema.LOGGER;

public class Mesh {
    private final ShaderProgram shader;

    private final Vao vao;
    private final Vbo vbo;


    private final int stride;
    private int vertCount;

    private int lastIndex;
    private int lastOffset;

    public Mesh(ShaderProgram pShaderProgram){
        this.shader = pShaderProgram;

        vao = new Vao();
        vbo = new Vbo();

        vertCount = 0;
        this.stride = pShaderProgram.getStride();

        lastIndex = -1;
        lastOffset = 0;

        //init layout
        for(LayoutEntry le : pShaderProgram.getLayout()){
            addField(le.size(), le.type(), le.normalize());
        }
    }

    public ShaderProgram getShaderProgram(){
        return this.shader;
    }

    public void uploadToVBO(FloatBuffer pBuffer){
        vbo.bind();
        GraphicsWrapper.uploadToVBO(pBuffer);

        vertCount = pBuffer.limit() / stride;
        //MemoryUtil.memFree(pBuffer);
    }

    public void addField(int pSize, FieldType pType, boolean pNormalize){
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
        GraphicsWrapper.assignVToV(lastIndex, pSize, pNormalize, this.stride, lastOffset, pType);
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
        getShaderProgram().use();//todo fix + batch up
        bindVAO();
        GraphicsWrapper.drawTriangles(vertCount);
    }

    public void assignVertices(float[] pVertices){
        FloatBuffer vertexBuffer = Buffers.floatBuffer(pVertices.length);
        vertexBuffer.put(pVertices).flip();
        uploadToVBO(vertexBuffer);
        Buffers.free(vertexBuffer);
    }

}
