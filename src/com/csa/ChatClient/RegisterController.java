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

public class RegisterController {
	
	@FXML
	private TextField txtUsername;
	
	@FXML
	private PasswordField txtPassword;
	
	@FXML
	private PasswordField txtConfirmPassword;
	
	@FXML
	private Button btnRegister;
	
	@FXML
	private Hyperlink btnLogin;
	
	@FXML
	private Label lblError;
	
	private ChatClient client;

	public void Register(ActionEvent event) throws IOException {
		
		client = Main.getClient();
		client.connect();
		
		String username;
		String password;
		
		if(txtPassword.getText().equals(txtConfirmPassword.getText())) {
			username = txtUsername.getText();	// Get text from username textfield
			password = txtPassword.getText();	// Get text from password textfield
						
			if(client.register(username, password)) {
                System.out.println("Register Successful");
                if(client.login(username, password)) {
                	System.out.println("Login Successfull");
                }
                else {
                	System.err.println("Login Failed");
                }
            }
            else {
                System.err.println("Register Failed");
            }
		}
		else {
			lblError.setText("Please make sure your passwords match");
		}
		
	}
	
	public void Login(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/csa/ChatClient/Login.fxml"));
			
			Parent root = loader.load();
			
			Scene scene = new Scene(root,400,400);
			Stage primaryStage = new Stage();

			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.setTitle("Login to Chatroom");
			primaryStage.show();
			
			Stage stage = (Stage) btnLogin.getScene().getWindow();
			stage.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	 
}
