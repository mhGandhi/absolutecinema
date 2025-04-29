package net.absolutecinema.models;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public interface Model {
    void draw();
    void setPos(Vector3f pPos);
    void setRotation(float pitch, float yaw, float roll);
    void setModelMat(Matrix4f mm);
}
