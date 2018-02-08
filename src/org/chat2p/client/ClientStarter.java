package org.chat2p.client;

import org.chat2p.client.ui.StartupFrame;

import java.awt.*;

/**
 * The ClientStarter Class
 */
public class ClientStarter {

    /**
     * The main method of the program
     * @param args Java Arguments
     */
    public static void main(String[] args){
        new StartupFrame("Chat2p - Startup", Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height);
    }

}
