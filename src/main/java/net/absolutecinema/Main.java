package net.absolutecinema;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        GameConfig config;
        config = new GameConfig(new File("C:\\Users\\rando\\IdeaProjects\\absolutecinema\\workingDirectory"), new File("C:\\Users\\rando\\IdeaProjects\\absolutecinema\\assets"));

        AbsoluteCinema absoluteCinema = new AbsoluteCinema(config);

        absoluteCinema.run();
    }
    /*todo
    LOGGER
    MESH (DIFFERENT KINDS)
    MORE SOLID VToV fields
    (TEXTURE)
    Auto decide Mesh type
    Model Class (interface?) for static ones, groups etc
    Gridlines
    */
}
