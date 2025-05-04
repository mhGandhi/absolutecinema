package net.absolutecinema.models;

import net.absolutecinema.Constants;
import net.absolutecinema.rendering.meshes.Mesh;
import net.absolutecinema.rendering.shader.LayoutEntry;
import net.absolutecinema.rendering.shader.programs.ModelShader;
import net.absolutecinema.rendering.shader.programs.ShaderProgram;
import net.absolutecinema.rendering.shader.Uni;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.assimp.*;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static net.absolutecinema.AbsoluteCinema.shaderManager;

public class Model {
    private final Matrix4f relModelMat;
    private final Mesh mesh;
    private final ModelShader shaderProgram;

    private Model parent;

    private final Uni<Matrix4f> modelMatUni;

    public Model(Mesh pMesh, ModelShader pShader){//todo change that shit
        this.mesh = pMesh;
        this.shaderProgram = pShader;
        this.parent = null;

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

    public static Model fromFile(Path pPath){
        return fromFile(pPath, null);
    }

    public static Model fromFile(Path pPath, ModelShader pShader) {
        String modelPath = pPath.toAbsolutePath().toString();

        AIScene scene = Assimp.aiImportFile(modelPath,
                Assimp.aiProcess_Triangulate |
                        Assimp.aiProcess_JoinIdenticalVertices |
                        Assimp.aiProcess_GenNormals |
                        Assimp.aiProcess_FlipUVs);

        if (scene == null || (scene.mFlags() & Assimp.AI_SCENE_FLAGS_INCOMPLETE) != 0 || scene.mRootNode() == null) {
            throw new RuntimeException("Error loading model: " + Assimp.aiGetErrorString());
        }

        AIMesh mesh = AIMesh.create(scene.mMeshes().get(0)); // First mesh only for simplicity

        boolean hasNormals = mesh.mNormals() != null;
        boolean hasTexCoords = mesh.mTextureCoords(0) != null;

        ModelShader shaderProgram;
        if(pShader != null){
            shaderProgram = pShader;
        }else{
            // Determine shader based on available data //todo
            shaderProgram = null;
        }

        // Load mesh
        Mesh convertedMesh = shaderProgram.newCompatibleMesh();//todo mby store layout info within Mesh
        convertedMesh.assignVertices(meshToArr(mesh, shaderProgram));

        Assimp.aiReleaseImport(scene);

        return new Model(convertedMesh, shaderProgram);
    }

    private static float[] meshToArr(AIMesh pMesh, ShaderProgram pShader){//todo maybe offload somewhere else
        List<Float> data = new ArrayList<>();

        AIVector3D.Buffer vertices = pMesh.mVertices();
        AIVector3D.Buffer normals = pMesh.mNormals();
        AIFace.Buffer faces = pMesh.mFaces();
//        PointerBuffer uv = pMesh.mTextureCoords();

        for (int j = 0; j < faces.remaining(); j++) {
            AIFace face = faces.get(j);

            if (face.mNumIndices() != 3) {
                continue; // Skip non-triangle faces (shouldn't happen if aiProcess_Triangulate is used)
            }


            for (int k = 0; k < 3; k++) {
                int index = face.mIndices().get(k);
                for(LayoutEntry le : pShader.getLayout()){
                    if(le.name().equals(Constants.VERT_COORDINATE_LAYOUT_FIELD)){
                        AIVector3D vertex = vertices.get(index);
                        data.add(vertex.x());
                        data.add(vertex.y());
                        data.add(vertex.z());
                        continue;
                    }
                    if(le.name().equals(Constants.VERT_NORMAL_LAYOUT_FIELD)){
                        AIVector3D normal = normals.get(index);
                        data.add(normal.x());
                        data.add(normal.y());
                        data.add(normal.z());
                        continue;
                    }
//                    if(le.name().equals(Constants.UV_COORDINATE_LAYOUT_FIELD)){//todo
//                        AIVector2D uvV = uv.get(index);
//                        data.add(uv.x());
//                        data.add(uv.y());
//                        continue;
//                    }
                }
            }
        }

        // Convert List<Float> to float[]
        float[] result = new float[data.size()];
        for (int i = 0; i < data.size(); i++) {
            result[i] = data.get(i);
        }

        return result;
    }
}
