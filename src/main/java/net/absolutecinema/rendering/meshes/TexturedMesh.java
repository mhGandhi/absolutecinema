package net.absolutecinema.rendering.meshes;

import net.absolutecinema.rendering.GraphicsWrapper;
import net.absolutecinema.rendering.Texture;
import net.absolutecinema.rendering.shader.programs.ShaderProgram;
import net.absolutecinema.rendering.shader.programs.TexturedObjShader;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GLXEXTTextureFromPixmap;
import org.w3c.dom.Text;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class TexturedMesh extends Mesh{
    private final Texture texture;

    public Texture getTexture(){
        return this.texture;
    }

    public TexturedMesh(TexturedObjShader pShaderProgram, Texture pTexture) {
        super(pShaderProgram);
        glActiveTexture(GL_TEXTURE0);//Todo extract
        this.texture = pTexture;
    }

    @Override
    public void draw() {
        getTexture().bind();
        super.draw();
    }
}
