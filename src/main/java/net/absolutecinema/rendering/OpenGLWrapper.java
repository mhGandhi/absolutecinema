package net.absolutecinema.rendering;

import net.absolutecinema.rendering.shader.ShaderCompilationException;
import net.absolutecinema.rendering.shader.ShaderType;
import org.lwjgl.opengl.GL33;


import static net.absolutecinema.AbsoluteCinema.LOGGER;

public class OpenGLWrapper {
    public static int createShader(ShaderType pShaderType){
        int type = shaderTypeToInt(pShaderType);
        return GL33.glCreateShader(type);
    }

    public static void uploadSourceToShader(int pShaderId, String pSource){
        GL33.glShaderSource(pShaderId, pSource);
    }

    public static void compileShader(int pShaderId) throws ShaderCompilationException{
        GL33.glCompileShader(pShaderId);
        if (GL33.glGetShaderi(pShaderId, GL33.GL_COMPILE_STATUS) == GL33.GL_FALSE) {
            String log = GL33.glGetShaderInfoLog(pShaderId);
            LOGGER.err("Shader Compilation Failed:\n" + log);
            throw new ShaderCompilationException("Shader failed to compile.");
        }
    }

    public static int shaderTypeToInt(ShaderType pType){
        switch (pType){
            case VERTEX -> {
                return GL33.GL_VERTEX_SHADER;
            }
            case FRAGMENT -> {
                return GL33.GL_FRAGMENT_SHADER;
            }
            default -> {
                return -1;//todo exceptions
            }
        }
    }
}
