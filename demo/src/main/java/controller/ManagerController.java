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

public class ManagerController {

    @FXML
    private StackPane contentArea; // Mapping với fx:id="contentArea" bên FXML

    // --- CÁC HÀM XỬ LÝ MENU ---

    @FXML
    void showAccounts(ActionEvent event) {
        try {
            // Load file AccountView.fxml
            Parent view = FXMLLoader.load(getClass().getResource("/view/AccountView.fxml"));
            
            // Xóa nội dung cũ ở giữa và thêm nội dung mới vào
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void showEmployees(ActionEvent event) {
        System.out.println("Click: Quản lý Nhân viên");
    }

    @FXML
    void showTours(ActionEvent event) {
        System.out.println("Click: Quản lý Tour");
    }

    @FXML
    void showNotifications(ActionEvent event) {
        System.out.println("Click: Duyệt yêu cầu");
    }

    @FXML
    void showStatistics(ActionEvent event) {
        System.out.println("Click: Thống kê");
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
    
    // Hàm hỗ trợ load giao diện con vào vùng giữa (StackPane)
    /*
    private void loadView(String fxmlFileName) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource("/view/" + fxmlFileName));
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    */
}