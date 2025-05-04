package net.absolutecinema.rendering.shader;

import net.absolutecinema.rendering.GLObject;
import net.absolutecinema.rendering.GraphicsWrapper;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static net.absolutecinema.AbsoluteCinema.LOGGER;

public class ShaderProgram extends GLObject {
    private boolean linked;

    private List<Shader> shaders;
    private Map<String, Uni<?>> uniforms;

    public ShaderProgram(){
        super(GraphicsWrapper.createProgram());
        linked = false;

        shaders = new LinkedList<>();
        uniforms = new HashMap<>();
    }

    public Uni<?> addUni(CharSequence pName){
        Uni<?> uniform = new Uni<>(this, pName);
        uniforms.put((String)pName, uniform);
        return uniform;
    }

    public Uni<?> getUni(String pUniKey){
        return uniforms.get(pUniKey);
    }

    public void attach(Shader pShader){
        for(Shader s : shaders){
            if(s.type == pShader.type){
                LOGGER.err("Shader of Type ["+s.type+"] already attached - returning");
                return;
            }
        }

        GraphicsWrapper.attachShader(this.id, pShader.id);
        shaders.add(pShader);
    }

    public void detach(Shader pShader){
        if(linked){
            LOGGER.err("ShaderProgram already linked; can not detach ["+pShader+"] - returning");
            return;
        }

        if(!shaders.contains(pShader)){
            LOGGER.err("Shader ["+pShader+"] not attached - returning");
            return;
        }

        GraphicsWrapper.detachShader(this.id, pShader.id);
        shaders.remove(pShader);
    }

    public void linkAndClearShaders(){
        if(linked) {
            LOGGER.err("ShaderProgram ["+this+"] already linked - returning");
            return;
        }

        try{
            GraphicsWrapper.linkProgram(this.id);
        }catch(ProgramLinkingException e){
            LOGGER.err(e.getMessage());
            return;
        }
        linked = true;

        for (Shader s: shaders) {
            s.delete();
        }
    }

    public boolean isLinked(){
        return this.linked;
    }

    public void use(){
        GraphicsWrapper.useProgram(this.id);
    }

    public void delete(){
        GraphicsWrapper.deleteProgram(this.id);
    }
}
