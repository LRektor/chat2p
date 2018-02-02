package org.chat2p.backend;

import org.chat2p.backend.net.Server;

public class BackendStarter {

    public static void main(String[] args){
        int port;
        if(args.length >= 1 && args[0].contains("-port:")){
            port = Integer.parseInt(args[0].split(":")[1]);
            System.out.println("Setting custom port to " + port);
        }else{
            port = 25678;
        }
        Server backendServer = new Server(port);
    }

}
