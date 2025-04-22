package net.absolutecinema.rendering.shader;

import net.absolutecinema.rendering.GLObject;
import net.absolutecinema.rendering.OpenGLWrapper;

public class Shader extends GLObject {
    public final ShaderType type;

    public Shader(ShaderType pShaderType, String pSourceCode){
        super(OpenGLWrapper.createShader(pShaderType));
        this.type = pShaderType;
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
