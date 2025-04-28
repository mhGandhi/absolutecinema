package net.absolutecinema.models;

import net.absolutecinema.rendering.meshes.Mesh;
import net.absolutecinema.rendering.shader.Uni;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Model {
    private final Matrix4f modelMat;
    private final Mesh mesh;
    private final Uni<Matrix4f> modelMatLoc;

    public Model(Mesh pMesh, Uni<Matrix4f> pModelMatLoc){//todo change that shit
        modelMat = new Matrix4f().identity();
        this.mesh = pMesh;
        this.modelMatLoc = pModelMatLoc;
    }

    public void setModelMat(Matrix4f pM){
        modelMat.set(pM);
    }

    public void setPos(Vector3f pos){
        modelMat.setTranslation(pos);
    }

    public Matrix4f getModelMat(){
        return modelMat;
    }

    public void draw(){
        modelMatLoc.set(modelMat);
        mesh.draw();
    }
}
