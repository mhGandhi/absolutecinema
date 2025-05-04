package net.absolutecinema.rendering.shader.programs;

import net.absolutecinema.Constants;
import net.absolutecinema.rendering.shader.FieldType;
import net.absolutecinema.rendering.shader.LayoutEntry;
import net.absolutecinema.rendering.shader.Uni;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class DefaultObjShader extends ModelShader {
    //todo replace with class dynamically generating fields from file

    @Override
    protected void assignUnis() {
        super.assignUnis();

        viewMat = (Uni<Matrix4f>) addUni(Constants.VIEW_MAT_UNI, Constants.IDENTITY_4F);
        projMat = (Uni<Matrix4f>) addUni(Constants.PROJECTION_MAT_UNI, Constants.IDENTITY_4F);
        camPos = (Uni<Vector3f>) addUni(Constants.CAMERA_POS_UNI, Constants.ZERO_VEC3F);

        addLayoutEntry(new LayoutEntry(Constants.VERT_COORDINATE_LAYOUT_FIELD , 3, FieldType.FLOAT, false));
        addLayoutEntry(new LayoutEntry(Constants.VERT_NORMAL_LAYOUT_FIELD      , 3, FieldType.FLOAT, true));
    }

    public Uni<Matrix4f> viewMat;
    public Uni<Matrix4f> projMat;
    public Uni<Vector3f> camPos;
}
