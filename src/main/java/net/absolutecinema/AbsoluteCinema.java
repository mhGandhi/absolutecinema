package net.absolutecinema;

import net.absolutecinema.models.Model;
import net.absolutecinema.rendering.Camera;
import net.absolutecinema.rendering.GraphicsWrapper;
import net.absolutecinema.rendering.ShaderManager;
import net.absolutecinema.rendering.Window;
import net.absolutecinema.rendering.meshes.Mesh;
import net.absolutecinema.rendering.meshes.VertexNormalMesh;
import net.absolutecinema.rendering.shader.Shader;
import net.absolutecinema.rendering.shader.ShaderProgram;
import net.absolutecinema.rendering.shader.ShaderType;
import net.absolutecinema.rendering.shader.Uni;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWKeyCallback;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class AbsoluteCinema {
    public static AbsoluteCinema instance = null;
    public static Logger LOGGER = null;
    public static GameConfig config;

    private boolean running;
    private Window window;
    private long frameCount;

    public final Runtime runtime;
    private final ShaderManager shaderManager;

//////////////////////////////////////////////////
    Uni<Matrix4f> viewMat;
    Uni<Matrix4f> projectionMat;
    Uni<Matrix4f> modelMat;
    Uni<Vector3f> cameraPosVec;
    List<Model> objModels;

    Camera cam;
    double lastX = Double.MAX_VALUE;
    double lastY = Double.MAX_VALUE;
    Vector3f manPos = new Vector3f(0,0,0);
    float manYaw = 0f;
//////////////////////////////////////////////////

    public AbsoluteCinema(final GameConfig pConfig){
        instance = this;
        LOGGER = new Logger();
        config = pConfig;

        this.running = false;
        this.window = null;
        frameCount = 0;

        runtime = Runtime.getRuntime();
        shaderManager = new ShaderManager();
    }

    public void run(){
        this.running = true;

        init();
        loop();
        terminate();
    }

    private void init(){
        GraphicsWrapper.setErrorPrintStream(LOGGER.getErrorStream());
        GraphicsWrapper.init();

        //setUp window
        {
            GraphicsWrapper.setWindowHints();
            this.window = new Window();
            this.window.select();
            GraphicsWrapper.createCapabilities();
            this.window.show();
            //this.window.enableVsync();
        }

        //CALLBACKS todo remove
        {
            GLFWKeyCallback keyCallback = glfwSetKeyCallback(window.id, (window, key, scancode, action, mods) -> {
                if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                    glfwSetWindowShouldClose(window, true);
                if (action == GLFW_PRESS || action == GLFW_REPEAT) {
                    if (key == GLFW_KEY_W) cam.moveForward(0.1f);
                    if (key == GLFW_KEY_S) cam.moveForward(-0.1f);
                    if (key == GLFW_KEY_A) cam.moveRight(-0.1f);
                    if (key == GLFW_KEY_D) cam.moveRight(0.1f);
                    if (key == GLFW_KEY_SPACE) cam.moveUp(0.1f);
                    if (key == GLFW_KEY_LEFT_CONTROL) cam.moveUp(-0.1f);
                }
            });


            //glfwSetInputMode(testWindow, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
            //glfwSetInputMode(testWindow, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
            glfwSetInputMode(window.id, GLFW_RAW_MOUSE_MOTION, GLFW_TRUE);
            glfwSetCursorPosCallback(window.id, (window, xPos, yPos) -> {
                if (lastX == Double.MAX_VALUE || lastY == Double.MAX_VALUE) {
                    lastX = xPos;
                    lastY = yPos;
                }
                float deltaX = (float) (xPos - lastX);
                float deltaY = (float) (yPos - lastY);

                cam.yaw(-deltaX);
                cam.pitch(deltaY);

                lastX = xPos;
                lastY = yPos;
            });

            glfwSetWindowFocusCallback(window.id, (window, focused) -> {
                if (!focused) {
                    glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
                    lastX = Double.MAX_VALUE;
                    lastY = Double.MAX_VALUE;
                }
            });

            glfwSetMouseButtonCallback(window.id, (window, button, action, mods) -> {
                if (button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_PRESS) {
                    glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
                }
            });
            glfwSetFramebufferSizeCallback(window.id, (window, width, height) -> {
                if (width == 0 || height == 0) return;
                glViewport(0, 0, width, height);
            });
        }

        glEnable(GL_DEPTH_TEST);//todo
        glDepthFunc(GL_LESS);//todo
        glClearColor(1.0f, 1.0f, 1.0f, 1.0f);//todo

        //setUp shader
        {
            shaderManager.loadShader("norm");
            ShaderProgram shaderProgram = shaderManager.getShaderProgram("norm");

            shaderProgram.use();

            viewMat = new Uni<>(shaderProgram, Constants.VIEW_MAT_UNI, Constants.IDENTITY_4F);
            projectionMat = new Uni<>(shaderProgram, Constants.PROJECTION_MAT_UNI, Constants.IDENTITY_4F);
            modelMat = new Uni<>(shaderProgram, Constants.MODEL_MAT_UNI, Constants.IDENTITY_4F);
            cameraPosVec = new Uni<>(shaderProgram, Constants.CAMERA_POS_UNI, Constants.ZERO_VEC3F);
        }

        //setUp cam
        {
            cam = new Camera();

            viewMat.set(cam.getViewMatrix());
            projectionMat.set(cam.getProjectionMatrix((float) Math.toRadians(90.0f), ((float) 800 / (float) 600), 0.1f, 100.0f));
            modelMat.set(new Matrix4f().identity());
            cameraPosVec.set(cam.getPos());
        }

        //setUp objects
        {
            objModels = new LinkedList<>();
            String[] meshPaths = {"mountains","man","cube","axis","ship","teapotN"};
            for(String filename : meshPaths){
                float[] vertices = Util.trisFromObj(config.assetDirectory().toPath().resolve("models").resolve(filename+".obj"));
                Mesh m = new VertexNormalMesh();
                m.assignVertices(vertices);
                objModels.add(new Model(m, shaderManager.getShaderProgram("norm"), filename.equals("man")?objModels.get(0):null, modelMat));
            }
            objModels.get(0).setPos(new Vector3f(-10,-10,-10));
        }

    }

    private void loop(){
        int frames = 0;
        double timer = glfwGetTime();
        frameCount++;

        while(this.running){
            frame();

            double currentTime = glfwGetTime();
            frames++;
            if (currentTime - timer >= 1.0) {
                long totalMemory = runtime.totalMemory();
                long freeMemory = runtime.freeMemory();
                long usedMemory = totalMemory - freeMemory;

                window.setTitle("ABSOLUTE CINEMA - "+frames+" fps - "+usedMemory/(1024*1024)+"/"+totalMemory/(1024*1024)+" MB");
                frames = 0;
                timer += 1.0;
            }
        }
    }

    private void terminate(){
        GraphicsWrapper.terminate();
        Buffers.freeAll();
    }

    private void frame(){
        GraphicsWrapper.clearWin();
        frameCount++;
        GraphicsWrapper.pollEvents();
        if(window.shouldClose()){
            running = false;
        }

        viewMat.set(cam.getViewMatrix());
        cameraPosVec.set(cam.getPos());

        manYaw += 0.00001f;
        manPos.add(0,0,0.0002f);
        objModels.get(1).setPos(manPos);
        objModels.get(0).setRotation(0, manYaw, 0);

        for (Model m : objModels) {
            m.draw();
        }

        window.swapBuffers();
    }
}
