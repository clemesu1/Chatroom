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
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ChatroomController {
	@FXML
	private Label lblWelcome;
	
	@FXML
	private TextArea txtChatArea;
	
	@FXML
	private TextField txtMessageField;
	
	@FXML
	private Button btnSend;
	
	@FXML
	private ListView<String> lstUserList;
	
	@FXML
	private Hyperlink btnLogout;
	
    
	private ChatClient client = Main.getClient();
	
	
	public void Logout(ActionEvent event) {
		client = Main.getClient();
		
		try {
			client.logoff();

			FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/csa/ChatClient/Login.fxml"));
			
			Parent root = loader.load();
			
			Scene scene = new Scene(root,400,400);
			Stage primaryStage = new Stage();

			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.setTitle("Login to Chatroom");
			primaryStage.show();
			
			Stage stage = (Stage) btnSend.getScene().getWindow();
			stage.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendMessage(ActionEvent event) {
		
	}
}
