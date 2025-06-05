package net.absolutecinema.rendering.meshes;

import net.absolutecinema.Buffers;
import net.absolutecinema.Constants;
import net.absolutecinema.rendering.GLObject;
import net.absolutecinema.rendering.GraphicsWrapper;
import net.absolutecinema.rendering.shader.FieldType;
import net.absolutecinema.rendering.shader.LayoutEntry;
import net.absolutecinema.rendering.shader.programs.ShaderProgram;
import net.absolutecinema.rendering.shader.programs.TexturedObjShader;
import org.lwjgl.opengl.GL33;

import java.nio.FloatBuffer;
import java.text.CollationElementIterator;
import java.util.Collection;
import java.util.List;

import static net.absolutecinema.AbsoluteCinema.LOGGER;
import static net.absolutecinema.AbsoluteCinema.shaderManager;

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

//        for (LayoutEntry le : shader.getLayout()) {
//            int location = switch (le.name()) {
//                case Constants.VERT_COORDINATE_LAYOUT_FIELD -> 0;
//                case Constants.VERT_NORMAL_LAYOUT_FIELD -> 1;
//                case Constants.UV_COORDINATE_LAYOUT_FIELD -> 2;
//                default -> throw new IllegalStateException("Unknown layout field: " + le.name());
//            };
//
//            GraphicsWrapper.assignVToV(location, le.size(), le.normalize(), stride, lastOffset, le.type());
//            lastOffset += le.size();
//            GraphicsWrapper.enableVToV(location);
//        }
    }

    public ShaderProgram getShaderProgram(){
        return this.shader;
    }

    public void uploadToVBO(FloatBuffer pBuffer){
        vao.bind();
        vbo.bind();
        GraphicsWrapper.uploadToVBO(pBuffer);

        vertCount = pBuffer.limit() / stride;
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

        //LOGGER.debug("Assigning attr index " + lastIndex + " at offset " + lastOffset + " of stride " + stride);

        GraphicsWrapper.assignVToV(lastIndex, pSize, pNormalize, this.stride, lastOffset, pType);
        GraphicsWrapper.enableVToV(lastIndex);
        //LOGGER.debug("Enabled vertex attrib index " + lastIndex + " (offset=" + lastOffset + ")");
        lastOffset+=pSize;
    }

    public void unbind(){
        GraphicsWrapper.unbindVAO();
        GraphicsWrapper.unbindVBO();
    }

    public void bindVAO(){
        this.vao.bind();
    }

    public void draw(){
        draw(true);
    }
    public void draw(boolean applyShader){//todo fix + batch up
        if(applyShader)
            shaderManager.useShaderProgram(getShaderProgram());

        bindVAO();//BIND VAO
        //vbo.bind();
        GraphicsWrapper.drawTriangles(vertCount);
    }

    public void assignVertices(float[] pVertices){
        FloatBuffer vertexBuffer = Buffers.floatBuffer(pVertices.length);
        vertexBuffer.put(pVertices).flip();
        uploadToVBO(vertexBuffer);
        Buffers.free(vertexBuffer);
    }

    public Vao getVAO() {
        return vao;
    }
}
