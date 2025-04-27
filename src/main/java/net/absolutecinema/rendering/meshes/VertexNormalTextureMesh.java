package net.absolutecinema.rendering.meshes;

public class VertexNormalTextureMesh extends Mesh{

    public VertexNormalTextureMesh() {
        super(new BufferWrapper(8));
        gpuBuffer.addField(3,false);//Vertex Coordinate
        gpuBuffer.addField(3,true);//Vertex Normal
        gpuBuffer.addField(2, false);//UV-coordinates
    }

    //todo what about the texture itself?
    //todo how to decide which shader?
}
