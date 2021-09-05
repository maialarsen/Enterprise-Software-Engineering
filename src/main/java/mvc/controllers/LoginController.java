package mvc.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class LoginController implements Initializable {
    private static final Logger logger = LogManager.getLogger();
    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private Button loginBtn;

    @FXML
    void login(ActionEvent event) throws IOException {
        logger.info(username.getText() + " LOGGED IN");
        ViewSwitcher.getInstance().switchView(ViewScreen.PEOPLELISTVIEW);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
