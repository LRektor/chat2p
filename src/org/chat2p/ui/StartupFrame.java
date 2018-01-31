package org.chat2p.ui;

import javax.swing.*;

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
        this.setVisible(true);
    }

}
