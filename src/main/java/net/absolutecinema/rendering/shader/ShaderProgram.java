package net.absolutecinema.rendering.shader;

import net.absolutecinema.rendering.GLObject;
import net.absolutecinema.rendering.GraphicsWrapper;

import static net.absolutecinema.AbsoluteCinema.LOGGER;

public class ShaderProgram extends GLObject {
    private boolean linked;

    public ShaderProgram(){
        super(GraphicsWrapper.createProgram());
        linked = false;
    }

    public Uni<?> getUni(CharSequence pName){
        return new Uni<>(this, pName);
    }

    public void attach(Shader pShader){
        //boolean alreadyExists = shaders.stream()
        //        .anyMatch(shader -> shader.type.equals(pShader.type));

        //if(alreadyExists){
        //    LOGGER.err("Shader of type ["+pShader.type+"] already attached; not attaching ["+pShader.id+"]");
        //}else{
        //    shaders.add(pShader);
        //}
        GraphicsWrapper.attachShader(this.id, pShader.id);
    }

    public void link(){
        //for(Shader shader : shaders){
        //    OpenGLWrapper.attachShader(this.id, shader.id);
        //}
        try{
            GraphicsWrapper.linkProgram(this.id);
        }catch(ProgramLinkingException e){
            LOGGER.err(e.getMessage());
            return;
        }
        linked = true;
    }

    public boolean getLinked(){
        return this.linked;
    }

    public void use(){
        GraphicsWrapper.useProgram(this.id);
    }

    public void delete(){
        GraphicsWrapper.deleteProgram(this.id);
    }
}
