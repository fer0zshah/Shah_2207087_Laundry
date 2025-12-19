package com.example.landry;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

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
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Request New Pickup");
        dialog.setHeaderText("Enter Order Details");

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);


        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField detailsField = new TextField();
        detailsField.setPromptText("e.g., 5 Shirts, 2 Pants");

        TextField dateField = new TextField();
        dateField.setPromptText("e.g., Tomorrow 10 AM");

        grid.add(new Label("Clothes:"), 0, 0);
        grid.add(detailsField, 1, 0);
        grid.add(new Label("Pickup Time:"), 0, 1);
        grid.add(dateField, 1, 1);

        dialog.getDialogPane().setContent(grid);


        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String details = detailsField.getText();
            String date = dateField.getText();

            if(details.isEmpty() || date.isEmpty()) {
                showAlert("Error", "Please fill in all fields.");
            } else {
                saveOrderToDB(details, date);
            }
        }

    }
    private String currentUserId = "01711";
    public void setUserId(String userId) {
        this.currentUserId = userId;
    }
    private void saveOrderToDB(String details, String date) {
        String sql = "INSERT INTO orders(customer_phone, details, pickup_date, status, amount) VALUES(?, ?, ?, ?, ?)";

        try (Connection conn = database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, currentUserId);
            pstmt.setString(2, details);
            pstmt.setString(3, date);
            pstmt.setString(4, "Pending");
            pstmt.setDouble(5, 0.0);

            pstmt.executeUpdate();

            showAlert("Success", "Pickup Request Sent!");

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", e.getMessage());
        }
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void ShowPrice() {

        //I'll work later after adding database(open a popup)
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(" Price List");
        alert.setHeaderText("Service Rates");

        String prices = "Washing (Per Piece):   10 Tk\n" +
                "Ironing (Per Piece):   5 Tk\n" +
                "Dry Clean (Suit):   50 Tk\n" +
                "Dry Clean (Blanket): 100 Tk\n" +
                "Delivery Charge:    5Tk";

        alert.setContentText(prices);
        alert.showAndWait();
    }

    public void ShowHistory() {

        //I'll work later after adding database(open a pop up)
        StringBuilder historyText = new StringBuilder();

        String sql = "SELECT * FROM orders WHERE customer_phone = ?";

        try (Connection conn = database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Use the ID of the logged-in user
            pstmt.setString(1, currentUserId);

            ResultSet rs = pstmt.executeQuery();

            // 2. Loop through all results
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

        // 3. Show it in a Popup
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("My Order History");
        alert.setHeaderText("Past Transactions");

        // We use a TextArea so users can scroll if the list is long
        TextArea area = new TextArea(historyText.toString());
        area.setEditable(false);
        area.setWrapText(true);
        area.setPrefHeight(200);
        area.setPrefWidth(300);

        alert.getDialogPane().setContent(area);
        alert.showAndWait();
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            double currentHeight = stage.getHeight();
            double currentWidth = stage.getWidth();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setWidth(currentWidth);
            stage.setHeight(currentHeight);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}