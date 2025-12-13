package com.example.landry;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label statusText;

    @FXML
    private ProgressBar statusProgress;

    @FXML
    public void initialize() {

        String currentStatus = "Washing";

        if(currentStatus.equals("Pending")) {
            statusText.setText("Order Pending");
            statusText.setStyle("-fx-text-fill: orange;");
            statusProgress.setProgress(0.2);
        } else if (currentStatus.equals("Washing")) {
            statusText.setText("Washing in Progress");
            statusText.setStyle("-fx-text-fill: #2a5082;");
            statusProgress.setProgress(0.5);
        } else if (currentStatus.equals("Ready")) {
            statusText.setText("Ready for Pickup!");
            statusText.setStyle("-fx-text-fill: green;");
            statusProgress.setProgress(1.0);
        }
    }

    @FXML
    public void handleRequestPickup() {
        System.out.println("Pickup Requested!");
        //I'll work later after adding database(open a popup)
    }
    public void ShowPrice() {

        //I'll work later after adding database(open a popup)
    }

    public void ShowHistory() {

        //I'll work later after adding database(open a pop up)
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}