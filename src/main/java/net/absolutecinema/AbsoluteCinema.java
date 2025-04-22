package net.absolutecinema;

import net.absolutecinema.rendering.GraphicsWrapper;
import net.absolutecinema.rendering.Window;
import net.absolutecinema.rendering.shader.Shader;
import net.absolutecinema.rendering.shader.ShaderProgram;
import net.absolutecinema.rendering.shader.ShaderType;
import org.lwjgl.glfw.GLFWErrorCallback;

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
            ShaderProgram sp = new ShaderProgram();
            Path shaderpath = config.assetDirectory().toPath().resolve("shader");
            try{
                Shader vsh = new Shader(ShaderType.VERTEX, Files.readString(shaderpath.resolve("shader.vert")));
                Shader fsh = new Shader(ShaderType.FRAGMENT, Files.readString(shaderpath.resolve("shader.frag")));
                sp.attach(vsh);
                sp.attach(fsh);
            }catch (Exception e){
                e.printStackTrace();
            }
            sp.link();
            sp.use();
        }
    }

    private long frameCount = 0;
    private void frame(){
        frameCount++;
        glfwPollEvents();
        if(window.shouldClose()){
            running = false;
        }

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        if(frameCount%100_000==0) System.out.println(frameCount);
    }
}
