package net.absolutecinema.rendering.shader;

import net.absolutecinema.rendering.GLObject;
import net.absolutecinema.rendering.GraphicsWrapper;

public class Uni<T> extends GLObject {
    public Uni(ShaderProgram pProgram, CharSequence pName){
        super(GraphicsWrapper.getUniformLocation(pProgram.id, pName));
        if(this.id==-1){
            throw new UniformException("ShaderProgram ["+pProgram+"] does not support uniform ["+pName+"]");
        }
    }

    public void set(T pVal){
        GraphicsWrapper.putUniformValue(this.id, pVal);
    }

}
