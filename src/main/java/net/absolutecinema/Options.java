package net.absolutecinema;

public class Options {
    //FPS cap
    private int OP_fpsCap = 0; //FPS-Cap and Frame Time of 0 -> VSync
    private double frameTime = 0d;

    public void setFpsCap(int pTargetFps){
        pTargetFps = Math.max(pTargetFps,0);

        OP_fpsCap = pTargetFps;
        frameTime = pTargetFps == 0 ? 0d : 1.0d/pTargetFps;
    }
    public int getFpsCap(){
        return OP_fpsCap;
    }
    public double getTargetFrameTime(){
        return this.frameTime;
    }

    //FOV
    private float OP_fov = Constants.DEFAULT_FOV;

    public void setFov(float pFov){
        pFov = clamp(pFov, Constants.MIN_FOV, Constants.MAX_FOV);

        this.OP_fov = pFov;
    }
    public float getFov(){
        return this.OP_fov;
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private int clamp(int pNum, int pMin, int pMax){
        if(pNum<pMin)return pMin;
        return Math.min(pNum, pMax);
    }
    private float clamp(float pNum, float pMin, float pMax){
        if(pNum<pMin)return pMin;
        return Math.min(pNum, pMax);
    }
}
