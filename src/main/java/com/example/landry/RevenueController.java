package com.example.landry;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class RevenueController {

    @FXML private Label lblToday;
    @FXML private Label lblMonth;
    @FXML private Label lblTotal;
    @FXML private Label lblPending; // Bonus: Show potential income

    @FXML
    public void initialize() {
        calculateRevenue();
    }

    private void calculateRevenue() {
        // 1. Get Today's Date (e.g., "2025-12-25")
        String today = LocalDate.now().toString();
        // 2. Get Current Month prefix (e.g., "2025-12")
        String currentMonth = today.substring(0, 7);

        lblToday.setText(getRevenue("WHERE status = 'Delivered' AND pickup_date = '" + today + "'") + " Tk");
        lblMonth.setText(getRevenue("WHERE status = 'Delivered' AND pickup_date LIKE '" + currentMonth + "%'") + " Tk");
        lblTotal.setText(getRevenue("WHERE status = 'Delivered'") + " Tk");

        // Bonus: "Pending Revenue" (Money you will get soon)
        lblPending.setText(getRevenue("WHERE status != 'Delivered'") + " Tk");
    }

    // A helper method to run the SUM query easily
    private double getRevenue(String condition) {
        double total = 0;
        String sql = "SELECT SUM(amount) FROM orders " + condition;

        try (Connection conn = database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                total = rs.getDouble(1); // Get the first result (the SUM)
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    // --- NAVIGATION BACK TO DASHBOARD ---
    @FXML
    public void Back(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Admin.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}