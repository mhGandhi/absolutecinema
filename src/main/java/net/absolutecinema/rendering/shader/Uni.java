package net.absolutecinema.rendering.shader;

import net.absolutecinema.rendering.GLObject;
import net.absolutecinema.rendering.OpenGLWrapper;

public class Uni<T> extends GLObject {
    public Uni(ShaderProgram pProgram, CharSequence pName){
        super(OpenGLWrapper.getUniformLocation(pProgram.id, pName));
    }

    public void set(T pVal){
        OpenGLWrapper.putUniformValue(this.id, pVal);
    }

}
