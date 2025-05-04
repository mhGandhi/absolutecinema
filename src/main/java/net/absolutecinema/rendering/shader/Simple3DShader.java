package net.absolutecinema.rendering.shader;

import net.absolutecinema.Constants;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Simple3DShader extends ShaderProgram{
    public Simple3DShader(){//todo replace with class dynamically generating fields from file

    }

    @Override
    protected void assignUnis() {
        super.assignUnis();

        addUni(Constants.VIEW_MAT_UNI, Constants.IDENTITY_4F);
        addUni(Constants.PROJECTION_MAT_UNI, Constants.IDENTITY_4F);
        addUni(Constants.MODEL_MAT_UNI, Constants.IDENTITY_4F);
        addUni(Constants.CAMERA_POS_UNI, Constants.ZERO_VEC3F);

        addLayoutEntry(new LayoutEntry("vertex coordinates" , 3, FieldType.FLOAT, false));
        addLayoutEntry(new LayoutEntry("vertex normal"      , 3, FieldType.FLOAT, true));

        viewMat = (Uni<Matrix4f>) getUni(Constants.VIEW_MAT_UNI);
        projMat = (Uni<Matrix4f>) getUni(Constants.PROJECTION_MAT_UNI);
        modelMat = (Uni<Matrix4f>) getUni(Constants.MODEL_MAT_UNI);
        camPos = (Uni<Vector3f>) getUni(Constants.CAMERA_POS_UNI);
    }

    public Uni<Matrix4f> viewMat;
    public Uni<Matrix4f> projMat;
    public Uni<Matrix4f> modelMat;
    public Uni<Vector3f> camPos;
}
