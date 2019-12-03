package com.csa.ChatServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {

    private final int serverPort;

    ArrayList<ServerWorker> workerList = new ArrayList<>();

    public Server(int serverPort) {
        this.serverPort = serverPort;
    }

    public List<ServerWorker> getWorkerList() {
        return workerList;
    }

    @Override
    public void run() {
        try {
            @SuppressWarnings("resource")
			ServerSocket serverSocket = new ServerSocket(serverPort); // Sets up a server socket listening on port 8818
            while(true) {   // Continuously accept connections from the client
                System.out.println("Waiting to accept client connections...");
                Socket clientSocket; // Represents the connection to the client
                clientSocket = serverSocket.accept();  // Creates connection between server and client;
                System.out.println("Accepted connection from " + clientSocket);
                ServerWorker worker = new ServerWorker(this, clientSocket);   // Creates thread for each client connection
                workerList.add(worker);
                worker.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeWorker(ServerWorker serverWorker) {
        workerList.remove(serverWorker);
    }
}
