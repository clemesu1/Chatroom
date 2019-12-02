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
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class RegisterController {
	@FXML
    private JFXTextField txtRegisterUsername;
    
    @FXML
    private JFXPasswordField txtRegisterPassword;
    
    @FXML
    private JFXPasswordField txtConfirmPassword;
    
    @FXML
    private Button btnRegisterUser;
    	
    @FXML
    private Hyperlink btnLoginWindow;
    
    @FXML
    private Label lblError;
    
    private ChatClient client;
	
	public void RegisterUser(ActionEvent event) throws IOException {
		
		client = Main.getClient();
		
		String username = txtRegisterUsername.getText();
		String password = txtRegisterPassword.getText();
		
		if(password.equals(txtConfirmPassword.getText())) {
			if(client.register(username, password)) {
                try {
        			FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/csa/ChatClient/Login.fxml"));
        			
        			Parent root = loader.load();
        			
        			Scene scene = new Scene(root,400,400);
        			Stage primaryStage = new Stage();
        			        			
        			primaryStage.setScene(scene);
        			primaryStage.setResizable(false);
        			primaryStage.setTitle("Login to Chatroom");
        			primaryStage.show();
        			
        			Stage stage = (Stage) btnRegisterUser.getScene().getWindow();
        			stage.close();
        			client.logoff();
        			
        		} catch (IOException e) {
        			e.printStackTrace();
        		}
            }
            else {
            	Alert alert = new Alert(AlertType.ERROR);
    			alert.setTitle("User Registration Error");
    			alert.setContentText("Error with user registration.");
    			alert.showAndWait();
            }
		}
		else {
			lblError.setText("Please make sure your passwords match");
		}
		

	}
	
	public void LoginWindow(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/csa/ChatClient/Login.fxml"));
			
			Parent root = loader.load();
			
			Scene scene = new Scene(root,400,400);
			Stage primaryStage = new Stage();

			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.setTitle("Login to Chatroom");
			primaryStage.show();
			
			Stage stage = (Stage) btnLoginWindow.getScene().getWindow();
			stage.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
