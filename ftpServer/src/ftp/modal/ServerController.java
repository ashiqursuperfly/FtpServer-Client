package ftp.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class ServerController {

    Main main;

    @FXML
    ListView<String> logger;
    @FXML
    ListView<String> userListView;
    List<String> activeUsers = Collections.synchronizedList(new ArrayList<String>());


    public void setMain(Main main) {
        this.main = main;
    }

    public void setPortLabel(String value) {
        portLabel.setText(value);


    }

    synchronized public void updateLog(String message) {


        String timeStamp = new SimpleDateFormat("hh:mm:ss").format(new Date());

        message = "[" + timeStamp + "]  " + message;

        logger.getItems().add(message);


    }

    synchronized public void addUser(String userName) {


        if (!activeUsers.contains(userName)) {
            userListView.getItems().add(userName);
            activeUsers.add(userName);
        }


    }

    synchronized public void removeUser(String userName) {


        userListView.getItems().remove(userName);
        activeUsers.remove(userName);

    }

    public void testButtonClick() {

    }


    @FXML
    private Label portLabel;


    //TODO Create a separate chart keeping track of user's that are logged in


}
