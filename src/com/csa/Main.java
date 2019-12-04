package com.csa;

import java.util.HashSet;

import com.csa.ChatClient.ChatClient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	
	private static ChatClient client;
	private static String username;
	private static HashSet<String> userList;
		
	@Override
	public void start(Stage primaryStage) {
			
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/com/csa/ChatClient/IPAddress.fxml"));
			Scene scene = new Scene(root,400,200);
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.setTitle("Please enter an IP Address");
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}

	public static void setClient(String ipAddress) {
		client = new ChatClient(ipAddress, 8818);
	}
	
	public static ChatClient getClient() {
		return client;
	}
	
	public static void setUsername(String name) {
		username = name;
	}
	
	public static String getUsername() {
		return username;
	}
	
	public static void setUserSet(HashSet<String> userSet) {
		userList = userSet;
	}
	
	public static HashSet<String> getUserList() {
		return userList;
	}
}
