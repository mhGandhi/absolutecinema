package net.absolutecinema.rendering.shader.programs;

import net.absolutecinema.Constants;
import net.absolutecinema.rendering.shader.Uni;
import org.joml.Matrix4f;

public class ModelShader extends ShaderProgram {

    @Override
    protected void assignUnis() {
        super.assignUnis();

        modelMat = (Uni<Matrix4f>) addUni(Constants.MODEL_MAT_UNI, Constants.IDENTITY_4F);
    }

    public Uni<Matrix4f> modelMat;
}
