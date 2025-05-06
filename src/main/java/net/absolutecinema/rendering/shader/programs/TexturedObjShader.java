package net.absolutecinema.rendering.shader.programs;

import net.absolutecinema.Constants;
import net.absolutecinema.rendering.Texture;
import net.absolutecinema.rendering.shader.FieldType;
import net.absolutecinema.rendering.shader.LayoutEntry;
import net.absolutecinema.rendering.shader.Uni;
import org.joml.Vector2f;

public class TexturedObjShader extends DefaultObjShader{
    @Override
    protected void assignUnis() {
        super.assignUnis();

        texture = (Uni<Texture>) addUni(Constants.TEXTURE_UNI, Texture.empty());

        addLayoutEntry(new LayoutEntry(Constants.UV_COORDINATE_LAYOUT_FIELD, 2, FieldType.FLOAT, false));
    }

    Uni<Texture> texture;
}
