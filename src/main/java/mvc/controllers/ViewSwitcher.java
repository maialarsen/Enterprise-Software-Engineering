package mvc.controllers;

import gateways.PersonGateway;
import gateways.Session;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import mvc.models.Person;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class ViewSwitcher {
    private static ViewSwitcher instance = null;
    private Stage stage;
    private BorderPane rootPane;
    private static final Logger logger = LogManager.getLogger();

    private Session session;
    private PersonGateway personGateway;
    private ArrayList<Person> people;

    private ViewSwitcher() {
        personGateway = new PersonGateway();
    }

    public static ViewSwitcher getInstance() {
        if (instance == null)
            instance = new ViewSwitcher();
        return instance;
    }

    public void initStage() throws IOException {
        Scene scene = new Scene(rootPane, 600, 400);
        stage.setTitle("Assignment 2");
        stage.setScene(scene);
        stage.show();
        switchView(ViewScreen.LOGINVIEW);
    }

    public void switchView(ViewScreen view) throws IOException {
        URL fxmlFile;
        FXMLLoader loader;
        PeopleListController peopleListController = new PeopleListController();
        switch (view) {
            case LOGINVIEW:
                fxmlFile = this.getClass().getResource("/loginView.fxml");
                loader = new FXMLLoader(fxmlFile);
                loader.setController(new LoginController());
                rootPane.setCenter(loader.load());
                break;
            case PEOPLELISTVIEW:
                people = PersonGateway.fetchPeople(session.getSessionId());

                fxmlFile = this.getClass().getResource("/peopleListView.fxml");
                loader = new FXMLLoader(fxmlFile);
                PeopleListController.setInstance(peopleListController);
                loader.setController(peopleListController);
                rootPane.setCenter(loader.load());
                break;
            case PEOPLEEDITVIEW:
                Person personToEdit;
                fxmlFile = this.getClass().getResource("/peopleEditView.fxml");
                loader = new FXMLLoader(fxmlFile);

                if (PeopleListController.getInstance().isEditMode() && PeopleListController.getInstance().getPeopleList().getSelectionModel().getSelectedItem() != null) {
                    personToEdit = PeopleListController.getInstance().getPeopleList().getSelectionModel().getSelectedItem();
                    logger.info("READING " + personToEdit);
                    loader.setController(new PeopleEditController(personToEdit));
                }
                else if (!(PeopleListController.getInstance().isEditMode())) {
                    loader.setController(new PeopleEditController());
                }
                else {
                    logger.error("No person selected");
                    break;
                }
                rootPane.setCenter(loader.load());
                break;
        }
    }
    public Person findById(int id) {
        for (int i = 0; i < people.size(); i++) {
            if (people.get(i).getId() == id)
                return people.get(i);
        }
        return null;
    }

    public static void setInstance(ViewSwitcher instance) {
        ViewSwitcher.instance = instance;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public BorderPane getRootPane() {
        return rootPane;
    }

    public void setRootPane(BorderPane rootPane) {
        this.rootPane = rootPane;
    }

    public ArrayList<Person> getPeople() {
        return people;
    }

    public void setPeople(ArrayList<Person> people) {
        this.people = people;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public PersonGateway getPersonGateway() {
        return personGateway;
    }

    public void setPersonGateway(PersonGateway personGateway) {
        this.personGateway = personGateway;
    }
}