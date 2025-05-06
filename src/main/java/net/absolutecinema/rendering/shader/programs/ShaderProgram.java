package net.absolutecinema.rendering.shader.programs;

import net.absolutecinema.rendering.GLObject;
import net.absolutecinema.rendering.GraphicsWrapper;
import net.absolutecinema.rendering.meshes.Mesh;
import net.absolutecinema.rendering.shader.*;

import java.util.*;

import static net.absolutecinema.AbsoluteCinema.LOGGER;

public class ShaderProgram extends GLObject implements AutoCloseable {
    private boolean linked;

    private final List<Shader> shaders;
    private final Map<String, Uni<?>> uniforms;
    private final List<LayoutEntry> fieldsLayout;

    private int stride;

    public ShaderProgram(){
        super(GraphicsWrapper.createProgram());
        linked = false;

        shaders = new LinkedList<>();
        uniforms = new HashMap<>();
        fieldsLayout = new LinkedList<>();

        stride = -1;
    }

    public Uni<?> addUni(CharSequence pName, Object initVal){
        if(isLinked()){
            throw new UniformException("ShaderProgram already linked; can not modify uniforms");
        }

        Uni<?> uniform = new Uni<>(this, pName, initVal);
        uniforms.put((String)pName, uniform);
        return uniform;
    }

    public Uni<?> getUni(String pUniKey){
        return uniforms.get(pUniKey);
    }

    public void addLayoutEntry(LayoutEntry pLE){
        if(isLinked()){
            LOGGER.err("ShaderProgram already linked; can not modify layout - returning");
            return;
        }

        this.fieldsLayout.add(pLE);
    }

    public Collection<LayoutEntry> getLayout(){
        if(!isLinked()){
            throw new ProgramLinkingException("Program is not yet linked - can not provide definite layout");
        }
        return this.fieldsLayout;
    }

    public int getStride(){
        if(!isLinked()){
            throw new ProgramLinkingException("Program is not yet linked - can not provide definite stride");
        }
        return this.stride;
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
        if(isLinked()){
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

    public void linkAndClearShaders(){//todo assert uniform + layout names all set up correctly
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

        assignUnis();
        linked = true;

        for (Shader s: shaders) {
            s.close();
        }
        shaders.clear();

        {//determine stride
            int fieldsSize = 0;
            for(LayoutEntry le : getLayout()){
                fieldsSize += le.size();
            }
            this.stride = fieldsSize;
        }

        LOGGER.info("LINKED " + this.toString());
    }

    protected void assignUnis() {
        //override here
    }

    public boolean isLinked(){
        return this.linked;
    }

    public void use(){
        GraphicsWrapper.useProgram(this.id);
    }

    public void close(){
        GraphicsWrapper.deleteProgram(this.id);
    }

    @Override
    public String toString() {
        String[] className =this.getClass().toString().split("\\.");
        return
                "<"+className[className.length-1]+"> layout:\n"+LayoutEntry.layoutToString(fieldsLayout)
                        +"uniforms:\n"+Uni.uniMapToString(uniforms);
    }
}
