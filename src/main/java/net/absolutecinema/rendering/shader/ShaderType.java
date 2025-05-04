package net.absolutecinema.rendering.shader;

import java.io.File;
import java.nio.file.Path;

import static net.absolutecinema.AbsoluteCinema.LOGGER;

public enum ShaderType {
    VERTEX("vertex", ".vert"),
    FRAGMENT("fragment", ".frag");

    public final String name;
    public final String extension;

    private ShaderType(final String pName, final String pExtension) {
        this.name = pName;
        this.extension = pExtension;
    }

    public static ShaderType byPath(Path pPath) {
        for (ShaderType shaderType : values()) {
            if (pPath.toString().endsWith(shaderType.extension)) {
                LOGGER.info("Shader at ["+pPath.getFileName()+"] is of type "+shaderType.name);
                return shaderType;
            }
        }

        return null;
    }
}
