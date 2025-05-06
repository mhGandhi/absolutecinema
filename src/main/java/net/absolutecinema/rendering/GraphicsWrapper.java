package net.absolutecinema.rendering;

import net.absolutecinema.rendering.shader.FieldType;
import net.absolutecinema.rendering.shader.programs.ProgramLinkingException;
import net.absolutecinema.rendering.shader.ShaderCompilationException;
import net.absolutecinema.rendering.shader.ShaderType;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL33;


import java.io.PrintStream;
import java.nio.FloatBuffer;

import static net.absolutecinema.AbsoluteCinema.LOGGER;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GraphicsWrapper {
    private static int boundVBO = -1;
    private static int boundVAO = -1;
    private static long contextWindow = -1;

    public static int getBoundVAO() {
        return boundVAO;
    }
    public static int getBoundVBO(){
        return boundVBO;
    }
    public static long getContextWindow(){
        return contextWindow;
    }

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
    public static void deleteProgram(int pProgramId){
        GL33.glDeleteProgram(pProgramId);
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
        if(pValue instanceof Texture tex){
            glUniform1i(pLocation, tex.id);
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
        contextWindow = pId;
        glfwMakeContextCurrent(pId);
    }
    public static void createCapabilities(){
        GL.createCapabilities();
    }
    public static void swapInterval(int pIv){
        glfwSwapInterval(pIv);
    }
    public static void showWindow(long pWindowId){
        glfwShowWindow(pWindowId);
    }
    public static void hideWindow(long pWindowId){
        glfwHideWindow(pWindowId);
    }
    public static void swapBuffers(long pWindowId){
        glfwSwapBuffers(pWindowId);
    }
    public static void setWindowTitle(long pWindowId, String pTitle){
        glfwSetWindowTitle(pWindowId, pTitle);
    }
    public static void setWindowHints(){
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);//todo maybe not always?
    }
    public static void pollEvents(){
        glfwPollEvents();
    }
    public static boolean windowShouldClose(long pWindowId){
        return glfwWindowShouldClose(pWindowId);
    }
    public static void clearWin(){
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }
//////////////////////////////////////////////////////////////////////////////////////////////////VAO
    public static int genVAO(){
        return GL33.glGenVertexArrays();
    }
    public static void bindVAO(int pVaoID){
        GL33.glBindVertexArray(pVaoID);
        boundVAO = pVaoID;
    }
    public static void unbindVAO(){
        GL33.glBindVertexArray(0);
        boundVAO = 0;
    }
//////////////////////////////////////////////////////////////////////////////////////////////////VBO
    public static int genVBO(){
        return GL33.glGenBuffers();
    }
    public static void bindVBO(int pVboID){
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, pVboID);
        boundVBO = pVboID;
    }
    public static void unbindVBO(){
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, 0);
        boundVBO = 0;
    }
    public static void uploadToVBO(FloatBuffer pVertexBuffer){
        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, pVertexBuffer, GL33.GL_STATIC_DRAW);//todo draw param
    }

    //todo ka wie man das nennt
    public static void assignVToV(int pIndex, int pSize, boolean pNormalize, int pStride, int pOffset, FieldType pType){
        GL33.glVertexAttribPointer(pIndex, pSize, fieldTypeToInt(pType), pNormalize, pStride * pType.bytes, (long) pOffset * pType.bytes);
    }
    public static void enableVToV(int pIndex){
        GL33.glEnableVertexAttribArray(pIndex);
    }
    public static void disableVToV(int pIndex){
        GL33.glDisableVertexAttribArray(pIndex);
    }


    public static void drawTriangles(int pCount){
        GL33.glDrawArrays(GL33.GL_TRIANGLES, 0, pCount);
    }

    public static void init(){
        initGLFW();
    }
    public static void terminate(){
        glfwTerminate();
    }
    public static void setErrorPrintStream(PrintStream pPS){
        GLFWErrorCallback.createPrint(pPS).set();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////TEXTURE
    //todo


    //////////////////////////////////////////////////////////////////////////////////////////////////TOINT
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
    public static int fieldTypeToInt(FieldType pType){
        switch (pType){
            case FLOAT -> {
                return GL_FLOAT;
            }
            case INTEGER -> {
                return GL_INT;
            }
            case BYTE -> {
                return GL_BYTE;
            }
            default -> {
                return -1;
            }
        }
    }
}
