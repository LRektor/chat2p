package org.chat2p.backend.net;

import org.chat2p.api.MessageType;
import org.chat2p.api.NetMessage;
import org.chat2p.api.P2PConnectionRequest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@SuppressWarnings("deprecation")
public class ConnectedClient extends Thread {

    Socket connectionSocket;

    private Server serverInstance;

    private ObjectInputStream inStream;
    private ObjectOutputStream outStream;

    String username;

    ConnectedClient(Server server, Socket socket){
        this.connectionSocket = socket;
        this.serverInstance = server;
        try {
            this.inStream = new ObjectInputStream(socket.getInputStream());
            this.outStream = new ObjectOutputStream(socket.getOutputStream());
            NetMessage message = (NetMessage) this.inStream.readObject();
            if(message.type == MessageType.RequestConnection){
                this.username = message.sender;
                serverInstance.clients.put(this.username, this);
                serverInstance.pendingConfirmation.remove(this);
                this.outStream.writeObject(new NetMessage("Server:" + serverInstance.socket.getInetAddress().toString(), this.username, "accepted", MessageType.AcceptedConnection));
            }else{
                this.inStream.close();
                this.outStream.close();
                this.connectionSocket.close();
                this.stop();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
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
        inStream.close();
        outStream.close();
        connectionSocket.close();
    }

    @Override
    public void run() {
        boolean keepConnection = true;
        while (keepConnection){
            try {
                NetMessage message = (NetMessage) inStream.readObject();
                switch (message.type){
                    case RequestP2P:
                        if(message.message instanceof String) {
                            serverInstance.requestP2P(this, serverInstance.clients.get(message.message));
                        }else{
                            outStream.writeObject(new NetMessage("Server:" + serverInstance.socket.getInetAddress().toString(), this.username, "Format is false, requires String", MessageType.DenyP2P));
                        }
                        break;
                    case IsUserOnline:
                        if(message.message instanceof String && serverInstance.clients.containsKey(message.message)){
                            outStream.writeObject(new NetMessage("Server:" + serverInstance.socket.getInetAddress().toString(), this.username, true, MessageType.IsUserOnline));
                        }else{
                            outStream.writeObject(new NetMessage("Server:" + serverInstance.socket.getInetAddress().toString(), this.username, false, MessageType.IsUserOnline));
                        }
                        break;
                    case AcceptP2P:
                        if(message.message instanceof P2PConnectionRequest){
                            serverInstance.acceptP2P((P2PConnectionRequest) message.message);
                        }else{
                            outStream.writeObject(new NetMessage("Server:" + serverInstance.socket.getInetAddress().toString(), this.username, "Failed to parse Response", MessageType.Error));
                        }
                        break;
                    case DenyP2P:
                        if(message.message instanceof P2PConnectionRequest){
                            serverInstance.denyP2P((P2PConnectionRequest) message.message);
                        }else{
                            outStream.writeObject(new NetMessage("Server:" + serverInstance.socket.getInetAddress().toString(), this.username, "Failed to parse Response", MessageType.Error));
                        }
                        break;
                    case Disconnect:
                        keepConnection = false;
                        break;
                    case Default:
                        System.out.println("Recieved NetMessage from client " + username + " at " + connectionSocket.getInetAddress().toString() + ": " + message.message);
                        break;
                    default:
                        System.out.println("Recieved Message from client " + username + " at " + connectionSocket.getInetAddress().toString() + ": " + message.message);
                        break;
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
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
