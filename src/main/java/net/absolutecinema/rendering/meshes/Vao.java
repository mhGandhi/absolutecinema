package net.absolutecinema.rendering.meshes;

import net.absolutecinema.rendering.GLObject;
import net.absolutecinema.rendering.OpenGLWrapper;

public class Vao extends GLObject {

    public Vao() {
        super(OpenGLWrapper.genVAO());
    }

    public void bind(){
        OpenGLWrapper.bindVAO(this.id);
    }
}
