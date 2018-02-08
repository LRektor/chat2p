package org.chat2p.api.logger;

public class Logger {

    /**
     * The Log Level changes how much debug log is shown.
     * 0 = Console
     * 1 = Base Log
     * 2 = Full Log
     * 3 = Debug Log
     */
    public static int globalLogLevel = 0;

    public static void log(String message, int logLevel){
        log("Info", message, logLevel);
    }

    public static void log(String tag, String message, int logLevel){
        if(logLevel <= globalLogLevel){
            System.out.println("[" + tag + "]: " + message);
        }
    }

}
