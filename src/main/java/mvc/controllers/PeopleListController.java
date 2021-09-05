package mvc.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
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

    public static PeopleListController getInstance() {
        if (instance == null)
            instance = new PeopleListController();
        return instance;
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        editMode = true;
        for (int i = 0; i < ViewSwitcher.getInstance().getPeople().size(); i++) {
            peopleList.getItems().add(ViewSwitcher.getInstance().getPeople().get(i).getFirstName() + " " + ViewSwitcher.getInstance().getPeople().get(i).getLastName());
        }
    }
    @FXML
    private Button addBtn;

    @FXML
    private Button editBtn;

    @FXML
    private ListView<String> peopleList;

    @FXML
    private Button deleteBtn;

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
    void deleteSelected(ActionEvent event) {
        if (peopleList.getItems().size() == 0) {
            logger.error("No people to delete");
        } else if (peopleList.getSelectionModel().getSelectedItem() == null) {
            logger.error("No person selected");
        }
        else {
            String firstName = peopleList.getSelectionModel().getSelectedItem().split(" ")[0];
            String lastName = peopleList.getSelectionModel().getSelectedItem().split(" ")[1];

            Person personToDelete = ViewSwitcher.getInstance().getPerson(firstName, lastName);
            peopleList.getItems().remove(firstName + " " + lastName);
            ViewSwitcher.getInstance().getPeople().remove(personToDelete);
            logger.info("DELETING " + firstName + " " + lastName);
        }
    }

    public ListView<String> getPeopleList() {
        return peopleList;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }
}
