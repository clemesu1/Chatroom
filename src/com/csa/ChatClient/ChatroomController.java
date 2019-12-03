package com.csa.ChatClient;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ChatroomController implements Initializable, UserStatusListener, MessageListener {
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
	
	private File chatlog = new File("C:\\Users\\coliw\\OneDrive\\Documents\\GitHub\\Chatroom\\src\\com\\csa\\ChatServer\\database\\chatroom.log");
	private FileWriter fileWriter;	
	
	public void Logout(ActionEvent event) {
		
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
			String timeStamp = new Timestamp(System.currentTimeMillis()).toString();
	        timeStamp = timeStamp.substring(0, timeStamp.length() - 4);	
	        timeStamp = "[" + timeStamp + "]";
	        
			String message = txtMessageField.getText();	
			
			if(!message.isEmpty()) {
				client.msg(message);
				String chatMessage = timeStamp + " " + Main.getUsername() + ": " + message + "\n";
				txtChatArea.appendText(chatMessage);
				fileWriter = new FileWriter(chatlog, true);
				fileWriter.write(chatMessage);
				fileWriter.close();
				txtMessageField.setText("");
			}
			else {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Message Error");
				alert.setContentText("Cannot send blank message");
				alert.showAndWait();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void online(String login) {
		String timeStamp = new Timestamp(System.currentTimeMillis()).toString();
        timeStamp = timeStamp.substring(0, timeStamp.length() - 4);	
        timeStamp = "[" + timeStamp + "]";
		String chatMessage = timeStamp + " " + login + " is online\n";
		txtChatArea.appendText(chatMessage);
		try {
			fileWriter = new FileWriter(chatlog, true);
			fileWriter.write(chatMessage);
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		lstUserList.getItems().add(login);
	}

	@Override
	public void offline(String login) {
		String timeStamp = new Timestamp(System.currentTimeMillis()).toString();
        timeStamp = timeStamp.substring(0, timeStamp.length() - 4);	
        timeStamp = "[" + timeStamp + "]";
        String chatMessage = timeStamp + " " + login + " is offline\n";
		txtChatArea.appendText(chatMessage);
		try {
			fileWriter = new FileWriter(chatlog, true);
			fileWriter.write(chatMessage);
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		lstUserList.getItems().remove(login);
	}

	@Override
	public void onMessage(String fromLogin, String msgBody) {
		String timeStamp = new Timestamp(System.currentTimeMillis()).toString();
        timeStamp = timeStamp.substring(0, timeStamp.length() - 4);	
        timeStamp = "[" + timeStamp + "]";
		String line = fromLogin + ": " + msgBody;
		txtChatArea.appendText(timeStamp + " " + line + "\n");
		
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
