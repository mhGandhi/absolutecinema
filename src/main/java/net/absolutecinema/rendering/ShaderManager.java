package net.absolutecinema.rendering;

import net.absolutecinema.rendering.shader.Shader;
import net.absolutecinema.rendering.shader.Uni;
import net.absolutecinema.rendering.shader.UniformException;
import net.absolutecinema.rendering.shader.programs.ShaderProgram;
import net.absolutecinema.rendering.shader.ShaderType;
import org.joml.Matrix4f;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static net.absolutecinema.AbsoluteCinema.LOGGER;
import static net.absolutecinema.AbsoluteCinema.config;
import static net.absolutecinema.Constants.SHADER_FOLDER_NAME;

public class ShaderManager {
    private final Map<String, ShaderProgram> shaders;

    private ShaderProgram selectedShader;

    public ShaderManager(){
        shaders = new HashMap<>();
        selectedShader = null;
    }

    public ShaderProgram loadShader(String pSourceName, ShaderProgram pToLoad){
        if(!shaders.containsKey(pSourceName)) {
            try {
                shaders.put(pSourceName, shaderFromFileName(pSourceName, pToLoad));
            } catch (Exception e) {
                e.printStackTrace(LOGGER.getErrorStream());
                return null;
            }
        }else{
            LOGGER.warn("Shader ["+pSourceName+"] already loaded - returning existing one");//todo overwrite existing and check from outside
        }

        return shaders.get(pSourceName);
    }

    private ShaderProgram shaderFromFileName(String pFileName, ShaderProgram pTarget) throws IOException {
        Path shaderpath = config.assetDirectory().toPath().resolve(SHADER_FOLDER_NAME);

        if (!Files.isDirectory(shaderpath)) {
            throw new IllegalArgumentException("Provided path is not a directory: " + shaderpath);
        }

        List<Path> indShaders;
        try (var stream = Files.list(shaderpath)) {
            indShaders = stream
                    .filter(Files::isRegularFile)
                    .filter(path -> {
                        String fileName = path.getFileName().toString();
                        int dotIndex = fileName.lastIndexOf('.');
                        if (dotIndex == -1) return false;
                        return fileName.substring(0, dotIndex).equals(pFileName);
                    })
                    .toList();
        }

        for (Path sh : indShaders){
            pTarget.attach(
                    new Shader(
                            ShaderType.byPath(sh),
                            Files.readString(sh)
                    )
            );
        }

        pTarget.linkAndClearShaders();

        return pTarget;
    }

    public ShaderProgram getShaderProgram(String pKey){
        return this.shaders.get(pKey);
    }

    public ShaderProgram useShaderProgram(String pKey) {
        ShaderProgram ret = getShaderProgram(pKey);
        useShaderProgram(ret);
        return ret;
    }

    public void useShaderProgram(ShaderProgram pProg) {
        if(pProg==selectedShader) return;

        LOGGER.debugPF("USING SHADER "+pProg);
        GraphicsWrapper.useProgram(pProg.id);

        if(GraphicsWrapper.getCurrentShaderId() == pProg.id)
            selectedShader = pProg;
    }

    public ShaderProgram getSelectedShader(){
        return selectedShader;
    }

    public void noProgram(){
        selectedShader = null;
        GraphicsWrapper.noProgram();
    }

    public void setUni(String uniKey, Object pValue) {
        if(selectedShader == null) {
            LOGGER.err("trying to assign value to uni while no shader is selected - returning");
            return;
        }
        Uni<?> uni = selectedShader.getUni(uniKey);
        if(uni == null){
            LOGGER.err("selected shader "+selectedShader+" has no uni with key "+uniKey);
            return;
        }

        uni.set(pValue);
    }
}
