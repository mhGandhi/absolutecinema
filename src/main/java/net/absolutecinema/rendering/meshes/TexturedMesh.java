package net.absolutecinema.rendering.meshes;

import net.absolutecinema.rendering.Texture;
import net.absolutecinema.rendering.shader.programs.ShaderProgram;
import org.w3c.dom.Text;

public class TexturedMesh extends Mesh{
    private final Texture texture;

    public Texture getTexture(){
        return this.texture;
    }

    public TexturedMesh(ShaderProgram pShaderProgram, Texture pTexture) {
        super(pShaderProgram);
        this.texture = pTexture;
    }

    @Override
    public void draw() {
        getTexture().bind();
        super.draw();
    }
}
