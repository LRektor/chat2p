package org.chat2p.backend.net;

import java.io.IOException;
import java.net.Socket;

public class ConnectedClient extends Thread {

    private Socket connectionSocket;

    private Server serverInstance;

    public ConnectedClient(Server server, Socket socket){
        this.connectionSocket = socket;
        this.serverInstance = server;
    }

    public void close() throws IOException {
        connectionSocket.close();
    }

}
