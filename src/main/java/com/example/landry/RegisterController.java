package com.example.landry;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class RegisterController {

    @FXML
    private TextField NameField;

    @FXML
    private TextField phnField;

    @FXML
    private TextField addField;

    @FXML
    private PasswordField passField;

    @FXML
    private Label statusLabel;

    @FXML
    public void Register(ActionEvent event) {
        String name = NameField.getText();
        String phone = phnField.getText();
        String address = addField.getText();
        String pass = passField.getText();

        if (name.isEmpty() || phone.isEmpty() || address.isEmpty() || pass.isEmpty()) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Please fill in all fields!");
            return;
        }

        //database er kaj pore korbo( user info insertion)


        statusLabel.setStyle("-fx-text-fill: green;");
        statusLabel.setText("Account Created! Please Login.");
    }

    @FXML
    public void switchToLogin(ActionEvent event) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Login");
        stage.show();
    }
}