package javafx;

import javafx.application.Application;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import mvc.controllers.ViewSwitcher;

public class AppMain extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage) throws Exception {
        BorderPane rootPane = new BorderPane();
        ViewSwitcher.getInstance().setStage(stage);
        ViewSwitcher.getInstance().setRootPane(rootPane);
        ViewSwitcher.getInstance().initStage();
    }
}
