package net.absolutecinema.rendering.meshes;

import net.absolutecinema.rendering.GLObject;
import net.absolutecinema.rendering.GraphicsWrapper;

import static net.absolutecinema.AbsoluteCinema.LOGGER;

public class Vbo extends GLObject {

    public Vbo() {
        super(GraphicsWrapper.genVBO());
    }

    public void bind(){
        if(GraphicsWrapper.getBoundVBO()!=this.id) {
            GraphicsWrapper.bindVBO(this.id);
        }//else{
            //LOGGER.info("VBO "+this.id+" already bound");
        //}
    }
}
