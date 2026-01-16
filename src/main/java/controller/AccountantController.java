package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Account;

public class AccountantController implements Initializable {

    @FXML
    private StackPane contentArea; // Vùng hiển thị nội dung thay đổi ở giữa
    private Account currentAccount; // Thêm biến này để lưu tài khoản đang đăng nhập

    // Hàm để nhận tài khoản từ màn hình Login
    public void setCurrentAccount(Account account) {
        this.currentAccount = account;
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Khi mở lên, có thể mặc định hiện trang Hóa đơn luôn nếu muốn
        // showInvoices(null);
    }

    // --- Xử lý nút "Quản lý Hóa đơn" ---
    @FXML
    void showInvoices(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/InvoiceListView.fxml"));
            Parent view = loader.load();
            
            // Lấy controller và truyền account sang cấp tiếp theo
            InvoiceListController ctrl = loader.getController();
            if (this.currentAccount != null) {
                ctrl.setAccount(this.currentAccount);
            }
            
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- Xử lý nút "Thống kê Tài chính" ---
    @FXML
    void showFinancialReport(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/FinancialReportView.fxml"));
            Parent view = loader.load();
            
            // Lấy controller và truyền account vào
            FinancialReportController ctrl = loader.getController();
            ctrl.setAccount(this.currentAccount); // Quan trọng: Truyền tài khoản để không bị lỗi null
            
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- Xử lý nút "Đăng xuất" ---
    @FXML
    void handleChangePassword(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ChangePasswordView.fxml"));
            Parent root = loader.load();

            // Lấy controller của màn hình đổi mật khẩu và truyền account vào
            ChangePasswordController controller = loader.getController();
            controller.setCurrentAccount(this.currentAccount);

            Stage stage = new Stage();
            stage.setTitle("Đổi Mật Khẩu");
            stage.initModality(Modality.APPLICATION_MODAL); // Ngăn tương tác với màn hình chính
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
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

    // Hàm hỗ trợ load các view con vào vùng giữa (contentArea)
    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            contentArea.getChildren().clear(); // Xóa nội dung cũ
            contentArea.getChildren().add(view); // Thêm nội dung mới
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Không tìm thấy file FXML: " + fxmlPath);
        }
    }
}