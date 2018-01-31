package org.chat2p;

import org.chat2p.ui.StartupFrame;

import java.awt.*;

/**
 * The Main Class
 */
public class Main {

    /**
     * The main method of the program
     * @param args Java Arguments
     */
    public static void main(String[] args){
        StartupFrame startup = new StartupFrame("Chat2p - Startup", Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height);
    }

}
