package org.chat2p.backend.net;

import org.chat2p.api.MessageType;
import org.chat2p.api.NetMessage;
import org.chat2p.api.P2PConnectionRequest;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Server {

    public ServerSocket socket;

    public HashMap<String, ConnectedClient> clients = new HashMap<>();
    public ArrayList<ConnectedClient> pendingConfirmation = new ArrayList<>();

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
                pendingConfirmation.add(newClient);
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

    public void requestP2P(ConnectedClient requester, ConnectedClient requested){
        requested.sendMessage(new NetMessage("Server:" + this.socket.getInetAddress().toString(), requested.username, new P2PConnectionRequest(requester.username, requested.username), MessageType.RequestP2P));
    }

    public void acceptP2P(P2PConnectionRequest req){

    }

    public void  denyP2P(P2PConnectionRequest req){

    }

}
