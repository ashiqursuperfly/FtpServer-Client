 package frontEnd;

import backEnd.FtpClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;


 public class LoginController {

    private Main main;

    @FXML
    private TextField userNameText;

    @FXML
    private PasswordField passwordText;

    @FXML
    private Button resetButton;

    @FXML
    private Button loginButton;
    @FXML
    private TextField portText;
    @FXML
    private TextField serverAddressText;

    private String[] loginData;

    @FXML
    void loginButtonAction(ActionEvent event) {

        loginData=new String[5];

        loginData[0]=(serverAddressText.getText());
        loginData[1]=(portText.getText());
        loginData[2]=(userNameText.getText());
        loginData[3]=(passwordText.getText());

        FtpClient.backEnd(loginData, main);
    }



    @FXML
    void resetButtonAction(ActionEvent event) {
        userNameText.setText(null);
        passwordText.setText(null);
    }


    void setMain(Main main) {
        this.main = main;
    }

}
