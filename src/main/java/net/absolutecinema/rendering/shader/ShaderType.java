package net.absolutecinema.rendering.shader;

public enum ShaderType {
    VERTEX("vertex", ".vert"),
    FRAGMENT("fragment", ".frag");

    public final String name;
    public final String extension;

    private ShaderType(final String pName, final String pExtension) {
        this.name = pName;
        this.extension = pExtension;
    }
}
