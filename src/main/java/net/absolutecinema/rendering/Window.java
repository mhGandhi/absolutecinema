package net.absolutecinema.rendering;

import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    public final long id;

    private boolean vsync;

    public boolean vsyncEnabled() {
        return vsync;
    }

    public Window(){
        this.id = GraphicsWrapper.createWindow(800, 600, "ABSOLUTE CINEMA", NULL, NULL);
        vsync = false;
    }


    public void show(){
        GraphicsWrapper.showWindow(this.id);
    }

    public void select(){
        GraphicsWrapper.makeWinContextCurrent(this.id);
    }

    public void enableVsync(){
        //if(!vsync){
            if(GraphicsWrapper.getContextWindow()!=this.id){
                select();
            }
            GraphicsWrapper.swapInterval(1);
        //}

    }
    public void disableVsync(){
        //if(vsync){
            if(GraphicsWrapper.getContextWindow()!=this.id){
                select();
            }
            GraphicsWrapper.swapInterval(0);
        //}
    }

    public boolean shouldClose(){
        return GraphicsWrapper.windowShouldClose(this.id);
    }

    public void swapBuffers() {
        GraphicsWrapper.swapBuffers(this.id);
    }

    public void setTitle(String pTitle){
        GraphicsWrapper.setWindowTitle(this.id, pTitle);
    }
}
