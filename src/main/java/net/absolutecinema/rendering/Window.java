package net.absolutecinema.rendering;

import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    public final long id;

    public Window(){
        this.id = OpenGLWrapper.createWindow(800, 600, "ABSOLUTE CINEMA", NULL, NULL);
    }


    public void show(){
        OpenGLWrapper.showWindow(this.id);
    }

    public void select(){
        OpenGLWrapper.makeWinContextCurrent(this.id);
    }

    public void enableVsync(){
        OpenGLWrapper.swapInterval(1);
    }

    public boolean shouldClose(){
        return glfwWindowShouldClose(this.id);
    }
}
