package net.absolutecinema.models;

import net.absolutecinema.rendering.meshes.Mesh;
import net.absolutecinema.rendering.shader.ShaderProgram;
import net.absolutecinema.rendering.shader.Uni;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static net.absolutecinema.AbsoluteCinema.LOGGER;

public class Model {
    private final Matrix4f relModelMat;
    private final Mesh mesh;
    private final ShaderProgram shaderProgram;

    private final Model parent;

    private final Uni<Matrix4f> modelMatUni;

    public Model(Mesh pMesh, ShaderProgram pShader, Model pParent, Uni<Matrix4f> pModelMatUni){//todo change that shit
        this.mesh = pMesh;
        this.shaderProgram = pShader;
        this.parent = pParent;

        this.modelMatUni = pModelMatUni;

        relModelMat = new Matrix4f().identity();
    }

    public void setRotation(float pitch, float yaw, float roll){
        relModelMat.setRotationXYZ(pitch, yaw, roll);
    }

    public void setPos(Vector3f pos){
        relModelMat.setTranslation(pos);
    }

    public Matrix4f getRelModelMat(){
        if(getParent() == null)
        return relModelMat;
        else {
            return new Matrix4f(getParent().getRelModelMat()).mul(relModelMat);
            //return getParent().getRelModelMat().mul(relModelMat);
            //return relModelMat.mul(getParent().getRelModelMat());
        }
    }

    public Mesh getMesh(){
        return this.mesh;
    }

    public ShaderProgram getShaderProgram(){
        return this.shaderProgram;
    }

    public Model getParent(){
        return this.parent;
    }

    public void draw(){
        shaderProgram.use();
        modelMatUni.set(getRelModelMat());
        getMesh().draw();
    }
}
