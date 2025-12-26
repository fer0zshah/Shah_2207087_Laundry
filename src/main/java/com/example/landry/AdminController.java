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
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminController {

    @FXML private TableView<OrderModel> orderTable;
    @FXML private TableColumn<OrderModel, String> colId;
    @FXML private TableColumn<OrderModel, String> colCustomer;
    @FXML private TableColumn<OrderModel, String> colDetails;
    @FXML private TableColumn<OrderModel, String> colStatus;
    @FXML private TableColumn<OrderModel, Double> colTotal;

    @FXML private HBox boxStatusButtons;
    @FXML private HBox boxRequestButtons;

    private ObservableList<OrderModel> orderList = FXCollections.observableArrayList();
    private boolean isRequestMode = false;

    @FXML
    public void initialize() {
        colId.setText("Phone Number");
        colId.setCellValueFactory(new PropertyValueFactory<>("customerPhone"));
        colCustomer.setText("Client Name");
        colCustomer.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        colDetails.setCellValueFactory(new PropertyValueFactory<>("details"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("amount"));

        Active();
    }

    @FXML
    public void Active() {
        isRequestMode = false;
        boxStatusButtons.setVisible(true);
        boxStatusButtons.setManaged(true);
        boxRequestButtons.setVisible(false);
        boxRequestButtons.setManaged(false);
        loadOrders();
    }

    @FXML
    public void Pickup() {
        isRequestMode = true;
        boxStatusButtons.setVisible(false);
        boxStatusButtons.setManaged(false);
        boxRequestButtons.setVisible(true);
        boxRequestButtons.setManaged(true);
        loadOrders();
    }
    @FXML
    public void RevenueReport(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Reveneu.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Revenue Report");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML public void Refresh() { loadOrders(); }

    private void loadOrders() {
        orderList.clear();
        String sql;
        if (isRequestMode) {
            sql = "SELECT o.*, u.name FROM orders o " +
                    "JOIN users u ON o.customer_phone = u.phone " +
                    "WHERE o.status = 'Pending' ORDER BY o.order_id ASC";
        } else {
            sql = "SELECT o.*, u.name FROM orders o " +
                    "JOIN users u ON o.customer_phone = u.phone " +
                    "WHERE o.status != 'Pending' ORDER BY o.order_id DESC";
        }

        try (Connection conn = database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                orderList.add(new OrderModel(
                        rs.getInt("order_id"),
                        rs.getString("customer_phone"),
                        rs.getString("name"),
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

    @FXML
    public void Accept() {
        if (!isRequestMode) return;
        updateStatus("Accepted");
    }

    @FXML
    public void Reject() {
        if (!isRequestMode) return;
        OrderModel selected = orderTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Select a request to reject.");
            return;
        }

        String sql = "DELETE FROM orders WHERE order_id = ?";
        try (Connection conn = database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, selected.getOrderId());
            pstmt.executeUpdate();
            loadOrders();
            showAlert("Success", "Order Rejected.");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @FXML public void Washing() { updateStatus("Washing"); }
    @FXML public void Ready() { updateStatus("Ready"); }
    @FXML public void Delivered() { updateStatus("Delivered"); }

    private void updateStatus(String newStatus) {
        OrderModel selected = orderTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        String sql;
        if (newStatus.equals("Delivered")) {
            sql = "UPDATE orders SET status = ?, pickup_date = ? WHERE order_id = ?";
        } else {
            sql = "UPDATE orders SET status = ? WHERE order_id = ?";
        }

        try (Connection conn = database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newStatus);

            if (newStatus.equals("Delivered")) {
                pstmt.setString(2, java.time.LocalDate.now().toString());
                pstmt.setInt(3, selected.getOrderId());
            } else {
                pstmt.setInt(2, selected.getOrderId());
            }

            pstmt.executeUpdate();
            loadOrders();
            showAlert("Success", "Status updated to " + newStatus);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void Delete(ActionEvent event) {
        OrderModel selected = orderTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select an order.");
            return;
        }
        if (!"Delivered".equalsIgnoreCase(selected.getStatus())) {
            showAlert("Restriction", "Only 'Delivered' orders can be deleted.");
            return;
        }

        String sql = "DELETE FROM orders WHERE order_id = ?";
        try (Connection conn = database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, selected.getOrderId());
            pstmt.executeUpdate();
            loadOrders();
            showAlert("Success", "Order Deleted.");
        } catch (SQLException e) { e.printStackTrace(); }
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
        private String customerName;
        private String details;
        private String status;
        private double amount;

        public OrderModel(int orderId, String customerPhone, String customerName, String details, String status, double amount) {
            this.orderId = orderId;
            this.customerPhone = customerPhone;
            this.customerName = customerName;
            this.details = details;
            this.status = status;
            this.amount = amount;
        }

        public int getOrderId() { return orderId; }
        public String getCustomerPhone() { return customerPhone; }
        public String getCustomerName() { return customerName; }
        public String getDetails() { return details; }
        public String getStatus() { return status; }
        public double getAmount() { return amount; }
    }
}