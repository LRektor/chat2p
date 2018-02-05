package org.chat2p.client.net;

import org.chat2p.api.MessageType;
import org.chat2p.api.NetMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ConnectedServerClient {

    public Socket server;
    public String username;

    public ObjectInputStream inputStream;
    public ObjectOutputStream outputStream;

    public boolean connected = false;

    public ConnectedServerClient(String ip, int port, String username){
        try {
            this.server = new Socket(ip, port);
            this.username = username;
            connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void connect() {
        try {
            this.inputStream = new ObjectInputStream(server.getInputStream());
            this.outputStream = new ObjectOutputStream(server.getOutputStream());
            this.outputStream.writeObject(new NetMessage(this.username, "Server", "request", MessageType.RequestConnection));
            NetMessage response = (NetMessage) inputStream.readObject();
            if(response.reciever == this.username && response.message == "accepted" && response.type == MessageType.AcceptedConnection){
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

    private void disconnect() {
        try {
            System.out.println("Disconnecting!");
            this.connected = false;
            this.outputStream.close();
            this.inputStream.close();
            this.server.close();
            System.out.println("Disconnected!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
