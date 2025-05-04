package net.absolutecinema;

import java.io.OutputStream;
import java.io.PrintStream;

public class Logger {//todo
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
        System.out.println(withPrefixOnEachLine("[DBG] ", pDebug));
    }

    public void info(String pInfo){
        System.out.println(withPrefixOnEachLine("[INF] ", pInfo));
    }

    public void warn(String pWarn){
        System.out.println(withPrefixOnEachLine("[WRN] ", pWarn));
    }

    public void err(String pErr){
        System.err.println(withPrefixOnEachLine("[ERR] ", pErr));
    }

    public void fatal(String pFatal){
        System.err.println(withPrefixOnEachLine("[FAT] ", pFatal));
    }

    public String withPrefixOnEachLine(String pPrefix, String pContent) {
        String ret = pContent.lines()
                .map(line -> pPrefix + line)
                .reduce((a, b) -> a + "\n" + b)
                .orElse("");
        return ret+"\n";
    }

}