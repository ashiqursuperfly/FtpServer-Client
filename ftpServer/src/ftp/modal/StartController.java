package ftp.controller;

import ftp.modal.ServerAccess;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;


public class StartController {

    private Main main;

    @FXML
    private TextField nPortText;

    String nPortTextInput;





    @FXML
    private Button startButton;




    @FXML
    void startButtonAction (ActionEvent event) {




        nPortTextInput = nPortText.getText();






        /*try {
            main.showServerPage(nPortTextInput);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        ServerAccess.startServer(nPortTextInput, this, main);





    }

    public void showPortConnectError(String Header, String details)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(Header);
        alert.setContentText(details);
        alert.showAndWait();
    }



    void setMain(Main main) {
        this.main = main;
    }

}
