package net.absolutecinema.rendering.meshes;

public class VertexNormalMesh extends Mesh{

    public VertexNormalMesh() {
        super(new BufferWrapper(6));
        gpuBuffer.addField(3,false);//Vertex Coordinate
        gpuBuffer.addField(3,true);//Vertex Normal
    }
}
