package com.csa.ChatServer;

//import java.net.InetAddress;

public class ServerMain {
  public static void main(String[] args) {
      int port = 8818;
      //InetAddress ip = InetAddress.getByName("127.0.0.1");
      Server server = new Server(port);
      server.start();
  }
}
