package com.example.landry;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminController {

    @FXML private TableView<OrderModel> orderTable;
    @FXML private TableColumn<OrderModel, Integer> colId;
    @FXML private TableColumn<OrderModel, String> colCustomer;
    @FXML private TableColumn<OrderModel, String> colDetails; // New Column
    @FXML private TableColumn<OrderModel, String> colStatus;
    @FXML private TableColumn<OrderModel, Double> colTotal;

    private ObservableList<OrderModel> orderList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        colId.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        colCustomer.setCellValueFactory(new PropertyValueFactory<>("customerPhone"));
        colDetails.setCellValueFactory(new PropertyValueFactory<>("details"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("amount"));

        loadOrders();
    }

    private void loadOrders() {
        orderList.clear();
        String sql = "SELECT * FROM orders";

        try (Connection conn = database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                orderList.add(new OrderModel(
                        rs.getInt("order_id"),
                        rs.getString("customer_phone"),
                        rs.getString("details"),
                        rs.getString("status"),
                        rs.getDouble("amount")
                ));
            }
            orderTable.setItems(orderList);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML public void Refresh() { loadOrders(); }

    @FXML public void Washing() { updateStatus("Washing"); }
    @FXML public void Ready() { updateStatus("Ready"); }
    @FXML public void Delivered() { updateStatus("Delivered"); }

    private void updateStatus(String newStatus) {
        OrderModel selected = orderTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Select an order first.");
            return;
        }

        String sql = "UPDATE orders SET status = ? WHERE order_id = ?";
        try (Connection conn = database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newStatus);
            pstmt.setInt(2, selected.getOrderId());
            pstmt.executeUpdate();

            loadOrders(); // Refresh table
            showAlert("Success", "Order #" + selected.getOrderId() + " marked as " + newStatus);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void Delete(ActionEvent event) {
        OrderModel selected = orderTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert("Error", "Please select an order to delete.");
            return;
        }
        if (!"Delivered".equalsIgnoreCase(selected.getStatus())) {
            showAlert("Restriction", "You can only delete orders that are 'Delivered'.");
            return;
        }

        String sql = "DELETE FROM orders WHERE order_id = ?";

        try (Connection conn = database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, selected.getOrderId());
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                loadOrders();
                showAlert("Success", "Order #" + selected.getOrderId() + " has been deleted.");
            } else {
                showAlert("Error", "Could not delete the order.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", e.getMessage());
        }
    }

    @FXML
    public void Logout(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public static class OrderModel {
        private int orderId;
        private String customerPhone;
        private String details;
        private String status;
        private double amount;

        public OrderModel(int orderId, String customerPhone, String details, String status, double amount) {
            this.orderId = orderId;
            this.customerPhone = customerPhone;
            this.details = details;
            this.status = status;
            this.amount = amount;
        }

        public int getOrderId() { return orderId; }
        public String getCustomerPhone() { return customerPhone; }
        public String getDetails() { return details; }
        public String getStatus() { return status; }
        public double getAmount() { return amount; }
    }
}