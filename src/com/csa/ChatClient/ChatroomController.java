package com.csa.ChatClient;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;

import com.csa.Main;
import com.csa.ChatServer.ServerWorker;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ChatroomController implements Initializable, UserStatusListener, MessageListener	{
	@FXML
	private Label lblWelcome;
	
	@FXML
	private TextArea txtChatArea;
	
	@FXML
	private TextField txtMessageField;
	
	@FXML	
	private Button btnSend;
	
	@FXML
	private ListView<String> lstUserList = new ListView<>();
	
	@FXML
	private Hyperlink btnLogout;
	
    
	private ChatClient client;
		
	
	public void Logout(ActionEvent event) {
		
		//client.connect();
		
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
		
		try {
			String message = txtMessageField.getText();			
			client.msg(message);
			txtChatArea.appendText(Main.getUsername() + ": " + message + "\n");
			txtMessageField.setText("");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void online(String login) {
		txtChatArea.appendText(login + " is online\n");
		lstUserList.getItems().add(login);
		//lstUserList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	}

	@Override
	public void offline(String login) {
		txtChatArea.appendText(login + " is offline\n");
		lstUserList.getItems().remove(login);
		//lstUserList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	}

	@Override
	public void onMessage(String fromLogin, String msgBody) {
		String line = fromLogin + " " + msgBody;
		txtChatArea.appendText(line + "\n");
		
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		lblWelcome.setText("Welcome " + Main.getUsername() + "!");
		
		HashSet<String> nameSet = ServerWorker.getNameSet();
		lstUserList.getItems().addAll(nameSet);	
		
		client = Main.getClient();
		
		client.addUserStatusListener(this);
		client.addMessageListener(this);
		
	}

}
