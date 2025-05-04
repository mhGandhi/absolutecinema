package net.absolutecinema.rendering.meshes;

import net.absolutecinema.rendering.shader.FieldType;

public class VertexNormalMesh extends Mesh{

    public VertexNormalMesh() {
        super(new BufferWrapper(6));
        gpuBuffer.addField(3, FieldType.FLOAT,false);//Vertex Coordinate
        gpuBuffer.addField(3, FieldType.FLOAT, true);//Vertex Normal
    }
}
