package org.chat2p.client.net;

import org.chat2p.api.MessageType;
import org.chat2p.api.NetMessage;
import org.chat2p.api.P2PConnectionRequest;
import org.chat2p.client.ui.ConnectP2PUI;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

@SuppressWarnings({"deprecation", "unchecked"})
public class ConnectedServerClient extends Thread {

    private Socket server;
    public String username;

    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;

    private boolean connected = false;
    public boolean keepConnection = true;

    public ArrayList<String> users;
    public long lastUpdateUserList = -1;

    private ConnectP2PUI ui;

    public ConnectedServerClient(String ip, int port, String username, JFrame startupUI){
        try {
            this.server = new Socket(ip, port);
            this.username = username;
            connect();
            if(connected){
                startupUI.setVisible(false);
                ui = new ConnectP2PUI(this);
                this.start();
                this.outputStream.writeObject(new NetMessage(this.username, "Server", "userlist", MessageType.ListUsers));
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
            }else {
                System.out.println("Received unknown message while trying to connect. Please try again!");
                disconnect();
            }
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

    public void sendMessage(NetMessage message){
        try {
            this.outputStream.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (keepConnection) {
            try {
                NetMessage message = (NetMessage) inputStream.readObject();
                switch (message.type) {
                    case RequestP2P:
                        if(message.message instanceof P2PConnectionRequest){
                            ui.p2pRequest((P2PConnectionRequest) message.message);
                        }else{
                            this.outputStream.writeObject(new NetMessage(this.username, "Server", "Format is false, requires String", MessageType.DenyP2P));
                }
                        break;
                    case IsUserOnline:
                        //HandleResponse
                        break;
                    case ListUsers:
                        if(message.message instanceof ArrayList){
                            users = (ArrayList<String>) message.message;
                            lastUpdateUserList = System.currentTimeMillis();
                        }
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
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                keepConnection = false;
            }
        }
        disconnect();
        stop();
    }
}
