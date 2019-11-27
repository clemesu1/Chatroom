package com.csa.ChatClient;

public interface MessageListener {
    // Whenever somebody sends you a message, the other user will send the message to the server,
    // and the server will relay the message to the target user
    public void onMessage(String fromLogin, String msgBody);
}
