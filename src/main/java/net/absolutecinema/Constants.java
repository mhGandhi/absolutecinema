package net.absolutecinema;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Constants {
    public static final String SHADER_FOLDER_NAME = "shader";

    public static final String TESTING_SHADER_NAME = "default";

    public static final String VIEW_MAT_UNI = "viewMat";
    public static final String MODEL_MAT_UNI = "modelMat";
    public static final String PROJECTION_MAT_UNI = "projectionMat";
    public static final String CAMERA_POS_UNI = "cameraPos";

    public static final String VERT_COORDINATE_LAYOUT_FIELD = "vertexCoordinate";
    public static final String VERT_NORMAL_LAYOUT_FIELD = "vertexNormal";
    public static final String UV_COORDINATE_LAYOUT_FIELD = "uvCoordinate";

    public static final Matrix4f IDENTITY_4F = new Matrix4f().identity();
    public static final Vector3f ZERO_VEC3F = new Vector3f().zero();

    public static final float DEFAULT_FOV = 90f;//should be between min and maxs
    public static final float MIN_FOV = 10f;//limits only used for options
    public static final float MAX_FOV = 160f;
}
