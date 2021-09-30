package mvc.controllers;

import exceptions.UnauthorizedException;
import exceptions.UnknownException;
import gateways.Session;
import gateways.SessionGateway;
import hash.HashUtils;
import javafx.Alerts;
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
        Session sessionId = null;
        String username = this.username.getText();
        String hash = HashUtils.getCryptoHash(this.password.getText(), "SHA-256");

        try {
            sessionId = SessionGateway.login(username, hash);
        } catch(UnauthorizedException e) {
            Alerts.infoAlert("Login Failed", "Invalid username or password");
            return;
        } catch (UnknownException e) {
            Alerts.infoAlert("Login Failed", "Something went wrong, please try again");
            return;
        }

        logger.info(this.username.getText() + " LOGGED IN");
        logger.info(sessionId);
        ViewSwitcher.getInstance().setSession(sessionId);
        ViewSwitcher.getInstance().switchView(ViewScreen.PEOPLELISTVIEW);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
