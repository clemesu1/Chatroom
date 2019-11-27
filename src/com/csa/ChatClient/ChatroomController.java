package com.csa.ChatClient;

import com.csa.Main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

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
		//client = Main.getClient();
	}
	
	public void sendMessage(ActionEvent event) {
		
	}
}
