package dk.ToDoList.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;
import java.util.Properties;

public class ToDoController {

   @FXML
    private ListView<String> lstDone;

    @FXML
    private ListView<String> lstProgress;

    @FXML
    private ListView<String> lstToDo;

    @FXML
   //ObservableList<String> nameOfUsers = FXCollections.observableArrayList();
    private ObservableList<String> listDone = FXCollections.observableArrayList();
    private ObservableList<String> listProgress = FXCollections.observableArrayList();
    private ObservableList<String> listToDo = FXCollections.observableArrayList();



    @FXML
    void btnCreate(ActionEvent event) throws IOException {
        openDialog("", lstToDo, false);
    }

    @FXML
    public void initialize() {
        lstDone.setItems(listDone);
        lstProgress.setItems(listProgress);
        lstToDo.setItems(listToDo);

        //ensures that only one item can be selected at a time across the lists.
        lstToDo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                lstProgress.getSelectionModel().clearSelection();
                lstDone.getSelectionModel().clearSelection();
            }
        });

        lstProgress.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                lstToDo.getSelectionModel().clearSelection();
                lstDone.getSelectionModel().clearSelection();
            }
        });

        lstDone.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                lstToDo.getSelectionModel().clearSelection();
                lstProgress.getSelectionModel().clearSelection();
            }
        });

        // Load data from the properties file
        try {

            Properties properties = new Properties();

            // Open an input stream to read the properties file "savefile.txt"
            InputStream input = new FileInputStream("src/savefile.txt");
            properties.load(input);
            // Retrieve values
            String doneProperty = properties.getProperty("listDone");
            String progressProperty = properties.getProperty("listProgress");
            String toDoProperty = properties.getProperty("listToDo");

            // If  is not null, split its value by commas and add to listDone
            if (doneProperty != null) {
                listDone.addAll(Arrays.asList(doneProperty.split(",")));
            }

            // If is not null, split its value by commas and add to listProgress
            if (progressProperty != null) {
                listProgress.addAll(Arrays.asList(progressProperty.split(",")));
            }

            // If is not null, split its value by commas and add to listToDo
            if (toDoProperty != null) {
                listToDo.addAll(Arrays.asList(toDoProperty.split(",")));
            }


            input.close();

        } catch (IOException e) {
            // If an IOException occurs (e.g., file not found), print the stack trace
            e.printStackTrace();
        }

        // Remove this section from your initialize method
        if (listDone.isEmpty() && listProgress.isEmpty() && listToDo.isEmpty()) {
            System.out.println("Resetting savefile.txt...");
            try {
                Properties properties = new Properties();
                OutputStream output = new FileOutputStream("src/savefile.txt");

                if (!listDone.isEmpty()) {
                    properties.setProperty("listDone", "");
                }
                if (!listProgress.isEmpty()) {
                    properties.setProperty("listProgress", "");
                }
                if (!listToDo.isEmpty()) {
                    properties.setProperty("listToDo", "");
                }

                properties.store(output, null);

                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Tilføj en event handler for dobbeltklik
        lstToDo.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                handleDoubleClick(lstToDo);
            }
        });

        lstProgress.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                handleDoubleClick(lstProgress);
            }
        });

        lstDone.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                handleDoubleClick(lstDone);
            }
        });


    }



        @FXML
    void btnErase(ActionEvent event) {
        String selectedItemToDo = lstToDo.getSelectionModel().getSelectedItem();
        String selectedItemProgress = lstProgress.getSelectionModel().getSelectedItem();
        String selectedItemDone = lstDone.getSelectionModel().getSelectedItem();

        if (selectedItemToDo != null) {
            listToDo.remove(selectedItemToDo);
        } else if (selectedItemProgress != null) {
            listProgress.remove(selectedItemProgress);
        } else if (selectedItemDone != null) {
            listDone.remove(selectedItemDone);
        }

    }

    @FXML
    public void addToDoItem(String itemName) {
        listToDo.add(itemName);
        lstToDo.setItems(listToDo);
    }

    @FXML
    void btnMoveProgress(ActionEvent event) {
        moveItem(lstToDo, listToDo, lstProgress, listProgress);
    }

    @FXML
    void btnMoveDone(ActionEvent event) {
        moveItem(lstProgress, listProgress, lstDone, listDone);
    }

    @FXML
    void btnMoveBackProgress(ActionEvent event) {
        moveItem(lstDone, listDone, lstProgress, listProgress);
    }

    @FXML
    void btnBackToDo(ActionEvent event) {
        moveItem(lstProgress, listProgress, lstToDo, listToDo);
    }

    // Moves the selected item from the source list to the target list along with updating their respective ObservableLists and clearing selection.
    private void moveItem(ListView<String> sourceList, ObservableList<String> sourceData,
                          ListView<String> targetList, ObservableList<String> targetData) {
        String selectedItem = sourceList.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            sourceData.remove(selectedItem);
            sourceList.setItems(sourceData);

            targetData.add(selectedItem);
            targetList.setItems(targetData);

            sourceList.getSelectionModel().clearSelection(); // removes marking
        }
    }

    public void saveData() {
        try {
            Properties properties = new Properties();
            OutputStream output = new FileOutputStream("src/savefile.txt");

            // Check if lists have items before saving
            if (!listDone.isEmpty()) {
                properties.setProperty("listDone", String.join(",", listDone));
            }
            if (!listProgress.isEmpty()) {
                properties.setProperty("listProgress", String.join(",", listProgress));
            }
            if (!listToDo.isEmpty()) {
                properties.setProperty("listToDo", String.join(",", listToDo));
            }

            // Save the properties to the file
            properties.store(output, null);

            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleDoubleClick(ListView<String> listView) {
        String selectedItem = listView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            openDialog(selectedItem, listView, true);
        }
    }

    private void openDialog(String initialValue, ListView<String> listView, boolean isEditing) {
        TextInputDialog dialog = new TextInputDialog(initialValue);
        dialog.setTitle(isEditing ? "Change ToDo" : "New ToDo");
        dialog.setHeaderText(null);
        dialog.setContentText("New ToDo:");
        dialog.getDialogPane().setGraphic(null);

        // Tilføj en ekstra CSS-klasse til dialogboksen
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        dialogPane.getStyleClass().add("custom-dialog");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newText -> {
            if (!newText.trim().isEmpty()) {
                if (isEditing) {
                    // Opdate element in my list
                    listView.getItems().set(listView.getSelectionModel().getSelectedIndex(), newText);
                } else {
                    // Add the new task
                    addToDoItem(newText);
                }

            }
            else {
                // Display alert and prevent closing
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Input Error");
                alert.setHeaderText(null);
                alert.setContentText("Please enter a ToDo before clicking OK.");

                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        openDialog(initialValue, listView, isEditing); // Open dialog again
                    }
                });
            }
        });
    }

}
