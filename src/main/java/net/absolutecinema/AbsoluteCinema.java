package net.absolutecinema;

import net.absolutecinema.models.Model;
import net.absolutecinema.rendering.*;
import net.absolutecinema.rendering.shader.programs.DefaultObjShader;
import net.absolutecinema.rendering.shader.Uni;
import net.absolutecinema.rendering.shader.programs.ModelShader;
import net.absolutecinema.rendering.shader.programs.TexturedObjShader;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWKeyCallback;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class AbsoluteCinema {
    public static AbsoluteCinema instance = null;
    public static Logger LOGGER = null;
    public static GameConfig config;
    public static Options options;
    public static ShaderManager shaderManager;

    private boolean running;
    private Window window;
    private long frameCount;

    public final Runtime runtime;

//////////////////////////////////////////////////
    Uni<Matrix4f> viewMat;
    Uni<Matrix4f> projectionMat;
    Uni<Matrix4f> modelMat;
    Uni<Vector3f> cameraPosVec;
    List<Model> objModels;
    Texture testTexture;

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
        options = new Options();//todo load from file
        shaderManager = new ShaderManager();

        this.running = false;
        this.window = null;
        frameCount = 0;

        runtime = Runtime.getRuntime();
    }

    public void run(){
        this.running = true;

        init();
        loop();
        terminate();
    }

    private void init(){
        {//setUp options
            options.setFpsCap(Integer.MAX_VALUE);
            options.setFov(90f);
        }

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

        //setUp shader todo smart selection with capabilities enum or sth - getting from manager
        {
            TexturedObjShader shaderProgram =
                    (TexturedObjShader) shaderManager.loadShader(
                            Constants.TESTING_SHADER_NAME, new TexturedObjShader()
                    );//shader loading only once
            if(shaderProgram == null)throw new NullPointerException("SOMETHING WENT WRONG LOADING SHADER");
            //todo save unis somewhere else so view etc can be applied on shaders of all unis
            shaderProgram.use();

            viewMat = shaderProgram.viewMat;
            projectionMat = shaderProgram.projMat;
            modelMat = shaderProgram.modelMat;
            cameraPosVec = shaderProgram.camPos;
        }

        //setUp cam
        {
            cam = new Camera();

            viewMat.set(cam.getViewMatrix());
            projectionMat.set(cam.getProjectionMatrix((float) Math.toRadians(options.getFov()), ((float) 800 / (float) 600), 0.0001f, 1000.0f));
            cameraPosVec.set(cam.getPos());
        }

        testTexture = new Texture(config.assetDirectory().toPath().resolve("textures/monkey.png"));
        //setUp objects
        {
            objModels = new LinkedList<>();
            String[] meshPaths = {"mountains","man","cube","cube"/*,"axis","ship","teapotN"*/};
            ModelShader shaderProgram = (ModelShader) shaderManager.getShaderProgram(Constants.TESTING_SHADER_NAME);
            for(String filename : meshPaths){

                Path objPath = config.assetDirectory().toPath().resolve("models").resolve(filename+".obj");
                Model add = Model.fromFile(objPath, shaderProgram);

                if(filename.equals("man")){
                    add.setParent(objModels.get(0));
                }
                objModels.add(add);
            }
            //objModels.get(0).setPos(new Vector3f(-10,-10,-10));
        }
    }

    private void loop() {
        int frames = 0;
        double timer = glfwGetTime();
        double lastTime = glfwGetTime();
        double deltaTime = 0;

        while (this.running) {
            double frameStartTime = glfwGetTime();
            deltaTime = frameStartTime - lastTime;
            lastTime = frameStartTime;

            frame(deltaTime);

            frames++;
            if (frameStartTime - timer >= 1.0) {
                long totalMemory = runtime.totalMemory();
                long freeMemory = runtime.freeMemory();
                long usedMemory = totalMemory - freeMemory;
                double frameTimeMs = deltaTime * 1000.0;

                window.setTitle(String.format(
                        "ABSOLUTE CINEMA - %d fps - %.2f ms - %d/%d MB",
                        frames,
                        frameTimeMs,
                        usedMemory / (1024 * 1024),
                        totalMemory / (1024 * 1024)
                ));

                frames = 0;
                timer += 1.0;
            }


            if(options.getTargetFrameTime()>0){//todo central option monitoring with caching
                if(window.vsyncEnabled())window.disableVsync();

                // FPS limiter
                double endTime = glfwGetTime();
                double frameDuration = endTime - frameStartTime;
                double sleepTime = options.getTargetFrameTime() - frameDuration;

                if (sleepTime > 0) {
                    try {
                        Thread.sleep((long)(sleepTime * 1000)); // convert to ms
                    } catch (InterruptedException ignored) {}
                }
            }else{
                if(!window.vsyncEnabled())window.enableVsync();
            }
        }
    }


    private void terminate(){
        GraphicsWrapper.terminate();
        Buffers.freeAll();
    }

    private void frame(double pDeltaTime){
        GraphicsWrapper.clearWin();
        frameCount++;
        GraphicsWrapper.pollEvents();
        if(window.shouldClose()){
            running = false;
        }

        viewMat.set(cam.getViewMatrix());
        cameraPosVec.set(cam.getPos());

        manYaw += (float) (0.3f*pDeltaTime);
        manPos.add(0, 0, (float) (1f*pDeltaTime));
        objModels.get(1).setPos(manPos);
        objModels.get(0).setRotation(0, manYaw, 0);

        for (Model m : objModels) {
            m.draw();
        }

        window.swapBuffers();
    }
}
