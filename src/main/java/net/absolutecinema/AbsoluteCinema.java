package net.absolutecinema;

public class AbsoluteCinema {
    public static AbsoluteCinema instance = null;
    public static Logger LOGGER = null;
    private boolean running;

    public AbsoluteCinema(final GameConfig pConfig){
        instance = this;
        this.running = false;
        LOGGER = new Logger();
    }

    public void run(){
        this.running = true;
        final long startTime = System.currentTimeMillis();

        while(this.running){
            frame();
        }
    }

    private void frame(){
        System.out.println("FRAME");
    }
}
