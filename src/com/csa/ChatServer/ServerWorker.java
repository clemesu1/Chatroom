package com.csa.ChatServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.csa.ChatServer.encryption.Crypto;
import com.csa.ChatServer.encryption.PasswordEncryption;

public class ServerWorker extends Thread {

    private final Socket clientSocket;
    private final Server server;

    private String login = null;
    private OutputStream outputStream;
    
    public static HashSet<User> userSet = new HashSet<>();
    public static HashSet<String> nameSet = new HashSet<>();
    
    private File database;
    private FileWriter fileWriter;
    private BufferedReader fileReader;
        
    private Crypto crypto = new PasswordEncryption();	

    public ServerWorker(Server server, Socket clientSocket) throws IOException {
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

        String line;
        while((line = reader.readLine()) != null) {
            String[] tokens = StringUtils.split(line);  // Split line into tokens
            if(tokens != null && tokens.length > 0) {

                String cmd = tokens[0];

                if("quit".equalsIgnoreCase(cmd) || "logoff".equalsIgnoreCase(cmd)) {
                    handleLogOff();
                    break;
                }
                else if("register".equalsIgnoreCase(cmd)) {
                    handleRegister(tokens, outputStream);
                }
                else if("login".equalsIgnoreCase(cmd)) {
                    handleLogin(outputStream, tokens);
                }
                else if("msg".equalsIgnoreCase(cmd)) {
                    String[] tokensMsg = StringUtils.split(line, null, 2);
                    handleMessage(tokensMsg);
                }
                else {
                    String msg = "Unknown command: " + cmd + "\n";
                    outputStream.write(msg.getBytes()); // Sends output back to the client
                }
            }
        }

        clientSocket.close();
    }

    private void handleRegister(String[] tokens, OutputStream outputStream) throws IOException {
        if(tokens.length == 3) {
            String username = tokens[1];
            String password = tokens[2];
            
            String fileName = "\\" + username + ".log";
            String directory = "C:\\Users\\coliw\\OneDrive\\Documents\\GitHub\\Chatroom\\src\\com\\csa\\ChatServer\\database";
            String fileLocation = directory + fileName;
            
            database = new File(fileLocation);
			fileWriter = new FileWriter(database, true);
            
            String filePassword = new String(crypto.encrypt(password.getBytes()));
            
            fileWriter.write(filePassword);
            fileWriter.close();
            
            User user = new User(username, password);
            userSet.add(user);
            
            String msg = username + " registered\n";
            outputStream.write(msg.getBytes());
            System.out.println("User " + username + " has registered successfully");

        }
            
    }
    
    private void handleLogin(OutputStream outputStream, String[] tokens) throws IOException {
    	boolean hasFile = false;
    	boolean inUserSet = false;
    	
    	if(tokens.length == 3) {
    		String username = tokens[1];
            String password = tokens[2];
            
            
            database = new File("C:\\Users\\coliw\\OneDrive\\Documents\\GitHub\\Chatroom\\src\\com\\csa\\ChatServer\\database\\");

            String fileCheck = username + ".log";
            
	    	File[] directory = database.listFiles();
	    	for(File file : directory) {
	    		if(file.isFile()) {
	    			if(fileCheck.equals(file.getName())) {
	    				fileReader = new BufferedReader(new FileReader(file));
	    				String passwordCheck = fileReader.readLine();
	    				fileReader.close();
	    				String decryptPassword = new String(crypto.decrypt(passwordCheck.getBytes()));
	    				if(decryptPassword.equals(password)) {
	    					hasFile = true;
	    					break;
	    				}
	    			}
	    		}
	    	}
	    	
	    	for(User user : userSet) {
	    		if(user.getUsername().equals(login) && user.getPassword().equals(password)) {
	    			inUserSet = true;
	    			break;
	    		}
	    	}
	    	
	    	if(hasFile || inUserSet) {
	    		 String msg = "ok login\n";
                 outputStream.write(msg.getBytes()); // Sends output back to the client
                 this.login = username; // Set login of this server worker to the user entered login string
                 System.out.println("User has logged in successfully: " + login);

                 List<ServerWorker> workerList = server.getWorkerList();

                 // Send current user all other users online
                 for(ServerWorker worker : workerList) {
                	 if(worker.getLogin() != null) {
                		 if(!login.equals(worker.getLogin())) {
	                		 String onlineUsers = "online " + worker.getLogin() + "\n";
	                		 send(onlineUsers);
                		 }
                	 }
                 }
                 
                 String onlineMsg = "online " + login + "\n";
                 for(ServerWorker worker : workerList) {
                	 if(!login.equals(worker.getLogin())) {
                    	 worker.send(onlineMsg);
                	 }
                 }
             }
             else {
                 String msg = "error login\n";
                 outputStream.write(msg.getBytes());
                 System.err.println("Login failed for " + username);
             }
    	}
  
    }
    
    private void handleMessage(String[] tokens) throws IOException {
        
        List<ServerWorker> workerList = server.getWorkerList();
        
        String msgBody = tokens[1];
        
        for(ServerWorker worker : workerList) {
        	if(!login.equals(worker.getLogin())) {
        		String outMsg = "msg " + login + " " + msgBody + "\n";
            	worker.send(outMsg);
        	}
        }
    }
    
    private void handleLogOff() throws IOException {
        List<ServerWorker> workerList = server.getWorkerList();

        String offlineMsg = "offline " + login + "\n";
        for(ServerWorker worker : workerList) {
            if(!login.equals(worker.getLogin())) {  // Stops it from sending yourself your own online status
                worker.send(offlineMsg);
            }
        }
        server.removeWorker(this);
        clientSocket.close();   // Close the client socket
    }
    
    public String getLogin() {
        return login;
    }

    private void send(String msg) throws IOException {
        if(login != null) { // Only send messages if login is not null
            outputStream.write(msg.getBytes());
        }
    }
    
}
