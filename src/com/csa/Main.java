package com.csa;
	

import com.csa.ChatClient.ChatClient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {
	
	private static ChatClient client;
	
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
	
	public static ChatClient getClient() {
		return client;
	}
	
	public static void setClient(String ipAddress) {
		client = new ChatClient(ipAddress, 8818);
	}
	
	

}
