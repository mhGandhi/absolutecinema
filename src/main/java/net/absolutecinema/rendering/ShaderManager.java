package net.absolutecinema.rendering;

import net.absolutecinema.AbsoluteCinema;
import net.absolutecinema.rendering.shader.Shader;
import net.absolutecinema.rendering.shader.ShaderProgram;
import net.absolutecinema.rendering.shader.ShaderType;
import org.lwjgl.system.MemoryUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.absolutecinema.AbsoluteCinema.LOGGER;
import static net.absolutecinema.AbsoluteCinema.config;

public class ShaderManager {
    private final Map<String, ShaderProgram> shaders;

    public ShaderManager(){
        shaders = new HashMap<>();
    }

    public void loadShader(String pName){
        try {
            shaders.put(pName, newShaderFromFileName(pName));
        }catch (Exception e){
            e.printStackTrace(LOGGER.getErrorStream());
        }
    }

    private ShaderProgram newShaderFromFileName(String pFileName) throws IOException {
        Path shaderpath = config.assetDirectory().toPath().resolve(config.shaderFolderName());

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

        ShaderProgram ret = new ShaderProgram();
        for (Path sh : indShaders){
            ret.attach(
                    new Shader(
                            ShaderType.byPath(sh),
                            Files.readString(sh)
                    )
            );
        }

        ret.linkAndClearShaders();

        return ret;
    }

    public ShaderProgram getShaderProgram(String pKey){
        return this.shaders.get(pKey);
    }
}
