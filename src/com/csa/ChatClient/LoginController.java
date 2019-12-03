package com.csa.ChatClient;

import java.io.IOException;

import com.csa.Main;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class LoginController {
	@FXML
    private JFXTextField txtLoginUsername;
    
    @FXML
    private JFXPasswordField txtLoginPassword;
    
    @FXML
    private Button btnLoginUser;
    
    @FXML
    private Hyperlink btnRegisterWindow;
    
    private ChatClient client;
    
	public void LoginUser(ActionEvent event) throws IOException {
		
		client = Main.getClient();
		client.connect();
		
		String username = txtLoginUsername.getText();
		String password = txtLoginPassword.getText();
		
		if(client.login(username, password)) {
			
			Main.setUsername(username);
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/csa/ChatClient/Chatroom.fxml"));
			
			Parent root = loader.load();
			
			Scene scene = new Scene(root,600,500);
			Stage primaryStage = new Stage();

			primaryStage.setScene(scene);
			primaryStage.setTitle("Chatroom");
			primaryStage.setResizable(false);
			primaryStage.setTitle("Chatroom");
			primaryStage.show();
			
			Stage stage = (Stage) btnLoginUser.getScene().getWindow();
			stage.close();
			
	    }
	    else {
	    	Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Login Error");
			alert.setContentText("Error with user login.");
			alert.showAndWait();
	    }
	}
	
	public void RegisterWindow(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/csa/ChatClient/Register.fxml"));
			
			Parent root = loader.load();
			
			Scene scene = new Scene(root,400,400);
			Stage primaryStage = new Stage();
			        			
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.setTitle("Register Account");
			primaryStage.show();
			
			Stage stage = (Stage) btnLoginUser.getScene().getWindow();
			stage.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
