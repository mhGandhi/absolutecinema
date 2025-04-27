package net.absolutecinema.rendering;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    private static final float PITCH_MIN = (float) Math.toRadians(-89.0f);
    private static final float PITCH_MAX = (float) Math.toRadians(89.0f);

    private final Matrix4f viewMatrix = new Matrix4f();
    private final Matrix4f projectionMatrix = new Matrix4f().identity();
    private final Vector3f position = new Vector3f(0, 0, 0);
    private final Vector3f forward = new Vector3f(0, 0, -1);
    private final Vector3f up = new Vector3f(0, 1, 0);
    private final Vector3f right = new Vector3f(1, 0, 0);
    private final Vector3f center = new Vector3f();

    private float pitch = 0.0f;
    private float yaw = 0.0f;
    private float roll = 0.0f;

    private final float sensitivity = 0.002f;

    public Camera() {

    }

    public void moveForward(float distance) {
        position.add(forward.x * distance, forward.y * distance, forward.z * distance);
    }

    public void moveRight(float distance) {
        position.add(right.x * distance, right.y * distance, right.z * distance);
    }

    public void moveUp(float distance) {
        position.add(up.x * distance, up.y * distance, up.z * distance);
    }

    public void yaw(float deltaYaw) {
        yaw += deltaYaw * sensitivity;
        updateVectors();
    }

    public void pitch(float deltaPitch) {
        pitch -= deltaPitch * sensitivity;
        pitch = Math.max(PITCH_MIN, Math.min(PITCH_MAX, pitch));
        updateVectors();
    }

    public void roll(float deltaRoll) {
        roll += deltaRoll * sensitivity;
        updateVectors();
    }

    public Matrix4f getViewMatrix() {
        center.set(position).add(forward);
        viewMatrix.identity();
        viewMatrix.lookAt(position, center, up);
        return viewMatrix;
    }

    public Matrix4f getProjectionMatrix(float fov, float aspectRatio, float near, float far) {
        projectionMatrix.perspective(fov, aspectRatio, near, far);
        return projectionMatrix;
    }

    public Vector3f getPos() {
        return position;
    }

    private void updateVectors() {
        forward.x = (float) (Math.cos(pitch) * Math.sin(yaw));
        forward.y = (float) Math.sin(pitch);
        forward.z = (float) (Math.cos(pitch) * Math.cos(yaw));
        forward.normalize();

        float cosRoll = (float) Math.cos(roll);
        float sinRoll = (float) Math.sin(roll);

        right.set(forward).cross(0f, 1f, 0f).normalize();
        right.set(right.x * cosRoll - up.x * sinRoll, right.y * cosRoll - up.y * sinRoll, right.z * cosRoll - up.z * sinRoll);

        up.set(right).cross(forward).normalize();
    }
}