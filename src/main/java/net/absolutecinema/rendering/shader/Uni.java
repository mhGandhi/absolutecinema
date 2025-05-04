package net.absolutecinema.rendering.shader;

import net.absolutecinema.rendering.GLObject;
import net.absolutecinema.rendering.GraphicsWrapper;
import net.absolutecinema.rendering.UniformType;

public class Uni<T> extends GLObject {
    public final UniformType type;

    public Uni(ShaderProgram pProgram, CharSequence pName, T startVal){
        super(GraphicsWrapper.getUniformLocation(pProgram.id, pName));
        if(startVal == null){
            throw new UniformException("Starting value for Uniform may not be null");
        }
        type = UniformType.fromClass(startVal.getClass());
        if(this.id==-1){
            throw new UniformException("ShaderProgram ["+pProgram+"] does not support uniform ["+pName+"]");
        }

        set(startVal);
    }

    public void set(T pVal){
        GraphicsWrapper.putUniformValue(this.id, pVal);
    }

}
