package net.absolutecinema.rendering.shader.programs;

import net.absolutecinema.Constants;
import net.absolutecinema.rendering.shader.FieldType;
import net.absolutecinema.rendering.shader.LayoutEntry;
import net.absolutecinema.rendering.shader.Uni;
import org.joml.Vector3f;

import java.util.List;

public class ColoredObjShader extends DefaultObjShader {
    @Override
    protected boolean assignUnis() {
        boolean superSuccessful = super.assignUnis();

        color = (Uni<Vector3f>) addUni(Constants.BASE_COLOR_UNI, Constants.ZERO_VEC3F);

        return color!=null && superSuccessful;
    }

    public Uni<Vector3f> color;
}
