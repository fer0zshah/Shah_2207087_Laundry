package com.example.landry;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

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

        // some dummy data
        orderList.add(new OrderModel(101, "Rahim Ahmed", "Pending", 150.0));
        orderList.add(new OrderModel(102, "Karim Ullah", "Washing", 320.0));
        orderList.add(new OrderModel(103, "Sultana Begum", "Ready", 80.0));

        orderTable.setItems(orderList);
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
        if (selectedOrder != null) {
            selectedOrder.setStatus(newStatus);
            orderTable.refresh(); // Refresh the UI to show changes
            System.out.println("Order " + selectedOrder.getOrderId() + " changed to " + newStatus);
        } else {
            System.out.println("Please select an order first!");
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