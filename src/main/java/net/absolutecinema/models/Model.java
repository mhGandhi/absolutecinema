package net.absolutecinema.models;

import net.absolutecinema.AbsoluteCinema;
import net.absolutecinema.Constants;
import net.absolutecinema.rendering.RenderException;
import net.absolutecinema.rendering.meshes.ColoredMesh;
import net.absolutecinema.rendering.meshes.Mesh;
import net.absolutecinema.rendering.meshes.TexturedMesh;
import net.absolutecinema.rendering.shader.LayoutEntry;
import net.absolutecinema.rendering.shader.Shader;
import net.absolutecinema.rendering.shader.programs.ColoredObjShader;
import net.absolutecinema.rendering.shader.programs.ModelShader;
import net.absolutecinema.rendering.shader.programs.ShaderProgram;
import net.absolutecinema.rendering.shader.Uni;
import net.absolutecinema.rendering.shader.programs.TexturedObjShader;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.assimp.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static net.absolutecinema.AbsoluteCinema.LOGGER;
import static net.absolutecinema.AbsoluteCinema.shaderManager;

public class Model {
    private final Matrix4f relModelMat;
    private final Collection<Mesh> meshes;
    private final ModelShader shaderProgram;

    private Model parent;

    private Model(Mesh pMesh, ModelShader pShader){
        this(List.of(pMesh), pShader);
    }

    private Model(Collection<Mesh> pMeshes, ModelShader pShader){//todo change that shit
        this.meshes = pMeshes;
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

    public Collection<Mesh> getMeshes(){
        return this.meshes;
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

    public static Model fromFile(Path pPath) throws Exception {
        return fromFile(pPath, null);
    }

    public static Model fromFile(Path pPath, ModelShader pShader) throws Exception {
        // If pPath is a directory, find the first .obj file inside
        if (Files.isDirectory(pPath)) {
            try {
                Path finalPPath = pPath;
                pPath = Files.walk(pPath, 1)
                        .filter(p -> p.toString().toLowerCase().endsWith(".obj"))
                        .findFirst()
                        .orElseThrow(() -> new IOException("No .obj file found in folder: " + finalPPath));
            } catch (IOException e) {
                throw new RuntimeException("Error scanning directory for .obj file: " + e.getMessage(), e);
            }
        }

        String modelPath = pPath.toAbsolutePath().toString();

        AIScene scene = Assimp.aiImportFile(modelPath,
                Assimp.aiProcess_Triangulate |
                        Assimp.aiProcess_JoinIdenticalVertices |
                        Assimp.aiProcess_GenNormals |
                        Assimp.aiProcess_FlipUVs);

        if (scene == null || (scene.mFlags() & Assimp.AI_SCENE_FLAGS_INCOMPLETE) != 0 || scene.mRootNode() == null) {
            throw new RuntimeException("Error loading model: " + Assimp.aiGetErrorString());
        }

        List<Mesh> allMeshes = new LinkedList<>();

        for(int i = 0; i<scene.mNumMeshes(); i++){
            AIMesh mesh = AIMesh.create(scene.mMeshes().get(i));
            AIMaterial material = AIMaterial.create(scene.mMaterials().get(mesh.mMaterialIndex()));

            Mesh convertedMesh = convertMesh(mesh, material, pShader, pPath);
            if(convertedMesh==null)continue;

            ModelShader modelShaderProg = (ModelShader) convertedMesh.getShaderProgram();
            convertedMesh.assignVertices(meshToArr(mesh, modelShaderProg));

            allMeshes.add(convertedMesh);
        }


        Assimp.aiReleaseImport(scene);
        return new Model(allMeshes,(ModelShader) allMeshes.get(0).getShaderProgram());
    }

    private static Mesh convertMesh(AIMesh pAIMesh, AIMaterial pMaterial, ShaderProgram pShader, Path pPath) throws Exception {
        AIColor4D color = AIColor4D.create();

        AIString matName = AIString.calloc();
        Assimp.aiGetMaterialString(pMaterial, Assimp.AI_MATKEY_NAME, 0, 0, matName);

        boolean hasNormals = pAIMesh.mNormals() != null;
        boolean hasTexCoords = pAIMesh.mTextureCoords(0) != null;
        boolean hasDiffuseColor = Assimp.aiGetMaterialColor(pMaterial, Assimp.AI_MATKEY_COLOR_DIFFUSE, 0, 0, color) == 0;
        boolean isDefaultDiffuse = color.r() == 0.6f && color.g() == 0.6f && color.b() == 0.6f;
        boolean hasSpecificMaterialProbably = hasDiffuseColor && !isDefaultDiffuse;
        boolean hasColor = hasSpecificMaterialProbably && (Assimp.aiGetMaterialColor(pMaterial, Assimp.AI_MATKEY_COLOR_DIFFUSE, 0, 0, color)==0);

        LOGGER.debug("MODEL "+pPath+(hasNormals?" -normals ":"")+(hasTexCoords?" -txCoords ":"")+(hasSpecificMaterialProbably?" -material ":"")+(hasColor?(" -col:["+color.r()+"|"+color.g()+"|"+color.b()+"]"):""));

        //System.out.println("norm "+hasNormals);
        //System.out.println("tex "+hasTexCoords);

        ShaderProgram shaderProgram = null;
        if(pShader != null){
            shaderProgram = pShader;
        }else{
            if(hasTexCoords /*todo && hasTexture*/){
                try {
                    shaderProgram = shaderManager.getShaderProgram(Constants.TEXTURE_MODEL_SHADER_NAME);
                    LOGGER.debug("TEXTURE "+pPath.toString());
                } catch (RenderException e) {
                    LOGGER.fatal(e.getMessage());
                }
            }
            if(shaderProgram==null && hasColor){
                try {
                    shaderProgram = shaderManager.getShaderProgram(Constants.COLORED_MODEL_SHADER_NAME);
                    LOGGER.debug("COLOR "+pPath.toString());
                } catch (RenderException e) {
                    LOGGER.fatal(e.getMessage());
                }
            }
            if(shaderProgram==null){
                try {
                    shaderProgram = shaderManager.getShaderProgram(Constants.DEFAULT_MODEL_SHADER_NAME);
                    LOGGER.debug("DEFAULT "+pPath.toString());
                } catch (RenderException e) {
                    LOGGER.fatal(e.getMessage());
                }
            }

            if(shaderProgram==null)throw new Exception("NO FITTING SHADER FOUND FOR MODEL AT "+pPath.toString());
            // Determine shader based on available data //todo
        }
        ModelShader modelShaderProg = (ModelShader) shaderProgram;//todo except mby?

        // Load mesh
        //todo
        Mesh convertedMesh;
        if(modelShaderProg instanceof TexturedObjShader tos){
            convertedMesh = new TexturedMesh(tos, AbsoluteCinema.testTexture);
        }else if(modelShaderProg instanceof ColoredObjShader cos){
            convertedMesh = new ColoredMesh(cos, new Vector3f(color.r(),color.g(),color.b()));
        } else{
            convertedMesh = new Mesh(modelShaderProg);
        }

        return convertedMesh;
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
