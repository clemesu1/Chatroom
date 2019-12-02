package com.csa.ChatClient;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;


public class ChatClient {
    private final String serverName;
    private final int serverPort;
    private Socket socket;
    private OutputStream serverOut;
    private InputStream serverIn;
    private static BufferedReader bufferedIn;

    private ArrayList<UserStatusListener> userStatusListeners = new ArrayList<>();
    private ArrayList<MessageListener> messageListeners = new ArrayList<>();

    public ChatClient(String serverName, int serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;
    }

    /*public static void main(String[] args) throws IOException {
        ChatClient client = new ChatClient("localhost", 8818);  // Create a new instance of the client
        
        client.addUserStatusListener(new UserStatusListener() {
            @Override
            public void online(String login) {
                System.out.println("ONLINE: " + login);
            }

            @Override
            public void offline(String login) {
                System.out.println("OFFLINE: " + login);
            }
        });

        client.addMessageListener(new MessageListener() {
            @Override
            public void onMessage(String fromLogin, String msgBody) {
                System.out.println("You got a message from " + fromLogin + " ===>" + msgBody);
            }
        });

        if(!client.connect()) {
            System.err.println("Connect failed.");
        }
        else {
            System.out.println("Connection successful.");
//            String username = null;
//            String password = null;
//
//                
//            if(client.register(username, password)) {
//                System.out.println("Register Successful");
//            }
//            else {
//                System.err.println("Register Failed");
//            }
//                }
//                
//          if(client.login(username, password)) {
//          System.out.println("Login Successful");
//          //client.msg("test", "hello world");
//      }
//      else {
//          System.err.println("Login Failed");
//      }
        }
    }*/

    public void msg(String sendTo, String msgBody) throws IOException {
        String cmd = "msg " + sendTo + " " + msgBody + "\n";
        serverOut.write(cmd.getBytes());
    }

    public void logoff() throws IOException {
        String cmd = "quit\n\r";
        serverOut.write(cmd.getBytes());
    }

    public boolean register(String username, String password) throws IOException {
        String cmd = "register " + username + " " + password + "\n";
        serverOut.write(cmd.getBytes());
        String response = bufferedIn.readLine();
        System.out.println("Register Response Line: " + response);

        if((username + " registered").equals(response)) {
            return true;
        }
        else {
            return false;
        }

    }

    public boolean login(String username, String password) throws IOException {
        String cmd = "login " + username + " " + password + "\n";
        serverOut.write(cmd.getBytes());
        String response = bufferedIn.readLine();
        System.out.println("Response Line: " + response);

        if(("login " + username).equalsIgnoreCase(response)) {
            startMessageReader();
            return true;
        }
        else {
            return false;
        }
    }

    private void startMessageReader() {
        Thread t = new Thread() {
            @Override
            public void run() {
                readMessageLoop();
            }
        };
        t.start();
    }

    private void readMessageLoop() {
        try {
            String line;
            while ((line = bufferedIn.readLine()) != null) {
                String[] tokens = StringUtils.split(line);  // Split line into tokens
                if (tokens != null && tokens.length > 0) {
                    String cmd = tokens[0];
                    
                    if("online".equalsIgnoreCase(cmd)) {
                        handleOnline(tokens);
                    }
                    else if("offline".equalsIgnoreCase(cmd)) {
                        handleOffline(tokens);
                    }
                    else if("msg".equalsIgnoreCase(cmd)) {
                        String[] tokensMsg = StringUtils.split(line, null, 3);
                        handleMessage(tokensMsg);
                    }
                 }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleMessage(String[] tokensMsg) {
        String login = tokensMsg[1];
        String msgBody = tokensMsg[2];

        for(MessageListener listener : messageListeners) {
            listener.onMessage(login, msgBody);
        }

    }

    private void handleOffline(String[] tokens) {
        String login = tokens[1];
        for(UserStatusListener listener : userStatusListeners) {
            listener.offline(login);
        }
    }

    private void handleOnline(String[] tokens) {
        String login = tokens[1];
        for(UserStatusListener listener : userStatusListeners) {
            listener.online(login);
        }
    }

    public boolean connect() {
        try {
            this.socket = new Socket(serverName, serverPort); // Create a socket for the client
            System.out.println("Client port is " + socket.getLocalPort());
            this.serverOut = socket.getOutputStream();
            this.serverIn = socket.getInputStream();
            this.bufferedIn = new BufferedReader(new InputStreamReader(serverIn));

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void addUserStatusListener(UserStatusListener listener) {
        userStatusListeners.add(listener);
    }
    public void removeUserStatusListener(UserStatusListener listener) {
        userStatusListeners.remove(listener);
    }

    public void addMessageListener(MessageListener listener) {
        messageListeners.add(listener);
    }
    public void removeMessageListener(MessageListener listener) {
        messageListeners.remove(listener);
    }
    
    public void test() {
    	System.out.println("test");
    }

    public void closeConnection() throws IOException {
    	socket.close();
    }
}