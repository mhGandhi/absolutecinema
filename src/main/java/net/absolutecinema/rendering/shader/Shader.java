package net.absolutecinema.rendering.shader;

import net.absolutecinema.rendering.GLObject;
import net.absolutecinema.rendering.GraphicsWrapper;

public class Shader extends GLObject implements AutoCloseable {
    public final ShaderType type;
    private String source;

    public Shader(ShaderType pShaderType, String pSourceCode){
        super(GraphicsWrapper.createShader(pShaderType));
        this.type = pShaderType;
        upload(pSourceCode);
        compile();
    }

    private void upload(String pSourceCode){
        this.source = pSourceCode;
        GraphicsWrapper.uploadSourceToShader(this.id, pSourceCode);
    }

    public String getSource(){
        return this.source;
    }

    private void compile(){
        GraphicsWrapper.compileShader(this.id);
    }

    public void close(){
        GraphicsWrapper.deleteShader(this.id);
    }
}
