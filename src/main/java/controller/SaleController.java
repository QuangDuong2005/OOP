package controller;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Account;

public class SaleController {
    private Account currentAccount;
    public void setAccount(Account acc) {
        this.currentAccount = acc;
        // Có thể cập nhật Label chào mừng ở đây: "Chào, " + acc.getUsername()
    }
    @FXML
    private StackPane contentArea; // Vùng hiển thị nội dung thay đổi

    @FXML
    void showCustomers(ActionEvent event) {
        // Hiện tại chưa có file CustomerView.fxml trong danh sách tải lên
        // Bạn cần tạo file này và bỏ comment dòng dưới để chạy
        loadView("/view/CustomerView.fxml");
        
        // loadView("/view/CustomerView.fxml"); 
    }

    @FXML
    void showBooking(ActionEvent event) {
        // Sử dụng TourView.fxml để hiển thị danh sách Tour cho quy trình Booking
        try {
            Parent view = FXMLLoader.load(getClass().getResource("/view/TourView.fxml"));
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace(); // In lỗi nếu không tìm thấy file hoặc lỗi code trong Controller con
            System.err.println("Lỗi: Không thể tải /view/TourView.fxml");
        }
    }

    @FXML
    void showOrders(ActionEvent event) {
        System.out.println("Click: Danh sách Đơn hàng");
                try {
            Parent view = FXMLLoader.load(getClass().getResource("/view/BookingList.fxml"));
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace(); // In lỗi nếu không tìm thấy file hoặc lỗi code trong Controller con
            System.err.println("Lỗi: Không thể tải /view/BookingList.fxml");
        }
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
    @FXML
    void handleChangePassword(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ChangePasswordView.fxml"));
            Parent root = loader.load();

            // Truyền user hiện tại sang màn hình đổi pass
            ChangePasswordController ctrl = loader.getController();
            ctrl.setCurrentAccount(this.currentAccount);

            Stage stage = new Stage();
            stage.setTitle("Đổi Mật Khẩu");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Chặn tương tác màn hình chính
            stage.showAndWait();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Hàm tiện ích để load view (nếu bạn muốn code gọn hơn)
    private void loadView(String fxmlPath) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(fxmlPath));
                contentArea.getChildren().clear();
                contentArea.getChildren().add(view);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
}