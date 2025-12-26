package com.example.landry;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class pickupController {


    private int countShirt = 0;
    private int countPant = 0;
    private int countSuit = 0;
    private int countBlanket = 0;

    private final int PRICE_SHIRT = 10;
    private final int PRICE_PANT = 15;
    private final int PRICE_SUIT = 50;
    private final int PRICE_BLANKET = 100;
    private final int IRON = 5;

    @FXML private Label lblShirt, lblPant, lblSuit, lblBlanket, lblTotal;
    @FXML private RadioButton radioWashDry, radioIron;

    private String currentUserId;


    public void setUserId(String userId) {
        this.currentUserId = userId;
    }

    @FXML public void increaseShirt() { countShirt++; updateUI(); }
    @FXML public void decreaseShirt() { if(countShirt > 0) countShirt--; updateUI(); }

    @FXML public void increasePant() { countPant++; updateUI(); }
    @FXML public void decreasePant() { if(countPant > 0) countPant--; updateUI(); }

    @FXML public void increaseSuit() { countSuit++; updateUI(); }
    @FXML public void decreaseSuit() { if(countSuit > 0) countSuit--; updateUI(); }

    @FXML public void increaseBlanket() { countBlanket++; updateUI(); }
    @FXML public void decreaseBlanket() { if(countBlanket > 0) countBlanket--; updateUI(); }

    @FXML
    public void updateUI() {
        lblShirt.setText(String.valueOf(countShirt));
        lblPant.setText(String.valueOf(countPant));
        lblSuit.setText(String.valueOf(countSuit));
        lblBlanket.setText(String.valueOf(countBlanket));

        double total = (countShirt * PRICE_SHIRT) + (countPant * PRICE_PANT) +
                (countSuit * PRICE_SUIT) + (countBlanket * PRICE_BLANKET);

        if (radioIron.isSelected()) {
            int totalItems = countShirt + countPant + countSuit + countBlanket;
            total += (totalItems * IRON);
        }

        lblTotal.setText(total + " Tk");
    }

    @FXML
    public void handleSubmit(ActionEvent event) {
        String amountText = lblTotal.getText().replace(" Tk", "").trim();
        double finalAmount = Double.parseDouble(amountText);

        if (finalAmount == 0) {
            showAlert("Error", "Please add at least one item.");
            return;
        }


        StringBuilder details = new StringBuilder();
        if (countShirt > 0) details.append(countShirt).append(" Shirts, ");
        if (countPant > 0) details.append(countPant).append(" Pants, ");
        if (countSuit > 0) details.append(countSuit).append(" Suits, ");
        if (countBlanket > 0) details.append(countBlanket).append(" Blankets, ");

        if (radioIron.isSelected()) details.append("[+Ironing]");
        else details.append("[Wash Only]");

        saveOrderToDB(details.toString(), finalAmount);

        handleBack(event);
    }

    private void saveOrderToDB(String details, double amount) {
        String sql = "INSERT INTO orders(customer_phone, details, pickup_date, status, amount) VALUES(?, ?, ?, ?, ?)";
        String date = LocalDate.now().toString();

        try (Connection conn = database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, currentUserId);
            pstmt.setString(2, details);
            pstmt.setString(3, date);
            pstmt.setString(4, "Pending");
            pstmt.setDouble(5, amount);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", e.getMessage());
        }
    }


    @FXML
    public void handleBack(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("client.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            ClientController controller = fxmlLoader.getController();
            controller.setUserId(currentUserId);

            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Client Dashboard");
            stage.setScene(scene);
            stage.show();

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