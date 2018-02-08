package org.chat2p.backend.net;

import org.chat2p.api.MessageType;
import org.chat2p.api.NetMessage;
import org.chat2p.api.P2PConnectionRequest;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

@SuppressWarnings("deprecation")
public class Server {

    ServerSocket socket;

    HashMap<String, ConnectedClient> clients = new HashMap<>();
    ArrayList<ConnectedClient> pendingConfirmation = new ArrayList<>();

    private boolean shutdown = false;

    public Server(int port){
        try {
            socket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        new CommandHandler().start();

        while (!shutdown) {
            try {
                System.out.println("Searching for clients");
                Socket clientSocket = socket.accept();
                if (shutdown) break;
                System.out.println("Client found. Client address is " + clientSocket.getInetAddress().toString());
                new ConnectedClient(this, clientSocket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void requestP2P(ConnectedClient requester, ConnectedClient requested){
        requested.sendMessage(new NetMessage("Server:" + this.socket.getInetAddress().toString(), requested.username, new P2PConnectionRequest(requester.username, requested.username), MessageType.RequestP2P));
    }

    void acceptP2P(P2PConnectionRequest req){
        clients.get(req.requester).sendMessage(new NetMessage("Server:" + this.socket.getInetAddress().toString(), req.requester, clients.get(req.requested).connectionSocket, MessageType.P2P));
        clients.get(req.requested).sendMessage(new NetMessage("Server:" + this.socket.getInetAddress().toString(), req.requested, clients.get(req.requester).connectionSocket, MessageType.P2P));
    }

    void denyP2P(P2PConnectionRequest req){
        clients.get(req.requester).sendMessage(new NetMessage("Server:" + this.socket.getInetAddress().toString(), req.requester, "denied", MessageType.DenyP2P));
    }

    private class CommandHandler extends Thread {
        @Override
        public void run() {
            while(!shutdown){
                Scanner scanner = new Scanner(System.in);
                if(scanner.hasNext()){
                    switch(scanner.next().toLowerCase()){
                        case "shutdown":
                            shutdown = true;
                            break;
                        case "users":
                            System.out.println("Listing all connections");
                            System.out.println("Connected Clients:");
                            int clientIndex = 0;
                            for(ConnectedClient client : clients.values()){
                                System.out.println("-" + clientIndex + ": " + client.username + " at " + client.connectionSocket.getInetAddress().toString());
                            }
                            System.out.println("Clients pending Connection:");
                            clientIndex = 0;
                            for(ConnectedClient client : pendingConfirmation){
                                System.out.println("-" + clientIndex + ": Client at " + client.connectionSocket.getInetAddress().toString());
                            }
                            System.out.println("Finished listing connections");
                            break;
                        case "help":
                            System.out.println("###Console Help###");
                            System.out.println("help:            Lists all commands and a description what they do.");
                            System.out.println("shutdown:        Stops the server and disconnects all users.");
                            System.out.println("users:           Lists all connected users and pending connections.");
                            System.out.println("###  End Help  ###");
                            break;
                        default:
                            System.err.println("Unknown command");
                            break;
                    }
                }
            }
            System.out.println("Shutting down...");
            for (ConnectedClient client : clients.values()) {
                try {
                    System.out.println("Stopping connection to client " + client.username + " at " + client.connectionSocket.getInetAddress().toString());
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                client.stop();
            }

            try {
                System.out.println("Closing Server Socket");
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Shut down...");
            System.exit(1);
        }
    }

}
