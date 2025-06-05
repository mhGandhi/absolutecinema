package net.absolutecinema.rendering.shader.programs;

import net.absolutecinema.Constants;
import net.absolutecinema.rendering.Texture;
import net.absolutecinema.rendering.shader.FieldType;
import net.absolutecinema.rendering.shader.LayoutEntry;
import net.absolutecinema.rendering.shader.Uni;
import org.joml.Vector2f;

import static org.lwjgl.opengl.GL20.glUniform1i;

public class TexturedObjShader extends DefaultObjShader{
    @Override
    protected void assignUnis() {
        super.assignUnis();

        texture = (Uni<Texture>) addUni(Constants.TEXTURE_UNI, Texture.empty());

        addLayoutEntry(new LayoutEntry(Constants.UV_COORDINATE_LAYOUT_FIELD, 2, FieldType.FLOAT, false));
    }

    Uni<Texture> texture;

    public void setTexture(int pUnit){
        glUniform1i(texture.id, pUnit);//todo some tex unit stuff
    }
}
