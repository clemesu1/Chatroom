package com.csa.ChatClient;

import java.io.IOException;

import com.csa.Main;
import com.jfoenix.controls.JFXTextField;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class IPAddressController {
public String ipAddress;
	
	public ChatClient client;

	/* IP ADDRESS WINDOW */
	@FXML
	private JFXTextField txtIPAddress;
	
	@FXML
	private Button btnContinue;
	
	public void processIPAddress(ActionEvent event) {
		
		ipAddress = txtIPAddress.getText();
		
		if(ipAddress.isEmpty()) {	
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("IP Address Error");
			alert.setContentText("IP Address field is empty.");
			alert.showAndWait();
		}
		else {
			
		Main.setClient(ipAddress);
		ChatClient client = Main.getClient();
			
			if(client.connect()) {
				try {
					FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/csa/ChatClient/Login.fxml"));
					Parent root = loader.load();
					Scene scene = new Scene(root,400,400);
					Stage primaryStage = new Stage();
					        			
					primaryStage.setScene(scene);
					primaryStage.setResizable(false);
					primaryStage.setTitle("Login to Chatroom");
					primaryStage.show();
					
					Stage stage = (Stage) btnContinue.getScene().getWindow();
					stage.close();
					
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			} 
			else {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Connection Error");
				alert.setContentText("Error connecting to server.");
				alert.showAndWait();
			}
		}
	}
}
