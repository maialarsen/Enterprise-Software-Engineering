package mvc.controllers;

import gateways.PersonGateway;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import mvc.models.Person;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PeopleListController implements Initializable {
    private boolean editMode;
    private static PeopleListController instance = null;
    private static final Logger logger = LogManager.getLogger();

    public static void setInstance(PeopleListController instance) {
        PeopleListController.instance = instance;
    }

    @FXML
    private ListView<Person> peopleList;

    public static PeopleListController getInstance() {
        if (instance == null)
            instance = new PeopleListController();
        return instance;
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        editMode = true;
        for (int i = 0; i < ViewSwitcher.getInstance().getPeople().size(); i++) {
            peopleList.getItems().add(ViewSwitcher.getInstance().getPeople().get(i));
        }
    }

    @FXML
    void edit(ActionEvent event) throws IOException {
        setEditMode(true);
        ViewSwitcher.getInstance().switchView(ViewScreen.PEOPLEEDITVIEW);
    }

    @FXML
    void addPerson(ActionEvent event) throws IOException {
        setEditMode(false);
        ViewSwitcher.getInstance().switchView(ViewScreen.PEOPLEEDITVIEW);
    }

    @FXML
    void deleteSelected(ActionEvent event) throws IOException {
        if (peopleList.getItems().size() == 0) {
            logger.error("No people to delete");
        } else if (peopleList.getSelectionModel().getSelectedItem() == null) {
            logger.error("No person selected");
        }
        else {
            Person personToDelete = peopleList.getSelectionModel().getSelectedItem();
            PersonGateway.deletePerson(ViewSwitcher.getInstance().getSession().getSessionId(), personToDelete);
        }
    }

    public ListView<Person> getPeopleList() {
        return peopleList;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }
}
