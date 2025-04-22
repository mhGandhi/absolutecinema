package net.absolutecinema.rendering.meshes;

import net.absolutecinema.rendering.GLObject;
import net.absolutecinema.rendering.GraphicsWrapper;

public class Vbo extends GLObject {

    public Vbo() {
        super(GraphicsWrapper.genVBO());
    }

    public void bind(){
        GraphicsWrapper.bindVBO(this.id);
    }
}
