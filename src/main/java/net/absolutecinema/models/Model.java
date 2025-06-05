package net.absolutecinema.models;

import net.absolutecinema.AbsoluteCinema;
import net.absolutecinema.Constants;
import net.absolutecinema.rendering.meshes.Mesh;
import net.absolutecinema.rendering.meshes.TexturedMesh;
import net.absolutecinema.rendering.shader.LayoutEntry;
import net.absolutecinema.rendering.shader.programs.ModelShader;
import net.absolutecinema.rendering.shader.programs.ShaderProgram;
import net.absolutecinema.rendering.shader.Uni;
import net.absolutecinema.rendering.shader.programs.TexturedObjShader;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.assimp.*;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static net.absolutecinema.AbsoluteCinema.LOGGER;
import static net.absolutecinema.AbsoluteCinema.shaderManager;

public class Model {
    private final Matrix4f relModelMat;
    private final Mesh mesh;
    private final ModelShader shaderProgram;

    private Model parent;

    private Model(Mesh pMesh, ModelShader pShader){//todo change that shit
        this.mesh = pMesh;
        this.shaderProgram = pShader;
        this.parent = null;

        if (pShader.modelMat == null)
            throw new IllegalStateException("Shader program missing modelMat uniform: " + pShader);

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

    public Matrix4f calcRelModelMat(){
        if(getParent() == null) return relModelMat;
        else {
            return new Matrix4f(getParent().calcRelModelMat()).mul(relModelMat);
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

        //System.out.println("norm "+hasNormals);
        //System.out.println("tex "+hasTexCoords);

        ShaderProgram shaderProgram;
        if(pShader != null){
            shaderProgram = pShader;
        }else{
            if(mesh.mTextureCoords(0)==null){
                System.out.println("NOTEX "+pPath.toString());
                shaderProgram = shaderManager.getShaderProgram(Constants.DEFAULT_MODEL_SHADER_NAME);
            }else{
                System.out.println("TEXTURE "+pPath.toString());
                shaderProgram = shaderManager.getShaderProgram(Constants.TEXTURE_MODEL_SHADER_NAME);
            }
            // Determine shader based on available data //todo
        }
        ModelShader modelShaderProg = (ModelShader) shaderProgram;//todo except mby?

        // Load mesh
        //todo
        Mesh convertedMesh;
        if(modelShaderProg instanceof TexturedObjShader tos){
            convertedMesh = new TexturedMesh(tos, AbsoluteCinema.testTexture);
        }else{
            convertedMesh = new Mesh(modelShaderProg);
        }

        convertedMesh.assignVertices(meshToArr(mesh, modelShaderProg));

        Assimp.aiReleaseImport(scene);

        return new Model(convertedMesh, modelShaderProg);
    }

    private static float[] meshToArr(AIMesh pMesh, ShaderProgram pShader){//todo maybe offload somewhere else
        List<Float> data = new ArrayList<>();

        AIVector3D.Buffer vertices = pMesh.mVertices();
        AIVector3D.Buffer normals = pMesh.mNormals();
        AIFace.Buffer faces = pMesh.mFaces();
        AIVector3D.Buffer texCoords = pMesh.mTextureCoords(0); // 0 = first set of UVs

        boolean noNormals = normals == null;
        boolean noTexCoords = texCoords == null;

        if(noNormals){
            LOGGER.debug("No normals");
        }
        if(noTexCoords){
            LOGGER.debug("No Tex Coords");
        }

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
                        if(noNormals){
                            data.add(0f);
                            data.add(0f);
                            data.add(0f);
                            continue;
                        }
                        AIVector3D normal = normals.get(index);
                        data.add(normal.x());
                        data.add(normal.y());
                        data.add(normal.z());
                        continue;
                    }
                    if(le.name().equals(Constants.UV_COORDINATE_LAYOUT_FIELD)) {
                        if(noTexCoords){
                            data.add(0f);
                            data.add(0f);
                            continue;
                        }
                        AIVector3D uvV = texCoords.get(index);
                        data.add(uvV.x());
                        data.add(uvV.y());
                        continue;
                    }
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
