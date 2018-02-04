package org.chat2p.api;

import java.io.Serializable;

public class NetMessage implements Serializable {

    public String sender, reciever;
    public Object message;
    public MessageType type;

    public NetMessage(String sender, String reciever, Object message, MessageType type){
        this.sender = sender;
        this.reciever = reciever;
        this.message = message;
        this.type = type;
    }

}
