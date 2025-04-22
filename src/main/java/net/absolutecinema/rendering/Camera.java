package net.absolutecinema.rendering;

import org.joml.Matrix4f;
import org.joml.Vector3f;

//todo provisorisch
public class Camera {
    private static final float PITCH_MIN = (float) (-Math.PI / 2); // -90 degrees in radians
    private static final float PITCH_MAX = (float) (Math.PI / 2);  // 90 degrees in radians


    private float x, y, z;
    private float pitch, yaw, roll;

    private final float sensitivity = 0.002f;

    public Camera() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.pitch = 0.0f;
        this.yaw = 0.0f;
        this.roll = 0.0f;
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getZ() { return z; }

    public void setX(float pX){if(!Float.isNaN(pX))this.x = pX;}
    public void setY(float pY){if(!Float.isNaN(pY))this.y = pY;}
    public void setZ(float pZ){if(!Float.isNaN(pZ))this.z = pZ;}

    public float getPitch(){return this.pitch;}
    public float getYaw(){return this.yaw;}
    public float getRoll(){return this.roll;}

    public void setPitch(float pPitch) {
        this.pitch = Math.max(PITCH_MIN, Math.min(PITCH_MAX, pPitch));
    }
    public void setYaw(float pYaw) {
        this.yaw = (float) (pYaw%(2*Math.PI));//todo cap that
    }
    public void setRoll(float pRoll) {
        this.roll = (float) (pRoll%(2*Math.PI));
    }

    public void translate(float pX, float pY, float pZ){
        setX(getX()+pX);
        setY(getY()+pY);
        setZ(getZ()+pZ);
    }

    public void moveForward(float pM){
        Vector3f translation = (getForward().normalize().mul(pM));
        translate(translation.x, translation.y, translation.z);
    }
    public void moveRight(float pM){
        Vector3f translation = (getRight().normalize().mul(pM));
        //System.out.println(getRight());
        //System.out.println(translation);
        translate(translation.x, translation.y, translation.z);
    }
    public void moveUp(float pM){
        Vector3f translation = (getUp().normalize().mul(pM));
        translate(translation.x, translation.y, translation.z);
    }

    public Vector3f getPos(){
        return new Vector3f(getX(),getY(),getZ());
    }

    public Vector3f getWorldUp(){
        return new Vector3f(0,1,0);
    }

    public Vector3f getUp(){
        Vector3f up = getWorldUp();
        up.rotateX(getPitch()).rotateY(getYaw()).rotateZ(getRoll());
        return up;
    }
    public Vector3f getForward(){
        Vector3f forward = new Vector3f(0,0,-1);
        forward.rotateX(getPitch()).rotateY(getYaw()).rotateZ(getRoll());
        return forward;
    }
    public Vector3f getRight(){
        return getForward().cross(getUp());
    }

    public Vector3f getLookAt() {
        return new Vector3f(getPos()).add(getForward());
    }

    // Rotation methods (change orientatigiton of the camera)
    public void yaw(float delta) { setYaw(getYaw() + delta * sensitivity) ; }
    public void pitch(float delta) { setPitch(getPitch() - delta * sensitivity); }
    public void roll(float delta){setRoll(getRoll()+ delta * sensitivity); }

    public Matrix4f getViewMatrix() {
        Matrix4f view = new Matrix4f();
        view.lookAt(getPos(), getLookAt(), getUp());
        return view;
    }

    public Matrix4f getProjectionMatrix(float fov, float aspectRatio, float near, float far) {
        Matrix4f projection = new Matrix4f();
        projection.perspective(fov, aspectRatio, near, far);
        return projection;
    }

    @Override
    public String toString() {
        return String.format("Camera{[%.2f|%.2f|%.2f] [p=%.2f|y=%.2f|r=%.2f]}",
                x, y, z,
                Math.toDegrees(getPitch()), Math.toDegrees(getYaw()), Math.toDegrees(getRoll()));
    }
}

