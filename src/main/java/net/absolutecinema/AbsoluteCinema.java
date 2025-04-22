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
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

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
    BufferWrapper cube;

    Camera cam;

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
        while(this.running){
            frame();
        }
    }

    private void init(){
        GLFWErrorCallback.createPrint(System.err).set();//todo to LOGGER
        initGLFW();

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        this.window = new Window();
        this.window.select();
        GraphicsWrapper.createCapabilities();
        this.window.show();
        this.window.enableVsync();

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

            cam = new Camera();
            //cam.setX(10);

            view.set(cam.getViewMatrix());
            projection.set(cam.getProjectionMatrix((float) Math.toRadians(90.0f), ((float) 800 / (float) 600), 0.1f, 100.0f));
            model.set(new Matrix4f().identity());
        }

        cube = new BufferWrapper(6);
        float[] cubeVts = Util.trisFromObj(config.assetDirectory().toPath().resolve("cube.obj"));
        FloatBuffer vertexBuffer = MemoryUtil.memAllocFloat(cubeVts.length);
        vertexBuffer.put(cubeVts).flip();
        cube.uploadToVBO(vertexBuffer);
        cube.addVToV(3,false,0);
        cube.addVToV(3,true,3);
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
        cube.bindVAO();
        cube.draw();


        window.swapBuffers();
        if(frameCount%100_000==0) System.out.println(frameCount);
    }
}
