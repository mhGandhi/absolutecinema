package net.absolutecinema.rendering.meshes;

import net.absolutecinema.rendering.shader.FieldType;

public class VertexNormalTextureMesh extends Mesh{

    public VertexNormalTextureMesh() {
        super(new BufferWrapper(8));
        gpuBuffer.addField(3, FieldType.FLOAT,false);//Vertex Coordinate
        gpuBuffer.addField(3, FieldType.FLOAT,true);//Vertex Normal
        gpuBuffer.addField(2, FieldType.FLOAT, false);//UV-coordinates
    }

    //todo what about the texture itself?
    //todo how to decide which shader?
}
