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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

        if (user.isEmpty() || pass.isEmpty()) {
            msglbl.setStyle("-fx-text-fill: red;");
            msglbl.setText("Please enter Phone and Password.");
            return;
        }
        String query = "SELECT role FROM users WHERE phone = ? AND password = ?";

        try (Connection conn = database.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, user);
            pstmt.setString(2, pass);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // User Found! Get their role
                String role = rs.getString("role");

                if (role.equalsIgnoreCase("Admin")) {
                    msglbl.setStyle("-fx-text-fill: green;");
                    msglbl.setText("Admin Login Success!");
                    openDashboard("Admin.fxml", "Admin Dashboard", event);
                } else {
                    msglbl.setStyle("-fx-text-fill: blue;");
                    msglbl.setText("Customer Login Success!");
                    openDashboard("Client.fxml", "Client Dashboard", event);
                }

            } else {
                // User NOT Found
                msglbl.setStyle("-fx-text-fill: red;");
                msglbl.setText("Invalid Phone or Password!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            msglbl.setStyle("-fx-text-fill: red;");
            msglbl.setText("Database Connection Error.");
        }
    }
    private void openDashboard(String fxmlFile, String title, ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            msglbl.setText("Error loading " + fxmlFile);
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
            msglbl.setText("Error: Could not load Resister.fxml");
        }
    }
}