package com.example.landry;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClientController {

    @FXML private Label welcomeLabel;
    @FXML private Label statusText;
    @FXML private ProgressBar statusProgress;

    private String currentUserId;

    public void setUserId(String userId) {
        this.currentUserId = userId;

        String userName = "Client";
        String query = "SELECT name FROM users WHERE phone = ?";
        try (Connection conn = database.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                userName = rs.getString("name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (welcomeLabel != null) {
            welcomeLabel.setText("Welcome, " + userName + "!");
        }
        checkOrderStatus();
    }



    @FXML
    public void initialize() {
//        statusText.setText("Ready for Orders");
//        statusText.setStyle("-fx-text-fill: #2a5082;");
//        statusProgress.setProgress(0.5);
    }
    private void checkOrderStatus() {
        String sql = "SELECT status FROM orders WHERE customer_phone = ? ORDER BY order_id DESC LIMIT 1";

        try (Connection conn = database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, currentUserId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String status = rs.getString("status");
                updateProgressUI(status);
            } else {
                statusText.setText("Ready for Orders");
                statusProgress.setProgress(0.0);
                statusProgress.setStyle("-fx-accent: #b2bec3;");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void updateProgressUI(String status) {
        statusText.setText("Current Status: " + status);
        statusText.setStyle("-fx-text-fill: #2a5082; -fx-font-weight: bold;");

        switch (status) {
            case "Pending":
                statusProgress.setProgress(0.1);
                statusProgress.setStyle("-fx-accent: #f1c40f;");
                break;

            case "Accepted":
                statusProgress.setProgress(0.25);
                statusProgress.setStyle("-fx-accent: #112c26;");
                break;

            case "Washing":
                statusProgress.setProgress(0.50);
                statusProgress.setStyle("-fx-accent: #247e6b;");
                break;

            case "Ready":
                statusProgress.setProgress(0.75);
                statusProgress.setStyle("-fx-accent: #46833f;");
                break;

            case "Delivered":
                statusProgress.setProgress(1.0);
                statusProgress.setStyle("-fx-accent: #1efd04;");
                break;

            case "Rejected":
                statusProgress.setProgress(1.0);
                statusProgress.setStyle("-fx-accent: #d63031;");
                statusText.setText("Order Rejected");
                break;

            default:
                statusProgress.setProgress(0.0);
                statusProgress.setStyle("-fx-accent: #b2bec3;"); // Grey
                break;
        }
    }
    @FXML
    public void Pickup(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("pickup.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            pickupController controller = fxmlLoader.getController();
            controller.setUserId(currentUserId);

            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("New Order Request");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load pickup-request.fxml");
        }
    }

    @FXML
    public void ShowHistory() {
        StringBuilder historyText = new StringBuilder();
        String sql = "SELECT * FROM orders WHERE customer_phone = ?";

        try (Connection conn = database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, currentUserId);
            ResultSet rs = pstmt.executeQuery();

            boolean hasOrders = false;
            while (rs.next()) {
                hasOrders = true;
                int id = rs.getInt("order_id");
                String date = rs.getString("pickup_date");
                String details = rs.getString("details");
                String status = rs.getString("status");
                double amount = rs.getDouble("amount");

                historyText.append("Order #").append(id)
                        .append(" | ").append(date)
                        .append("\nItems: ").append(details)
                        .append("\nStatus: ").append(status)
                        .append(" | Bill: ").append(amount).append(" Tk")
                        .append("\n-----------------------------------\n");
            }

            if (!hasOrders) {
                historyText.append("No previous orders found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            historyText.append("Error loading history.");
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Order History");
        alert.setHeaderText("My Past Orders");

        TextArea area = new TextArea(historyText.toString());
        area.setEditable(false);
        area.setWrapText(true);
        area.setPrefHeight(250);
        area.setPrefWidth(350);

        alert.getDialogPane().setContent(area);
        alert.showAndWait();
    }

    @FXML
    public void ShowPrice() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Price List");
        alert.setHeaderText("Standard Rates");

        String prices = "Shirt:       10 Tk\n" +
                "Pant:        15 Tk\n" +
                "Suit:        50 Tk\n" +
                "Blanket:     100 Tk\n" +
                "Ironing:     +5 Tk/item";

        alert.setContentText(prices);
        alert.showAndWait();
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            double currentWidth = stage.getWidth();
            double currentHeight = stage.getHeight();
            Scene scene = new Scene(root);
            stage.setTitle("Login");
            stage.setScene(scene);
            stage.setWidth(currentWidth);
            stage.setHeight(currentHeight);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}