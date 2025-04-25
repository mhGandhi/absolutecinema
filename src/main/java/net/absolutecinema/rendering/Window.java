package net.absolutecinema.rendering;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    public final long id;

    public Window(){
        this.id = GraphicsWrapper.createWindow(800, 600, "ABSOLUTE CINEMA", NULL, NULL);
    }


    public void show(){
        GraphicsWrapper.showWindow(this.id);
    }

    public void select(){
        GraphicsWrapper.makeWinContextCurrent(this.id);
    }

    public void enableVsync(){
        GraphicsWrapper.swapInterval(1);
    }

    public boolean shouldClose(){
        return glfwWindowShouldClose(this.id);
    }

    public void swapBuffers() {
        GraphicsWrapper.swapBuffers(this.id);
    }

    public void setTitle(String pTitle){
        GraphicsWrapper.setWindowTitle(this.id, pTitle);
    }
}
