package net.absolutecinema.rendering.shader;

import net.absolutecinema.rendering.OpenGLWrapper;

public class Uni<T> {
    public final int location;

    public Uni(ShaderProgram pProgram, CharSequence pName){
        this.location = OpenGLWrapper.getUniformLocation(pProgram.id, pName);
    }

    public void set(T pVal){
        OpenGLWrapper.putUniformValue(this.location, pVal);
    }

}
