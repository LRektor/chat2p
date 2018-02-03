package org.chat2p.api;

public class P2PConnectionRequest {

    public String requester, requested;
    public boolean processed = false, accepted = false;

    public P2PConnectionRequest(String requester, String requested){
        this.requested = requested;
        this.requester = requester;
    }

}
