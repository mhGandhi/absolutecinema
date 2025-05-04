package net.absolutecinema.rendering.shader;

import net.absolutecinema.Constants;

public class StaticShaderPrg extends ShaderProgram{
    public StaticShaderPrg(){//todo replace with class dynamically generating fields from file

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
    }
}
