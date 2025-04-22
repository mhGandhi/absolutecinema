package net.absolutecinema;

public class Logger {
    public void debug(String pDebug){
        System.out.println(pDebug);
    }

    public void info(String pInfo){
        System.out.println(pInfo);
    }

    public void warn(String pWarn){
        System.out.println(pWarn);
    }

    public void err(String pErr){
        System.err.println(pErr);
    }

    public void fatal(String pErr){
        System.err.println(pErr);
    }
}
