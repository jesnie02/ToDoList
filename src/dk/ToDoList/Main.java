package dk.ToDoList;

import dk.ToDoList.gui.ToDoController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args){
        Application.launch();
    }
    public void start(Stage primaryStage) throws Exception

    {
        FXMLLoader todoLoader = new FXMLLoader(getClass().getResource("/ToDoWindow.fxml"));
        Parent root = todoLoader.load();

        // Set an event handler to handle the close request of the primary stage.
        primaryStage.setOnCloseRequest(event -> {
            ToDoController controller = todoLoader.getController();
            controller.saveData();
        });

        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("My ToDo App");
        primaryStage.show();

    }
}