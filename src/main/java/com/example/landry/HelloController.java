package com.example.landry;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
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

                String role = rs.getString("role");

                if (role.equalsIgnoreCase("Admin")) {
                    msglbl.setStyle("-fx-text-fill: green;");
                    msglbl.setText("Admin Login Success!");
                    openDashboard("Admin.fxml", "Admin Dashboard", event,user);
                } else {
                    msglbl.setStyle("-fx-text-fill: blue;");
                    msglbl.setText("Customer Login Success!");
                    openDashboard("Client.fxml", "Client Dashboard", event,user);
                }

            } else {
                msglbl.setStyle("-fx-text-fill: red;");
                msglbl.setText("Invalid Phone or Password!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            msglbl.setStyle("-fx-text-fill: red;");
            msglbl.setText("Database Connection Error.");
        }
    }
    private void openDashboard(String fxmlFile, String title, ActionEvent event,String user) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = fxmlLoader.load();
            if(fxmlFile.equals("Client.fxml")){
                ClientController controller = fxmlLoader.getController();
                controller.setUserId(user);
            }
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            double currentWidth = stage.getWidth();
            double currentHeight = stage.getHeight();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.setWidth(currentWidth);
            stage.setHeight(currentHeight);
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
            Parent root=fxmlLoader.load();
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            double currentWidth = stage.getWidth();
            double currentHeight = stage.getHeight();

            Scene scene = new Scene(root);

            stage.setTitle("Create Account");
            stage.setScene(scene);
            stage.setWidth(currentWidth);
            stage.setHeight(currentHeight);

        } catch (IOException e) {
            e.printStackTrace();
            msglbl.setStyle("-fx-text-fill: red;");
            msglbl.setText("Error: Could not load Resister.fxml");
        }
    }
}