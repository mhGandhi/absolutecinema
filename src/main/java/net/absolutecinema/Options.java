package net.absolutecinema;

public class Options {
    private int OP_fpsCap = 0; //FPS-Cap and Frame Time of 0 -> VSync
    private double frameTime = 0d;

    public void setFpsCap(int pTargetFps){
        OP_fpsCap = pTargetFps;
        frameTime = pTargetFps == 0 ? 0d : 1.0d/pTargetFps;
    }
    public int getFpsCap(){
        return OP_fpsCap;
    }
    public double getTargetFrameTime(){
        return this.frameTime;
    }
}
