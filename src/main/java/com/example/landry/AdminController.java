package com.example.landry;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.scene.control.Alert;

import java.io.IOException;

public class AdminController {

    @FXML
    private TableView<OrderModel> orderTable;
    @FXML
    private TableColumn<OrderModel, Integer> colId;
    @FXML
    private TableColumn<OrderModel, String> colCustomer;
    @FXML
    private TableColumn<OrderModel, String> colStatus;
    @FXML
    private TableColumn<OrderModel, Double> colTotal;

    private ObservableList<OrderModel> orderList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        colCustomer.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));

//        orderList.add(new OrderModel(101, "Rahim Ahmed", "Pending", 150.0));
//        orderList.add(new OrderModel(102, "Karim Ullah", "Washing", 320.0));
//        orderList.add(new OrderModel(103, "Sultana Begum", "Ready", 80.0));

        loadOrders();
    }

    private void loadOrders() {
        orderList.clear();
        String sql = "SELECT * FROM orders";

        try (Connection conn = database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {

                int id = rs.getInt("order_id");
                String phone = rs.getString("customer_phone");
                String status = rs.getString("status");
                double amount = rs.getDouble("amount");
                orderList.add(new OrderModel(id, phone, status, amount));
            }

            orderTable.setItems(orderList);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 2. Status Update Logic
    @FXML
    public void Washing() { updateStatus("Washing"); }
    @FXML
    public void Ready() { updateStatus("Ready"); }
    @FXML
    public void Delivered() { updateStatus("Delivered"); }

    private void updateStatus(String newStatus) {
        OrderModel selectedOrder = orderTable.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            showAlert("Error", "Please select an order from the table first!");
            return;
        }

        String sql = "UPDATE orders SET status = ? WHERE order_id = ?";

        try (Connection conn = database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newStatus);
            pstmt.setInt(2, selectedOrder.getOrderId());
            pstmt.executeUpdate();

            showAlert("Success", "Order #" + selectedOrder.getOrderId() + " updated to: " + newStatus);

            loadOrders();

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


    @FXML
    public void Logout(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        double currentWidth = stage.getWidth();
        double currentHeight = stage.getHeight();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setWidth(currentWidth);
        stage.setHeight(currentHeight);

    }


    public static class OrderModel {
        private int orderId;
        private String customerName;
        private String status;
        private double totalAmount;

        public OrderModel(int orderId, String customerName, String status, double totalAmount) {
            this.orderId = orderId;
            this.customerName = customerName;
            this.status = status;
            this.totalAmount = totalAmount;
        }

        public int getOrderId() { return orderId; }
        public String getCustomerName() { return customerName; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public double getTotalAmount() { return totalAmount; }
    }
}