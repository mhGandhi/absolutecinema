package net.absolutecinema.rendering.meshes;

import net.absolutecinema.rendering.GLObject;
import net.absolutecinema.rendering.GraphicsWrapper;

public class Vao extends GLObject {

    public Vao() {
        super(GraphicsWrapper.genVAO());
    }

    public void bind(){
        GraphicsWrapper.bindVAO(this.id);
    }
}
