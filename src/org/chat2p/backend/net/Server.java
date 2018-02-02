package org.chat2p.backend.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server {

    private ServerSocket socket;

    private HashMap<String, ConnectedClient> clients = new HashMap<>();

    public boolean shutdown = false;

    public Server(int port){
        try {
            socket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (!shutdown) {
            try {
                System.out.println("Searching for clients");
                Socket clientSocket = socket.accept();
                if (shutdown) break;
                System.out.println("Client found. Client address is " + clientSocket.getInetAddress().toString());
                ConnectedClient newClient = new ConnectedClient(this, clientSocket);
                newClient.start();
                clients.put("name", newClient);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //Shuts down the clients
        for (ConnectedClient client : clients.values()) {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            client.stop();
        }

        try {
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
