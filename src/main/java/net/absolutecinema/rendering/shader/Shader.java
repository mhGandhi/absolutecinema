package net.absolutecinema.rendering.shader;

import net.absolutecinema.rendering.OpenGLWrapper;
import org.lwjgl.opengl.GL33;

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
}
