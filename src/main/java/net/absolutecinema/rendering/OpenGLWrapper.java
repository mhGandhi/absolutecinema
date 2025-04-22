package net.absolutecinema.rendering;

import net.absolutecinema.rendering.shader.ProgramLinkingException;
import net.absolutecinema.rendering.shader.ShaderCompilationException;
import net.absolutecinema.rendering.shader.ShaderType;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL33;


import java.nio.FloatBuffer;

import static net.absolutecinema.AbsoluteCinema.LOGGER;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class OpenGLWrapper {
//////////////////////////////////////////////////////////////////////////////////////////////////SHADER
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
    public static void deleteShader(int pShaderId){
        GL33.glDeleteShader(pShaderId);
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
//////////////////////////////////////////////////////////////////////////////////////////////////PROGRAM
    public static int createProgram(){
        return GL33.glCreateProgram();
    }
    public static void attachShader(int pProgramId, int pShaderId){
        GL33.glAttachShader(pProgramId, pShaderId);
    }
    public static void detachShader(int pProgramId, int pShaderId){
        GL33.glDetachShader(pProgramId, pShaderId);
    }
    public static void linkProgram(int pProgramId) throws ProgramLinkingException {
        GL33.glLinkProgram(pProgramId);
        if (GL33.glGetProgrami(pProgramId, GL33.GL_LINK_STATUS) == GL33.GL_FALSE) {
            String log = GL33.glGetProgramInfoLog(pProgramId);
            LOGGER.err("Shader Program Linking Failed:\n" + log);
            throw new ProgramLinkingException("Shader program failed to link.");
        }
    }
    public static void useProgram(int pProgramId){
        GL33.glUseProgram(pProgramId);
    }

    public static int getUniformLocation(int pProgramId, CharSequence pName){
        return GL33.glGetUniformLocation(pProgramId, pName);
    }
    //doesn't assure type safety
    public static void putUniformValue(int pLocation, Object pValue){
        if(pValue instanceof Matrix4f m4f){
            GL33.glUniformMatrix4fv(pLocation, false, m4f.get(new float[16]));
            return;
        }
        if(pValue instanceof Float f){
            GL33.glUniform1f(pLocation, f);
            return;
        }
        if(pValue instanceof Vector3f v3f){
            GL33.glUniform3f(pLocation, v3f.x, v3f.y, v3f.z);
            return;
        }
    }
//////////////////////////////////////////////////////////////////////////////////////////////////WINDOW
    public static boolean initGLFW(){
        return glfwInit();
    }
    public static long createWindow(int pWidth, int pHeight, String pTitle, long pMonitor, long pShare) {
        long id = glfwCreateWindow(pWidth, pHeight, pTitle, pMonitor, pShare);
        if (id == NULL)
            throw new RuntimeException("Failed to create GLFW window");
        return id;
    }
    public static void makeWinContextCurrent(long pId){
        glfwMakeContextCurrent(pId);
    }
    public static void createCapabilities(){
        GL.createCapabilities();
    }
    //todo make vsync directly
    public static void swapInterval(int pIv){
        glfwSwapInterval(pIv);
    }
    public static void showWindow(long pWindowId){
        glfwShowWindow(pWindowId);
    }
    public static void hideWindow(long pWindowId){
        glfwHideWindow(pWindowId);
    }
}
