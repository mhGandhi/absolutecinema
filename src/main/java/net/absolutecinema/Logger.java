package net.absolutecinema;

import java.io.OutputStream;
import java.io.PrintStream;

public class Logger {
    private final PrintStream errorStream;

    public Logger() {
        this.errorStream = new PrintStream(new OutputStream() {
            private final StringBuilder buffer = new StringBuilder();

            @Override
            public void write(int b) {
                if (b == '\n') {
                    err(buffer.toString());
                    buffer.setLength(0);
                } else {
                    buffer.append((char) b);
                }
            }
        }, true); // true = autoFlush
    }

    public PrintStream getErrorStream(){
        return this.errorStream;
    }

    public void debug(String pDebug){
        System.out.println("[DBG] "+pDebug);
    }

    public void info(String pInfo){
        System.out.println("[INF] "+pInfo);
    }

    public void warn(String pWarn){
        System.out.println("[WRN] "+pWarn);
    }

    public void err(String pErr){
        System.err.println("[ERR] "+pErr);
    }

    public void fatal(String pErr){
        System.err.println("[FAT] "+pErr);
    }
}
