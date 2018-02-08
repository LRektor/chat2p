package org.chat2p.client.ui;

import com.sun.xml.internal.ws.api.streaming.XMLStreamWriterFactory;
import org.chat2p.api.MessageType;
import org.chat2p.api.NetMessage;
import org.chat2p.api.P2PConnectionRequest;
import org.chat2p.client.net.ConnectedServerClient;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ConnectP2PUI extends JFrame {

    private ConnectedServerClient connection;

    //Components to Update
    private DefaultListModel<String> userList;

    public ConnectP2PUI(ConnectedServerClient backgroudClient){
        connection = backgroudClient;
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                backgroudClient.disconnect();
            }
        });
        this.setTitle("Chat 2P");
        this.setBounds(100, 100, 500, 300);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setLayout(null);
        this.addUIElements(this.getContentPane());
        this.setVisible(true);
        new UIUpdater().start();
    }

    private void addUIElements(Container c){
        DefaultListModel<String> model = new DefaultListModel<>();
        JList<String> userList = new JList<>(model);
        this.userList = model;
        userList.setBounds(10, 10, this.getWidth() - 50, 200);
        c.add(userList);
        JButton sendRequest = new JButton("Request P2P");
        sendRequest.setBounds(10, 220, this.getWidth() - 50, 25);
        sendRequest.addActionListener(e -> {
            connection.sendMessage(new NetMessage(connection.username, "Server", userList.getSelectedValue(), MessageType.RequestP2P));
        });
        c.add(sendRequest);
        userList.addListSelectionListener(e -> {
            if(userList.getSelectedValue() != null && userList.getSelectedValue().equalsIgnoreCase("")){
                sendRequest.setEnabled(false);
            }else{
                sendRequest.setEnabled(true);
            }
        });
    }

    private void updateComponents(){
        this.userList.clear();
        for(String user : connection.users){
            this.userList.addElement(user);
        }
    }

    public void p2pRequest(P2PConnectionRequest request){
        if(JOptionPane.showConfirmDialog(this, "Do you want to connect to user " + request.requester + "?", "Allow Connection", JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION){
            connection.sendMessage(new NetMessage(connection.username, "Server", request, MessageType.AcceptP2P));
        }else{
            connection.sendMessage(new NetMessage(connection.username, "Server", request, MessageType.DenyP2P));
        }
    }

    private class UIUpdater extends Thread {
        @Override
        public void run() {
            long lastUserUpdate = 0;
            while(connection.keepConnection){
                if(connection.lastUpdateUserList > lastUserUpdate){
                    lastUserUpdate = System.currentTimeMillis();
                    updateComponents();
                }
                if(System.currentTimeMillis() >= lastUserUpdate + 2500){
                    connection.sendMessage(new NetMessage(connection.username, "Server", "userlist", MessageType.ListUsers));
                }
            }
        }
    }

}
