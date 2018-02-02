package org.chat2p.client.ui;

import javax.swing.*;
import java.awt.*;

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

    /**
     * This method adds the JComponents to the Frame
     * @param c The ContentPane of this JFrame
     */
    private void addUIElements(Container c){
        //ClientStarter instruction JLabel
        JLabel instruction = new JLabel("Welcome. Please Setup for the connection:");
        instruction.setBounds(10, 10, 250, 25);
        c.add(instruction);
        //Which server for connecting to another client is used
        JLabel step1Server = new JLabel("To connect to others, please select your connection node:");
        step1Server.setBounds(10, 40, 350, 25);
        c.add(step1Server);
        //TextField to enter a custom server ip/url
        JTextField inputCustom = new JTextField();
        inputCustom.setBounds(480, 40, 125, 25);
        inputCustom.setEnabled(false);
        c.add(inputCustom);
        //ComboBox to choose a server either from a list or define a custom one
        JComboBox<String> nodeBox = new JComboBox<>(new String[]{"Default", "Internal", "Custom"});
        nodeBox.addActionListener(e -> {
            if(((String) nodeBox.getSelectedItem()).equalsIgnoreCase("custom")){
                inputCustom.setEnabled(true);
            }else{
                inputCustom.setEnabled(false);
            }
        });
        nodeBox.setBounds(370, 40, 100, 25);
        c.add(nodeBox);
        //Enter a nickname that's used to find other people by their name
        JLabel nickLabel = new JLabel("Enter a nickname (visible to everyone):");
        nickLabel.setBounds(10, 70, 250, 25);
        c.add(nickLabel);
        //Nickname TextField
        JTextField nickField = new JTextField();
        nickField.setBounds(270, 70, 200, 25);
        c.add(nickField);
    }

}