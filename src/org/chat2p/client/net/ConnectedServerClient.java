package org.chat2p.client.net;

import org.chat2p.api.MessageType;
import org.chat2p.api.NetMessage;
import org.chat2p.api.P2PConnectionRequest;
import org.chat2p.client.ui.ConnectP2PUI;

import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class ConnectedServerClient {

    public Socket server;
    public String username;

    public ObjectInputStream inputStream;
    public ObjectOutputStream outputStream;

    public boolean connected = false;
    boolean keepConnection = true;

    public ConnectedServerClient(String ip, int port, String username, JFrame startupUI){
        try {
            this.server = new Socket(ip, port);
            this.username = username;
            connect();
            if(connected){
                startupUI.setVisible(false);
                new ConnectP2PUI(this);
                new MessageHandler().start();
                //this.outputStream.writeObject(new NetMessage(this.username, "Server", "userlist", MessageType.ListUsers));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void connect() {
        try {
            System.out.println("Start connecting");
            this.outputStream = new ObjectOutputStream(server.getOutputStream());
            this.inputStream = new ObjectInputStream(server.getInputStream());
            System.out.println("Defined streams");
            this.outputStream.writeObject(new NetMessage(this.username, "Server", "request", MessageType.RequestConnection));
            System.out.println("Sent Connection Request to Server. Now waiting for response.");
            NetMessage response = (NetMessage) inputStream.readObject();
            if(response.reciever.equalsIgnoreCase(this.username) && ((String) response.message).equalsIgnoreCase("accepted") && response.type == MessageType.AcceptedConnection){
                System.out.println("Connected to the Server!");
                this.connected = true;
                return;
            }
            System.out.println("Received unknown message while trying to connect. Please try again!");
            disconnect();
        } catch (IOException | ClassCastException | ClassNotFoundException ex) {
            ex.printStackTrace();
            disconnect();
        }
    }

    public synchronized void disconnect() {
        try {
            System.out.println("Disconnecting!");
            if(this.connected){
                this.outputStream.writeObject(new NetMessage(this.username, "Server", "disconnect", MessageType.Disconnect));
            }
            this.connected = false;
            this.outputStream.close();
            this.inputStream.close();
            this.server.close();
            System.out.println("Disconnected!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class MessageHandler extends Thread {
        @Override
        public void run() {
            while (keepConnection){
                try {
                    if(inputStream.available() > 0) {
                        System.out.println("Received new Message");
                        NetMessage message = (NetMessage) inputStream.readObject();
                        switch (message.type) {
                            case RequestP2P:
                                //HandleRequest
                                break;
                            case IsUserOnline:
                                //HandleResponse
                                break;
                            case ListUsers:
                                System.out.println("User list received");
                                break;
                            case AcceptP2P:
                                //Start P2P Connection
                                break;
                            case DenyP2P:
                                //Show Info to user
                                break;
                            case Default:
                                System.out.println("Received NetMessage from server: " + message.message);
                                break;
                            case PING:
                                outputStream.writeObject(new NetMessage(username, "Server", "pingresponse", MessageType.PING));
                                break;
                            default:
                                System.out.println("Received Message from server: " + message.message);
                                break;
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    keepConnection = false;
                }
            }
            disconnect();
            stop();
        }
    }

}
