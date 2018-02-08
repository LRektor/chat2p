package org.chat2p.backend.net;

import org.chat2p.api.MessageType;
import org.chat2p.api.NetMessage;
import org.chat2p.api.P2PConnectionRequest;
import org.chat2p.api.logger.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;

@SuppressWarnings("deprecation")
public class ConnectedClient extends Thread {

    Socket connectionSocket;

    private Server serverInstance;

    private ObjectInputStream inStream;
    private ObjectOutputStream outStream;

    String username;

    ConnectedClient(Server server, Socket socket){
        System.out.println("Starting new client connection at " + socket.getInetAddress().toString());
        server.pendingConfirmation.add(this);
        this.connectionSocket = socket;
        this.serverInstance = server;
        try {
            this.outStream = new ObjectOutputStream(socket.getOutputStream());
            this.inStream = new ObjectInputStream(socket.getInputStream());
            System.out.println("Waiting for connection request.");
            NetMessage message = (NetMessage) this.inStream.readObject();
            System.out.println("Received connection request");
            if(message.type == MessageType.RequestConnection){
                System.out.println("User connected with username " + message.sender);
                this.username = message.sender;
                serverInstance.clients.put(this.username, this);
                serverInstance.pendingConfirmation.remove(this);
                System.out.println("Starting connection listening");
                this.start();
                this.outStream.writeObject(new NetMessage("Server:" + serverInstance.socket.getInetAddress().toString(), this.username, "accepted", MessageType.AcceptedConnection));
            }else{
                serverInstance.pendingConfirmation.remove(this);
                this.inStream.close();
                this.outStream.close();
                this.connectionSocket.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            serverInstance.pendingConfirmation.remove(this);
            e.printStackTrace();
            try {
                this.connectionSocket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    void sendMessage(NetMessage message){
        try {
            this.outStream.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void close() throws IOException {
        System.out.println("Closing connection");
        serverInstance.clients.remove(this.username, this);
        serverInstance.pendingConfirmation.remove(this);
        inStream.close();
        outStream.close();
        connectionSocket.close();
    }

    @Override
    public void run() {
        boolean keepConnection = true;
        long lastPing = System.currentTimeMillis();
        boolean pingSent = false;
        while (keepConnection){
            try {
                if(inStream.available() > 0) {
                    System.out.println("Received new Message");
                    NetMessage message = (NetMessage) inStream.readObject();
                    switch (message.type) {
                        case RequestP2P:
                            if (message.message instanceof String) {
                                serverInstance.requestP2P(this, serverInstance.clients.get(message.message));
                            } else {
                                outStream.writeObject(new NetMessage("Server:" + serverInstance.socket.getInetAddress().toString(), this.username, "Format is false, requires String", MessageType.DenyP2P));
                            }
                            break;
                        case IsUserOnline:
                            if (message.message instanceof String && serverInstance.clients.containsKey(message.message)) {
                                outStream.writeObject(new NetMessage("Server:" + serverInstance.socket.getInetAddress().toString(), this.username, true, MessageType.IsUserOnline));
                            } else {
                                outStream.writeObject(new NetMessage("Server:" + serverInstance.socket.getInetAddress().toString(), this.username, false, MessageType.IsUserOnline));
                            }
                            break;
                        case ListUsers:
                            outStream.writeObject(new NetMessage("Server:" + serverInstance.socket.getInetAddress().toString(), this.username, serverInstance.clients, MessageType.ListUsers));
                            break;
                        case AcceptP2P:
                            if (message.message instanceof P2PConnectionRequest) {
                                serverInstance.acceptP2P((P2PConnectionRequest) message.message);
                            } else {
                                outStream.writeObject(new NetMessage("Server:" + serverInstance.socket.getInetAddress().toString(), this.username, "Failed to parse Response", MessageType.Error));
                            }
                            break;
                        case DenyP2P:
                            if (message.message instanceof P2PConnectionRequest) {
                                serverInstance.denyP2P((P2PConnectionRequest) message.message);
                            } else {
                                outStream.writeObject(new NetMessage("Server:" + serverInstance.socket.getInetAddress().toString(), this.username, "Failed to parse Response", MessageType.Error));
                            }
                            break;
                        case Disconnect:
                            System.out.println("Disconnect from client " + username + " at " + connectionSocket.getInetAddress().toString());
                            keepConnection = false;
                            break;
                        case Default:
                            Logger.log("Default Message", "Received Message from client " + username + " at " + connectionSocket.getInetAddress().toString() + ": " + message.message, 3);
                            break;
                        case PING:
                            lastPing = System.currentTimeMillis();
                            pingSent = false;
                            Logger.log("Ping", "Client " + this.username + " at " + this.connectionSocket.getInetAddress().toString() + " answered the ping request.", 3);
                            break;
                        default:
                            Logger.log("Default Message", "Received Message from client " + username + " at " + connectionSocket.getInetAddress().toString() + ": " + message.message, 3);
                            break;
                    }
                }
                if((System.currentTimeMillis() - lastPing) >= 5000){
                    if((System.currentTimeMillis() - lastPing) >= 15000){
                        keepConnection = false;
                        Logger.log("Timeout", "Client " + this.username + " at " + this.connectionSocket.getInetAddress().toString() + " timed out.", 1);
                    }
                    if(!pingSent) {
                        Logger.log("Ping", "Sending ping to user " + this.username + " at " + this.connectionSocket.getInetAddress().toString(), 2);
                        outStream.writeObject(new NetMessage("Server:" + serverInstance.socket.getInetAddress().toString(), this.username, "ping", MessageType.PING));
                        pingSent = true;
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                keepConnection = false;
            }
        }
        try {
            close();
            stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
