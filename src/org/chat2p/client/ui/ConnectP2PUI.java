package org.chat2p.client.ui;

import org.chat2p.api.MessageType;
import org.chat2p.api.NetMessage;
import org.chat2p.client.net.ConnectedServerClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ConnectP2PUI extends JFrame {

    ConnectedServerClient connection;

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
    }

    private void addUIElements(Container c){
        JButton btnRequestP2P = new JButton("Ask for P2P Connection");
        btnRequestP2P.setBounds(10, 10, 200, 25);
        btnRequestP2P.addActionListener(e -> {
            connection.sendMessage(new NetMessage(connection.username, "Server", "TestMessage", MessageType.Default));
        });
        c.add(btnRequestP2P);
    }

}
