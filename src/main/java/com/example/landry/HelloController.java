package com.example.landry;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

public class HelloController {

    @FXML
    private TextField userField;

    @FXML
    private PasswordField passField;

    @FXML
    private Label msglbl;

    @FXML
    private Button loginBtn;

    @FXML
    public void Login(ActionEvent event) {
        String user = userField.getText();
        String pass = passField.getText();

        if (user.equals("admin") && pass.equals("1234")) {
            msglbl.setStyle("-fx-text-fill: green;");
            msglbl.setText("Admin Login Success!");

            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Admin.fxml"));
                Scene scene = new Scene(fxmlLoader.load());
                Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
                stage.setTitle("Admin Dashboard");
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }


        } else if (user.equals("client") && pass.equals("1234")) {
            msglbl.setStyle("-fx-text-fill: blue;");
            msglbl.setText("Customer Login Success!");


            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Client.fxml"));
                Scene scene = new Scene(fxmlLoader.load());
                Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
                stage.setTitle("Client Dashboard");
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }


        } else {
            msglbl.setStyle("-fx-text-fill: red;");
            msglbl.setText("Invalid Username or Password!");
        }
    }

    @FXML
    public void CreatAcc(ActionEvent event) {
        try {
            System.out.println("Navigating to Registration...");
            msglbl.setStyle("-fx-text-fill: black;");
            msglbl.setText("Loading Registration Screen...");


            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Register.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();

            stage.setTitle("Create Account");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            msglbl.setStyle("-fx-text-fill: red;");
            msglbl.setText("Error: Could not load Register.fxml");
        }
    }
}