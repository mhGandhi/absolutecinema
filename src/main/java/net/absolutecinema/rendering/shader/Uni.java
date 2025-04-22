package net.absolutecinema.rendering.shader;

import net.absolutecinema.rendering.GLObject;
import net.absolutecinema.rendering.GraphicsWrapper;

public class Uni<T> extends GLObject {
    public Uni(ShaderProgram pProgram, CharSequence pName){
        super(GraphicsWrapper.getUniformLocation(pProgram.id, pName));
    }

    public void set(T pVal){
        GraphicsWrapper.putUniformValue(this.id, pVal);
    }

}
