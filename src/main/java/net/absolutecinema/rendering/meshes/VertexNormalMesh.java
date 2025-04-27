package net.absolutecinema.rendering.meshes;

public class VertexNormalMesh extends Mesh{

    public VertexNormalMesh() {
        super(new BufferWrapper(6));
        gpuBuffer.addField(3,false);
        gpuBuffer.addField(3,true);
    }
}
