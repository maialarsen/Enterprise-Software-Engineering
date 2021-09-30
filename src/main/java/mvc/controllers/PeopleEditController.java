package mvc.controllers;

import gateways.PersonGateway;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import org.json.JSONArray;
import org.json.JSONObject;

public class PeopleEditController implements Initializable {
    private Person person;
    private boolean editMode;
    private static final Logger logger = LogManager.getLogger();

    JSONObject updates = new JSONObject();

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
            age.setText(Integer.toString(person.getAge()));

            firstName.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                    updates.put("first_name", firstName.getText());
                }
            });

            lastName.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                    updates.put("last_name", lastName.getText());
                }
            });

            dateOfBirth.valueProperty().addListener(new ChangeListener<LocalDate>() {
                @Override
                public void changed(ObservableValue<? extends LocalDate> observableValue, LocalDate localDate, LocalDate t1) {
                    updates.put("birth_date", dateOfBirth.getValue().toString());
                }
            });

        } else {
            firstName.setText("");
            lastName.setText("");
            dateOfBirth.setValue(LocalDate.now());
            idNum.setText("");
            age.setText("");
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
    private TextField age;

    @FXML
    private Button saveBtn;

    @FXML
    void save(ActionEvent event) throws IOException {
        if (firstName.getText().isBlank() || lastName.getText().isBlank()) {
            logger.error("One or more fields have been left blank");
            return;
        }

        if (dateOfBirth.getValue().isAfter(LocalDate.now())) {
            logger.error("Birth date needs to be before current date");
            return;
        }

        if (editMode) {
            updates.put("id", idNum.getText());
            PersonGateway.updatePerson(ViewSwitcher.getInstance().getSession().getSessionId(), updates);
            logger.info("UPDATING " + this.person.getFirstName() + " " + this.person.getLastName());
        } else {
            Person newPerson = new Person(firstName.getText(), lastName.getText(), dateOfBirth.getValue());
            JSONObject newPersonInfo = new JSONObject();

            newPersonInfo.put("firstName", firstName.getText());
            newPersonInfo.put("lastName", lastName.getText());
            newPersonInfo.put("dateOfBirth", dateOfBirth.getValue());

            PersonGateway.insertPerson(ViewSwitcher.getInstance().getSession().getSessionId(), newPersonInfo);
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
