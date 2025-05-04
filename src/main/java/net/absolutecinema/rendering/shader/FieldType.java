package net.absolutecinema.rendering.shader;

public enum FieldType {
    FLOAT(Float.BYTES),
    INTEGER(Integer.BYTES),
    BYTE(Byte.BYTES);

    public final int bytes;

    private FieldType(int pBytes){
        this.bytes = pBytes;
    }
}
