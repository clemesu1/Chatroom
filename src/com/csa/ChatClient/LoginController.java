package com.csa.ChatClient;

import java.io.IOException;

import com.csa.Main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
    //ChatClient client = new ChatClient("localhost", 8818);  // Create a new instance of the client

    @FXML
    private TextField txtUsername;
    
    @FXML
    private PasswordField txtPassword;
    
    @FXML
    private Button btnLogin;
    
    @FXML
    private Hyperlink btnRegister;
    
    @FXML
    private Label lblMessage;
        
    private ChatClient client;
    
    public void Login(ActionEvent event) throws IOException {
    	
    	client = Main.getClient();
    	client.connect();
		String username = txtUsername.getText();
		String password = txtPassword.getText();
		
		if(client.login(username, password)) {
			System.out.println("Login Successful");
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/csa/ChatClient/Chatroom.fxml"));
			
			Parent root = loader.load();
			
			Scene scene = new Scene(root,600,500);
			Stage primaryStage = new Stage();

			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.setTitle("Chatroom");
			primaryStage.show();
			
			Stage stage = (Stage) btnLogin.getScene().getWindow();
			stage.close();
	    }
	    else {
	    	
	    	System.err.println("Login Failed");
	    	lblMessage.setText("Login Failed");
	    }
		
    }
    
    public void Register(ActionEvent event) {
    	try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/csa/ChatClient/Register.fxml"));
			
			Parent root = loader.load();
			
			Scene scene = new Scene(root,400,400);
			Stage primaryStage = new Stage();

			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.setTitle("Register Account");
			primaryStage.show();
			
			Stage stage = (Stage) btnLogin.getScene().getWindow();
			stage.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

}
