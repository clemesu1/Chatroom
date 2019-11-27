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
			client = new ChatClient("localhost", 8818);
			Parent root = FXMLLoader.load(getClass().getResource("/com/csa/ChatClient/Login.fxml"));
			Scene scene = new Scene(root,400,400);
			primaryStage.setScene(scene);
			primaryStage.show();
			client.connect();
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
	
}
