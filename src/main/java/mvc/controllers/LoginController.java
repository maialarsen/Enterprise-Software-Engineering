package mvc.controllers;

import exceptions.UnauthorizedException;
import exceptions.UnknownException;
import gateways.Session;
import gateways.SessionGateway;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
    void login(ActionEvent event) throws IOException {
        String username = this.username.getText();
        String password = this.password.getText();

        try {
            Session sessionId = SessionGateway.login(username, password);
        } catch(UnauthorizedException e) {
            logger.error("Login failed! Either your username or password is incorrect");
            return;
        } catch (UnknownException e) {
            logger.error("Login failed! Something went wrong please try again");
            return;
        }

        logger.info(this.username.getText() + " LOGGED IN");
        ViewSwitcher.getInstance().switchView(ViewScreen.PEOPLELISTVIEW);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
