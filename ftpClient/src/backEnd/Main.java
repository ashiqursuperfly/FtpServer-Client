package frontEnd;

import backEnd.FtpClient;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.util.List;
import java.util.concurrent.BlockingQueue;

public class Main extends Application {

    Stage stage;

    ControllerTreeView controllerTreeView;

    @Override
    public void start(Stage primaryStage) throws Exception{
        stage = primaryStage;
        showLoginPage();
    }

    public ControllerTreeView getControllerTreeView() {
        return controllerTreeView;
    }

    public void showLoginPage() throws Exception {
        // XML Loading using FXMLLoader

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("login.fxml"));
        Parent root = loader.load();


        // Loading the controller
        LoginController controller = loader.getController();
        controller.setMain(this);

        // Set the primary stage
        stage.setTitle("FTP Client");
        stage.setScene(new Scene(root, 400, 400));
        stage.show();
    }

    public void showClientPage(Boolean isClient, BlockingQueue queue,ConcurrentHashMap<String,Vector<String>> treeViewMap) throws Exception {

        if(!isClient) {
            this.invalid(ErrorCode.CLIENT_AUTHENTIFICATION);

        }

        else {


            if(FtpClient.DEBUGGER) System.out.println("Loading : treeView.fxml");
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("treeView1.fxml"));
            Parent root = loader.load();


            // Loading the controller
            ControllerTreeView controller = loader.getController();

            controllerTreeView=controller;
            // Initialise The Client That was Found
            controller.init(this,stage, queue, treeViewMap);


            // Set the primary stage
            stage.setTitle("Logged in ");
            stage.setScene(new Scene(root, 900, 1000));
            stage.show();
        }
    }
    public  void updateLog(String msg)
    {
        controllerTreeView.updateLog(msg);
    }
    public void invalid(ErrorCode errorCode)
    {

        if(errorCode==ErrorCode.CLIENT_AUTHENTIFICATION)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error"+ErrorCode.CLIENT_AUTHENTIFICATION);
            alert.setHeaderText("Incorrect Credentials");
            alert.setContentText("The username and password you provided is not correct.");
            alert.showAndWait();

        }
        else if(errorCode==ErrorCode.UPLOAD_ERROR)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("!!!!!");
            alert.setHeaderText("ERROR :"+ErrorCode.UPLOAD_ERROR);
            alert.setContentText("Please Check The Following \n 1.Choose Valid Server Directory \n 2.Select a File From The Specific Client Directory");
            alert.showAndWait();

        }
        else if (errorCode==ErrorCode.NETWORKERROR)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("!!!!!");
            alert.setHeaderText("ERROR");
            alert.setContentText("NETWORK ERROR");
            alert.showAndWait();

        }
        else if(errorCode==ErrorCode.OBJECT_READ_ERROR)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("!!!!!");
            alert.setHeaderText("ERROR");
            alert.setContentText("OBJECT Read/Write Error.");
            alert.showAndWait();

        }
        else if(errorCode==ErrorCode.DOWNLOAD_ERROR)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("!!!!!");
            alert.setHeaderText("ERROR");
            alert.setContentText("Download Failed");
            alert.showAndWait();
        }
        else
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("!!!!!");
                alert.setHeaderText("ERROR");
                alert.setContentText("Please TRY Again.");
                alert.showAndWait();

            }







    }

    public static void main(String[] args) {
        launch(args);
    }

    public void help() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("HELP :");
        alert.setHeaderText("Ask Ashiq and Tasin :3");
        alert.setContentText("Please TRY Again.");
        alert.showAndWait();
    }
}
