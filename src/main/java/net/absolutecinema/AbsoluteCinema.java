package net.absolutecinema;

public class AbsoluteCinema {
    public final AbsoluteCinema instance;
    private boolean running;

    public AbsoluteCinema(final GameConfig pConfig){
        this.instance = this;
        this.running = false;
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
