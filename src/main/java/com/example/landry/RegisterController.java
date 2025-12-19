package com.example.landry;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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

        String insertSql = "INSERT INTO users(phone, name, password, address, role) VALUES(?, ?, ?, ?, ?)";

        try (Connection conn = database.connect();
             PreparedStatement pstmt = conn.prepareStatement(insertSql)) {

            pstmt.setString(1, phone);
            pstmt.setString(2, name);
            pstmt.setString(3, pass);
            pstmt.setString(4, address);
            pstmt.setString(5, "Customer");

            pstmt.executeUpdate();

            statusLabel.setStyle("-fx-text-fill: green;");
            statusLabel.setText("Account Created! Please Login.");

            NameField.clear();
            phnField.clear();
            addField.clear();
            passField.clear();

        } catch (SQLException e) {

            e.printStackTrace();
            statusLabel.setStyle("-fx-text-fill: red;");

            if (e.getMessage().contains("UNIQUE constraint failed")) {
                statusLabel.setText("Phone number already registered!");
            } else {
                statusLabel.setText("Database Error: " + e.getMessage());
            }
        }
    }

    @FXML
    public void switchToLogin(ActionEvent event) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        double currentWidth = stage.getWidth();
        double currentHeight = stage.getHeight();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setWidth(currentWidth);
        stage.setHeight(currentHeight);
    }
}