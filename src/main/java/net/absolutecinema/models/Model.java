package net.absolutecinema.models;

import net.absolutecinema.ImportUtil;
import net.absolutecinema.rendering.meshes.Mesh;
import net.absolutecinema.rendering.shader.programs.ModelShader;
import net.absolutecinema.rendering.shader.programs.ShaderProgram;
import net.absolutecinema.rendering.shader.Uni;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.Assimp;

import java.nio.file.Path;

import static net.absolutecinema.AbsoluteCinema.shaderManager;

public class Model {
    private final Matrix4f relModelMat;
    private final Mesh mesh;
    private final ModelShader shaderProgram;

    private Model parent;

    private final Uni<Matrix4f> modelMatUni;

    public Model(Mesh pMesh, ModelShader pShader, Model pParent){//todo change that shit
        this.mesh = pMesh;
        this.shaderProgram = pShader;
        this.parent = pParent;

        this.modelMatUni = pShader.modelMat;

        relModelMat = new Matrix4f().identity();
    }

    public void setRotation(float pitch, float yaw, float roll){
        relModelMat.setRotationXYZ(pitch, yaw, roll);
    }

    public void setPos(Vector3f pos){
        relModelMat.setTranslation(pos);
    }

    public void setScale(Vector3f scale){
        relModelMat.scale(scale);
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

    public void setParent(Model pPar){
        this.parent = pPar;
    }

    public void draw(){
        getShaderProgram().use();//todo fix + batch up
        modelMatUni.set(getRelModelMat());
        getMesh().draw();
    }

    public static Model fromFile(Path pPath) {
        String modelPath = pPath.toAbsolutePath().toString();

        AIScene scene = Assimp.aiImportFile(modelPath,
                Assimp.aiProcess_Triangulate |
                        Assimp.aiProcess_JoinIdenticalVertices |
                        Assimp.aiProcess_GenSmoothNormals |
                        Assimp.aiProcess_FlipUVs);

        if (scene == null || (scene.mFlags() & Assimp.AI_SCENE_FLAGS_INCOMPLETE) != 0 || scene.mRootNode() == null) {
            throw new RuntimeException("Error loading model: " + Assimp.aiGetErrorString());
        }

        AIMesh mesh = AIMesh.create(scene.mMeshes().get(0)); // First mesh only for simplicity

        //boolean hasNormals = mesh.mNormals() != null;
        //boolean hasTexCoords = mesh.mTextureCoords(0) != null;

        // Determine shader based on available data
        ModelShader shaderProgram = (ModelShader) shaderManager.getShaderProgram("norm");//todo

        // Load mesh
        Mesh convertedMesh = shaderProgram.newCompatibleMesh();
        float[] vert = ImportUtil.trisFromObj(pPath); //todo
        convertedMesh.assignVertices(vert);

        Assimp.aiReleaseImport(scene);

        return new Model(convertedMesh, shaderProgram, null);
    }
}
