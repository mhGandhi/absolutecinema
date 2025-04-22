package net.absolutecinema.rendering.shader;

import net.absolutecinema.rendering.OpenGLWrapper;

public class Shader {
    public final int id;
    public final ShaderType type;

    public Shader(ShaderType pShaderType, String pSourceCode){
        this.type = pShaderType;
        this.id = OpenGLWrapper.createShader(this.type);
        upload(pSourceCode);
        compile();
    }

    private void upload(String pSourceCode){
        OpenGLWrapper.uploadSourceToShader(this.id, pSourceCode);
    }

    private void compile(){
        OpenGLWrapper.compileShader(this.id);
    }

    public void delete(){
        OpenGLWrapper.deleteShader(this.id);
    }
}
