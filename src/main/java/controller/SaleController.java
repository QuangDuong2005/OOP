package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Modality;
import java.io.IOException;

public class SaleController {

    @FXML
    private StackPane contentArea;

    // Chuyển sang màn hình Quản lý Khách hàng (sẽ làm chi tiết sau)
    @FXML
    private void showCustomers(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CustomerView.fxml"));
            Parent root = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showBooking(ActionEvent event) {
        // Placeholder cho chức năng Booking
        System.out.println("Chuyển sang màn hình Booking");
    }

    @FXML
    private void showOrders(ActionEvent event) {
        // Placeholder cho chức năng Đơn hàng
        System.out.println("Chuyển sang màn hình Đơn hàng");
    }

    // Hiển thị dialog Đổi mật khẩu (Sử dụng lại EditAccount.fxml)
    @FXML
    private void showProfile(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/EditAccount.fxml"));
            Parent root = loader.load();

            EditAccountController controller = loader.getController();
            // Cấu hình cho mode: CHỈ ĐỔI MẬT KHẨU (Personal Mode)
            controller.setPersonalMode(); 

            Stage stage = new Stage();
            stage.setTitle("Đổi mật khẩu cá nhân");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        // Xử lý đăng xuất (Xóa session, chuyển về Login)
        utils.UserSession.getInstance().cleanUserSession();
        // Code chuyển cảnh về màn hình login ở đây...
        System.out.println("Đăng xuất thành công");
        // Ví dụ: ((Stage) contentArea.getScene().getWindow()).close();
    }
}