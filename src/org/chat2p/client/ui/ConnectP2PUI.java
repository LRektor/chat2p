package org.chat2p.client.ui;

import org.chat2p.client.net.ConnectedServerClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ConnectP2PUI extends JFrame {

    public ConnectP2PUI(ConnectedServerClient backgroudClient){
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

    }

}
