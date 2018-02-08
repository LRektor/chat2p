package org.chat2p.backend;

import org.chat2p.api.logger.Logger;
import org.chat2p.backend.net.Server;

public class BackendStarter {

    public static void main(String[] args){
        if((args.length == 1 || args.length == 2) && args[0].contains("-log:")){
            Logger.globalLogLevel = Integer.parseInt(args[0].split(":")[1]);
            Logger.log("Setting logger to Log level " + Logger.globalLogLevel, 1);
        }else if((args.length == 1 || args.length == 2) && args[1].contains("-log:")){
            Logger.globalLogLevel = Integer.parseInt(args[1].split(":")[1]);
            Logger.log("Setting logger to Log level " + Logger.globalLogLevel, 1);
        }
        int port;
        if((args.length == 1 || args.length == 2) && args[0].contains("-port:")){
            port = Integer.parseInt(args[0].split(":")[1]);
            Logger.log("Setting custom port to " + port, 1);
        }else if((args.length == 1 || args.length == 2) && args[1].contains("-port:")){
            port = Integer.parseInt(args[1].split(":")[1]);
            Logger.log("Setting custom port to " + port, 1);
        }else{
            port = 25678;
        }
        new Server(port);
    }

}
