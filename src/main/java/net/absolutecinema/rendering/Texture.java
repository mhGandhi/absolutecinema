package net.absolutecinema.rendering;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class Texture extends GLObject{

    public Texture(Path pTexturePath) {
        super(loadTexture(pTexturePath));
    }

    public static int loadTexture(Path pPath) {
        String filePath = pPath.toAbsolutePath().toString();

        int width, height;
        ByteBuffer image;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            // Flip the image vertically (because OpenGL expects 0,0 at bottom left)
            STBImage.stbi_set_flip_vertically_on_load(true);

            image = STBImage.stbi_load(filePath, w, h, channels, 4); // Force RGBA
            if (image == null) {
                throw new RuntimeException("Failed to load texture file: " + STBImage.stbi_failure_reason());
            }

            width = w.get();
            height = h.get();
        }

        // Create OpenGL texture
        int textureId = GL11.glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);

        // Upload the texture to the GPU
        GL11.glTexImage2D(GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0,
                GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, image);

        // Generate mipmaps
        GL30.glGenerateMipmap(GL_TEXTURE_2D);

        // Set texture parameters
        GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
        GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL13.GL_CLAMP_TO_BORDER);
        GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL13.GL_CLAMP_TO_BORDER);

        // Free the image memory
        STBImage.stbi_image_free(image);

        return textureId;
    }

    public void bind() {
        glActiveTexture(this.id);
        glBindTexture(GL_TEXTURE_2D, this.id);
    }

    private Texture(){
        super(-1);
    }

    public static Texture empty(){
        return new Texture();
    }
}
