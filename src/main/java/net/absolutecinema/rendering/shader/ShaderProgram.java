package net.absolutecinema.rendering.shader;

import net.absolutecinema.rendering.OpenGLWrapper;

import java.util.LinkedList;
import java.util.List;

import static net.absolutecinema.AbsoluteCinema.LOGGER;

public class ShaderProgram {
    public final int id;
    private boolean linked;

    public ShaderProgram(){
        this.id = OpenGLWrapper.createProgram();
        linked = false;
    }

    public void attach(Shader pShader){
        //boolean alreadyExists = shaders.stream()
        //        .anyMatch(shader -> shader.type.equals(pShader.type));

        //if(alreadyExists){
        //    LOGGER.err("Shader of type ["+pShader.type+"] already attached; not attaching ["+pShader.id+"]");
        //}else{
        //    shaders.add(pShader);
        //}
        OpenGLWrapper.attachShader(this.id, pShader.id);
    }

    public void link(){
        //for(Shader shader : shaders){
        //    OpenGLWrapper.attachShader(this.id, shader.id);
        //}
        try{
            OpenGLWrapper.linkProgram(this.id);
        }catch(ProgramLinkingException e){
            LOGGER.err(e.getMessage());
            return;
        }
        linked = true;
    }

    public boolean getLinked(){
        return this.linked;
    }
}
