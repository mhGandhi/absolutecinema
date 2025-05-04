package net.absolutecinema.rendering;


import org.joml.Matrix4f;
import org.joml.Vector3f;

public enum UniformType {
    MAT4F(Matrix4f.class),
    VEC3F(Vector3f.class);

    public final Class<?> aClass;

    private UniformType(Class<?> pAClass){
        this.aClass = pAClass;
    }

    public static UniformType fromClass(Class<?> pClass){
        for (UniformType type: values()) {
            if(type.aClass.equals(pClass))return type;
        }
        return null;
    }
}
