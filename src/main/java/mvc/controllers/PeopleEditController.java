package mvc.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import mvc.models.Person;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PeopleEditController implements Initializable {
    private Person person;
    private boolean editMode;
    private static final Logger logger = LogManager.getLogger();

    public PeopleEditController() {
        this.person = null;
        this.editMode = false;
    }
    public PeopleEditController(Person person) {
        this.person = person;
        this.editMode = true;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (editMode) {
            firstName.setText(person.getFirstName());
            lastName.setText(person.getLastName());
            dateOfBirth.setValue(person.getDateOfBirth());
            idNum.setText(Integer.toString(person.getId()));
        } else {
            firstName.setText("");
            lastName.setText("");
            dateOfBirth.setValue(LocalDate.now());
            idNum.setText("");
        }
    }

    @FXML
    private TextField firstName;

    @FXML
    private TextField lastName;

    @FXML
    private Button cancelBtn;

    @FXML
    private DatePicker dateOfBirth;

    @FXML
    private TextField idNum;

    @FXML
    private Button saveBtn;

    @FXML
    void save(ActionEvent event) throws IOException {
        if(firstName.getText().isBlank() || lastName.getText().isBlank() || idNum.getText().isBlank()) {
            logger.error("One or more fields have been left blank");
            return;
        }

        try {
            Integer.parseInt(idNum.getText());
        } catch (NumberFormatException e) {
            logger.error("ID " + idNum.getText() + " is not a number");
            return;
        }

        if (editMode) {
            this.person.setFirstName(firstName.getText());
            this.person.setLastName(lastName.getText());
            this.person.setDateOfBirth(dateOfBirth.getValue());
            this.person.setId(Integer.parseInt(idNum.getText()));
            logger.info("UPDATING " + this.person.getFirstName() + " " + this.person.getLastName());
        } else {
            Person newPerson = new Person(firstName.getText(), lastName.getText(), dateOfBirth.getValue());
            newPerson.setId(Integer.parseInt(idNum.getText()));
            ViewSwitcher.getInstance().getPeople().add(newPerson);
            logger.info("CREATING " + newPerson.getFirstName() + " " + newPerson.getLastName());
        }
        ViewSwitcher.getInstance().switchView(ViewScreen.PEOPLELISTVIEW);
    }

    @FXML
    void cancel(ActionEvent event) throws IOException {
        ViewSwitcher.getInstance().switchView(ViewScreen.PEOPLELISTVIEW);
    }
}
