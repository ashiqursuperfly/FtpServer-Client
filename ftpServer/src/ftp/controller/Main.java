package ftp.controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    Stage stage;

    ServerController serverControllerInstance = null;

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        showLoginPage();
    }

    public void showLoginPage() throws Exception {
        // XML Loading using FXMLLoader
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("start.fxml"));
        Parent root = loader.load();

        // Initialising All the Users


        // Loading the controller
        StartController controller = loader.getController();
        controller.setMain(this);

        // Set the primary stage
        stage.setTitle("FTP Client");
        stage.setScene(new Scene(root, 400, 300));
        stage.show();
    }

    public void showServerPage(String portNumber) throws Exception {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("server.fxml"));
        Parent root = loader.load();

        // Loading the controller
        ServerController controller = loader.getController();
        serverControllerInstance = controller;


        controller.setMain(this);
        controller.setPortLabel(portNumber);

        controller.updateLog("Server started");

        // Set the primary stage
        stage.setTitle("Server");
        stage.setScene(new Scene(root, 655, 365));
        stage.show();


    }

    public ServerController getServerControllerInstance() {
        return serverControllerInstance;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
