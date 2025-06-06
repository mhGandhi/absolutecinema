package net.absolutecinema.rendering.meshes;

import net.absolutecinema.rendering.shader.programs.ColoredObjShader;
import net.absolutecinema.rendering.shader.programs.ShaderProgram;
import org.joml.Vector3f;

public class ColoredMesh extends Mesh{
    private final Vector3f color;

    public Vector3f getColor(){
        return this.color;
    }

    public ColoredMesh(ColoredObjShader pShaderProgram, Vector3f pColor) {
        super(pShaderProgram);
        color = pColor;
    }

    @Override
    public void draw() {
        ((ColoredObjShader)getShaderProgram()).color.set(color);
        super.draw();
    }
}
