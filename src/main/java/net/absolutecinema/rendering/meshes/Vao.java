package net.absolutecinema.rendering.meshes;

import net.absolutecinema.rendering.GLObject;
import net.absolutecinema.rendering.GraphicsWrapper;

import static net.absolutecinema.AbsoluteCinema.LOGGER;

public class Vao extends GLObject {

    public Vao() {
        super(GraphicsWrapper.genVAO());
    }

    public void bind(){
        if(GraphicsWrapper.getBoundVAO()!=this.id) {
            GraphicsWrapper.bindVAO(this.id);
        }//else{
            //LOGGER.info("VAO "+this.id+" already bound");
        //}
    }
}
