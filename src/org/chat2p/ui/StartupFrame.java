package org.chat2p.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class contains the startup frame of the application
 */
public class StartupFrame extends JFrame {

    public StartupFrame(String title, int screenwidth, int screenheigth){
        super(title);
        int frameWidth = screenwidth / 3;
        int frameHeight = screenheigth / 2;
        this.setBounds(frameWidth, frameHeight / 2, frameWidth, frameHeight);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setLayout(null);
        this.addUIElements(this.getContentPane());
        this.setVisible(true);
    }

    private void addUIElements(Container c){
        JLabel instruction = new JLabel("Welcome. Please Setup for the connection:");
        instruction.setBounds(10, 10, 250, 25);
        c.add(instruction);
        JLabel step1Server = new JLabel("To connect to others, please select your connection node:");
        step1Server.setBounds(10, 40, 350, 25);
        c.add(step1Server);
        JTextField inputCustom = new JTextField();
        inputCustom.setBounds(480, 40, 125, 25);
        inputCustom.setEnabled(false);
        c.add(inputCustom);
        JComboBox<String> nodeBox = new JComboBox<>(new String[]{"Internal", "Custom"});
        nodeBox.addActionListener(e -> {
            if(((String) nodeBox.getSelectedItem()).equalsIgnoreCase("custom")){
                inputCustom.setEnabled(true);
            }else{
                inputCustom.setEnabled(false);
            }
        });
        nodeBox.setBounds(370, 40, 100, 25);
        c.add(nodeBox);
    }

}
