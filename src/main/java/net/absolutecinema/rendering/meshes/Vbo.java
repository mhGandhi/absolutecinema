package net.absolutecinema.rendering.meshes;

import net.absolutecinema.rendering.GLObject;
import net.absolutecinema.rendering.OpenGLWrapper;

public class Vbo extends GLObject {

    public Vbo() {
        super(OpenGLWrapper.genVBO());
    }

    public void bind(){
        OpenGLWrapper.bindVBO(this.id);
    }
}
