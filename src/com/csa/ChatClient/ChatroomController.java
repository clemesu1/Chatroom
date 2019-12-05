package com.csa.ChatClient;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import com.csa.Main;

import javafx.application.Platform;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ChatroomController implements Initializable, UserStatusListener, MessageListener {
	
	/* CHATROOM */
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
	
	private ChatClient client;
	
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
			Timestamp time = new Timestamp(new Date().getTime());
	        String timeStamp = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(time);
	        timeStamp = "[" + timeStamp + "]";
	        
			String message = txtMessageField.getText();	
			
			if(!message.isEmpty()) {
				client.msg(message);
				String chatMessage = timeStamp + " " + Main.getUsername() + ": " + message + "\n";
				txtChatArea.appendText(chatMessage);
				
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
	
	/* FILE SHARING */
	@FXML
	private Label lblFileChosen;
	
	@FXML
	private ListView<File> lstFileList; 
	
	private File file;
	
	public void chooseFile(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		file = fileChooser.showOpenDialog(null);
		
		if(file != null)
			lblFileChosen.setText("File Chosen: " + file.getName());
		else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("File Error");
			alert.setContentText("Error with file selected");
			alert.showAndWait();
		}
	}
	
	public void uploadFile(ActionEvent event) {
		if(file != null)
			lstFileList.getItems().add(file);
		else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("File Upload Error");
			alert.setContentText("File does not exist");
			alert.showAndWait();
			
		}
	}

	@Override
	public void online(String login) {
		
		Timestamp time = new Timestamp(new Date().getTime());
        String timeStamp = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(time);
        timeStamp = "[" + timeStamp + "]";
        
		String chatMessage =  timeStamp + " " + login + " is online\n";
		
		txtChatArea.appendText(chatMessage);
		
		Platform.runLater(new Runnable(){
			@Override
			public void run() {
				lstUserList.getItems().add(login);
			}
		});
		
	}

	@Override
	public void offline(String login) {
	
		Timestamp time = new Timestamp(new Date().getTime());
        String timeStamp = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(time);
        timeStamp = "[" + timeStamp + "]";
        String chatMessage = timeStamp + " " +  login + " is offline\n";
		txtChatArea.appendText(chatMessage);
		
		
		Platform.runLater(new Runnable(){
			@Override
			public void run() {
				lstUserList.getItems().remove(login);
			}
		});
	}

	@Override
	public void onMessage(String fromLogin, String msgBody) {
		Timestamp time = new Timestamp(new Date().getTime());
        String timeStamp = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(time);
        timeStamp = "[" + timeStamp + "]";
		String line = fromLogin + ": " + msgBody;
		txtChatArea.appendText(timeStamp + " " + line + "\n");
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		lblWelcome.setText("Welcome " + Main.getUsername() + "!");
				
		lstUserList.getItems().add(Main.getUsername());
		
		client = Main.getClient();
		
		client.addUserStatusListener(this);
		client.addMessageListener(this);	
		
	}
}
