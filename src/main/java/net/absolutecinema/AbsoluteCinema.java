package net.absolutecinema;

import net.absolutecinema.rendering.Camera;
import net.absolutecinema.rendering.GraphicsWrapper;
import net.absolutecinema.rendering.Window;
import net.absolutecinema.rendering.meshes.BufferWrapper;
import net.absolutecinema.rendering.shader.Shader;
import net.absolutecinema.rendering.shader.ShaderProgram;
import net.absolutecinema.rendering.shader.ShaderType;
import net.absolutecinema.rendering.shader.Uni;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

import static net.absolutecinema.rendering.GraphicsWrapper.initGLFW;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class AbsoluteCinema {
    public static AbsoluteCinema instance = null;
    public static Logger LOGGER = null;

    private boolean running;
    private Window window;

    private final GameConfig config;

    Uni<Matrix4f> view;
    Uni<Matrix4f> projection;
    Uni<Matrix4f> model;
    Uni<Vector3f> cameraPos;
    BufferWrapper objModel;

    Camera cam;
    double lastX = Double.MAX_VALUE;
    double lastY = Double.MAX_VALUE;

    public AbsoluteCinema(final GameConfig pConfig){
        instance = this;
        LOGGER = new Logger();

        this.running = false;
        this.window = null;

        config = pConfig;
    }

    public void run(){
        this.running = true;
        final long startTime = System.currentTimeMillis();

        init();

        int frames = 0;
        double timer = glfwGetTime();

        while(this.running){
            frame();

            double currentTime = glfwGetTime();
            frames++;
            if (currentTime - timer >= 1.0) {
                window.setTitle("ABS CIN - "+frames+" fps");
                frames = 0;
                timer += 1.0;
            }
        }
    }

    private void init(){
        GLFWErrorCallback.createPrint(System.err).set();//todo to LOGGER
        initGLFW();

        glfwDefaultWindowHints();//todo hide that shi
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        this.window = new Window();
        this.window.select();
        GraphicsWrapper.createCapabilities();
        this.window.show();
        //this.window.enableVsync();

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
            glfwSetCursorPosCallback(window.id, (window, xpos, ypos) -> {
                if (lastX == Double.MAX_VALUE || lastY == Double.MAX_VALUE) {
                    lastX = xpos;
                    lastY = ypos;
                }
                float deltaX = (float) (xpos - lastX);
                float deltaY = (float) (ypos - lastY);

                cam.yaw(-deltaX);
                cam.pitch(deltaY);

                lastX = xpos;
                lastY = ypos;
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

        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);
        glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

        {
            ShaderProgram shaderProgram = new ShaderProgram();
            Path shaderpath = config.assetDirectory().toPath().resolve("shader");
            try{
                Shader vsh = new Shader(ShaderType.VERTEX, Files.readString(shaderpath.resolve("shader.vert")));
                Shader fsh = new Shader(ShaderType.FRAGMENT, Files.readString(shaderpath.resolve("shader.frag")));
                shaderProgram.attach(vsh);
                shaderProgram.attach(fsh);
            }catch (Exception e){
                e.printStackTrace();
            }
            shaderProgram.link();
            shaderProgram.use();

            view = new Uni<>(shaderProgram, "view");
            projection = new Uni<>(shaderProgram, "projection");
            model = new Uni<>(shaderProgram, "model");
            cameraPos = new Uni<>(shaderProgram, "cameraPos");

            cam = new Camera();
            //cam.setX(10);

            view.set(cam.getViewMatrix());
            projection.set(cam.getProjectionMatrix((float) Math.toRadians(90.0f), ((float) 800 / (float) 600), 0.1f, 100.0f));
            model.set(new Matrix4f().identity());
            cameraPos.set(cam.getPos());
        }

        objModel = new BufferWrapper(6);
        float[] cubeVts = Util.trisFromObj(config.assetDirectory().toPath().resolve("models/mountains.obj"));
        FloatBuffer vertexBuffer = MemoryUtil.memAllocFloat(cubeVts.length);
        vertexBuffer.put(cubeVts).flip();
        objModel.uploadToVBO(vertexBuffer);
        objModel.addVToV(3,false);
        objModel.addVToV(3,true);
    }

    private long frameCount = 0;
    private void frame(){
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        frameCount++;
        glfwPollEvents();
        if(window.shouldClose()){
            running = false;
        }

        view.set(cam.getViewMatrix());
        cameraPos.set(cam.getPos());
        objModel.bindVAO();
        objModel.draw();


        window.swapBuffers();
        if(frameCount%100_000==0) System.out.println(frameCount);
    }
}
