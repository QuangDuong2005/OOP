package controller;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class SaleController {

    @FXML
    private StackPane contentArea;

    @FXML
    void showCustomers(ActionEvent event) {
        System.out.println("Click: Quản lý Khách hàng");
        // loadView("CustomerView.fxml");
    }

    @FXML
    void showBooking(ActionEvent event) {
        System.out.println("Click: Đặt Tour");
    }

    @FXML
    void showOrders(ActionEvent event) {
        System.out.println("Click: Danh sách Đơn hàng");
    }

    @FXML
    void handleLogout(ActionEvent event) throws IOException {
        // Quay về màn hình đăng nhập
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Login.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.centerOnScreen();
    }
}