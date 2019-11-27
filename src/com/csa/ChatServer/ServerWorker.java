package com.csa.ChatServer;

import org.apache.commons.lang3.StringUtils;

import java.io.*;

import java.net.Socket;
import java.util.HashSet;
import java.util.List;

public class ServerWorker extends Thread {

    private final Socket clientSocket;
    private final Server server;

    private String login = null;
    private OutputStream outputStream;
    private HashSet<String> topicSet = new HashSet<>();
    private HashSet<User> userSet = new HashSet<>();


    public ServerWorker(Server server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            handleClientSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClientSocket() throws IOException {
        InputStream inputStream = clientSocket.getInputStream(); // Gets access to the input stream for reading data from the client
        this.outputStream = clientSocket.getOutputStream(); // access from client socket to get data from the client
        // Creates bi-directional communication with the client connection

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream)); // Reads input line-by-line

        //String help = "Type help for a list of commands\n\r";
        //outputStream.write(help.getBytes());

        String line;
        while((line = reader.readLine()) != null) {
            String[] tokens = StringUtils.split(line);  // Split line into tokens
            if(tokens != null && tokens.length > 0) {

                String cmd = tokens[0];

                if("quit".equalsIgnoreCase(cmd) || "logoff".equalsIgnoreCase(cmd)) {
                    handleLogOff();
                    break;
                }
                else if("help".equalsIgnoreCase(cmd)) {
                    handleHelp(outputStream);
                }
                else if("register".equalsIgnoreCase(cmd)) {
                    handleRegister(tokens, outputStream);
                }
                else if("login".equalsIgnoreCase(cmd)) {
                    handleLogin(outputStream, tokens);
                }
                else if("msg".equalsIgnoreCase(cmd)) {
                    String[] tokensMsg = StringUtils.split(line, null, 3);
                    handleMessage(tokensMsg);
                }
                else if("join".equalsIgnoreCase(cmd)) {
                    handleJoin(tokens);
                }
                else if("leave".equalsIgnoreCase(cmd)) {
                    handleLeave(tokens);
                }
                else {
                    String msg = "Unknown command: " + cmd + "\n\r";
                    outputStream.write(msg.getBytes()); // Sends output back to the client
                }
            }
        }

        clientSocket.close();
    }

    private void handleLeave(String[] tokens) {
        if(tokens.length > 1) {
            String topic = tokens[2];
            topicSet.remove(topic);
        }
    }

    private void handleRegister(String[] tokens, OutputStream outputStream) throws IOException {
        if(tokens.length == 3) {
            String username = tokens[1];
            String password = tokens[2];
            User user = new User(username, password);
            userSet.add(user);
            String msg = "User " + username + " registered\n\r";
            outputStream.write(msg.getBytes());
            System.out.println("User " + username + " has registered successfully");
        }
    }

    public boolean isMemberOfTopic(String topic) {
        return topicSet.contains(topic);
    }

    // maybe use this data type for storing registered users
    // register <USERNAME> <PASSWORD>
    // adds users to hash set userSet
    // if user tries to do command and isn't logged in print error
    // check if user is in set before doing anything


    // maybe have users automatically join general message group and message in that group
    private void handleJoin(String[] tokens) {
        // Store the membership of the user to a topic
        if(tokens.length > 1) {
            String topic = tokens[2];
            topicSet.add(topic);
        }
    }

    // format: msg <USERNAME> <MESSAGE>
    // format: msg #<TOPIC> <MESSAGE>
    private void handleMessage(String[] tokens) throws IOException {
        String sendTo = tokens[1];
        String body = tokens[2];

        boolean isTopic = sendTo.charAt(0) == '#';

        List<ServerWorker> workerList = server.getWorkerList();

        for(ServerWorker worker : workerList) {
            if(isTopic) {   // if message is meant for a topic
                if(worker.isMemberOfTopic(sendTo)) {
                    //String outMsg = "msg " + login + " " + body + "\n\r";
                    String outMsg = "(" + sendTo + ") " + login + "> " + body + "\n\r";
                    worker.send(outMsg);
                }
            }
            else {  // probably meant for a user
                if (sendTo.equalsIgnoreCase(worker.getLogin())) {
                    //String outMsg = "msg " + login + " " + body + "\n\r";
                    String outMsg = login + "> " + body + "\n\r";
                    worker.send(outMsg);
                }
            }
        }
    }

    private void handleLogOff() throws IOException {
        server.removeWorker(this);
        List<ServerWorker> workerList = server.getWorkerList();

        String offlineMsg = "offline " + login + "\n\r";
        for(ServerWorker worker : workerList) {
            if(!login.equals(worker.getLogin())) {  // Stops it from sending yourself your own online status
                worker.send(offlineMsg);
            }
        }
        clientSocket.close();   // Close the client socket
    }

    public String getLogin() {
        return login;
    }

    private void handleHelp(OutputStream outputStream) throws IOException { // Help class for list of commands
        String msg = "List of Commands:\n\r";
        msg += "register <USERNAME> <PASSWORD>\n\r";   // register command
        msg += "login <USERNAME> <PASSWORD>\n\r";   // login command
        msg += "msg <USERNAME> <MESSAGE>\n\r";  // direct messaging command
        msg += "join #<TOPIC>\n\r";    // join group command
        msg += "leave #<TOPIC>\n\r";    // leave group command
        msg += "msg #<TOPIC> <MESSAGE>\n\r";    // group messaging command
        msg += "logoff\n\r";    // logoff command
        msg += "quit\n\r";      // quit command


        outputStream.write(msg.getBytes()); // Sends output back to the client
    }

    private void handleLogin(OutputStream outputStream, String[] tokens) throws IOException {
        if(tokens.length == 3) {
            String login = tokens[1];
            String password = tokens[2];
            for(User user : userSet) {
                if(user.getUsername().equals(login) && user.getPassword().equals(password)) {
                    String msg = login + " logged in\n\r";
                    outputStream.write(msg.getBytes()); // Sends output back to the client
                    this.login = login; // Set login of this server worker to the user entered login string
                    System.out.println("User " + login + " has logged in successfully");

                    List<ServerWorker> workerList = server.getWorkerList();

                    // Send the current user all other online user's username's
                    for(ServerWorker worker : workerList) {
                        if(!login.equals(worker.getLogin())) {  // Stops it from sending yourself your own online status
                            if(worker.getLogin() != null) {
                                String localMsg = "online " + worker.getLogin() + "\n\r";
                                send(localMsg);
                            }
                        }
                    }

                    // Send other online users current user's online status
                    String onlineMsg = login + " is online\n\r";
                    for(ServerWorker worker : workerList) {
                        if(!login.equals(worker.getLogin())) {  // Stops it from sending yourself your own online status
                            worker.send(onlineMsg);
                        }
                    }
                }
                else {
                    String msg = "Login Error\n\r";
                    outputStream.write(msg.getBytes()); // Sends output back to the client
                    System.err.println("Login failed for " + login);
                }
            }
        }
    }

    private void send(String msg) throws IOException {
        if(login != null) { // Only send messages if login is not null
            outputStream.write(msg.getBytes());
        }
    }
}
