package net.absolutecinema.rendering.shader;

import net.absolutecinema.rendering.GLObject;
import net.absolutecinema.rendering.GraphicsWrapper;

public class Shader extends GLObject implements AutoCloseable {
    public final ShaderType type;

    public Shader(ShaderType pShaderType, String pSourceCode){
        super(GraphicsWrapper.createShader(pShaderType));
        this.type = pShaderType;
        upload(pSourceCode);
        compile();
    }

    private void upload(String pSourceCode){
        GraphicsWrapper.uploadSourceToShader(this.id, pSourceCode);
    }

    private void compile(){
        GraphicsWrapper.compileShader(this.id);
    }

    public void close(){
        GraphicsWrapper.deleteShader(this.id);
    }
}
