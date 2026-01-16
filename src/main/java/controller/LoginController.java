package controller;

import ctrl.LoginCtrl;
import java.io.IOException;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Account;
import repository.AccountRepository;

public class LoginController {

    // Khai báo các biến khớp với fx:id trong file FXML
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    // Hàm này sẽ chạy khi bấm nút "Đăng nhập" (onAction="#handleLogin")
    @FXML
    public void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // 1. Kiểm tra nhập liệu cơ bản
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        // 2. Gọi logic kiểm tra đăng nhập từ Backend cũ
        // Load danh sách tài khoản từ file JSON
        List<Account> accounts = AccountRepository.loadAccountsFromFile(); 
        // Gọi hàm authenticate của bạn
        Account acc = LoginCtrl.authenticate(username, password, accounts);

        if (acc != null) {
            // 3. Kiểm tra xem tài khoản có bị khóa không
            if (!acc.getIsActive()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Thông báo");
                alert.setHeaderText("Tài khoản đã bị khóa!");
                alert.setContentText("Vui lòng liên hệ Quản lý để được kích hoạt lại.");
                
                // Hiện thông báo và đợi người dùng bấm OK
                alert.showAndWait();
                return;
            }
            util.UserSession.setSession(acc);
            // 4. Đăng nhập thành công -> Chuyển màn hình
            System.out.println("Login Success: " + acc.getRole());
            errorLabel.setText(""); // Xóa lỗi
            
            // Điều hướng dựa trên Role
            try {
                String role = acc.getRole();
                String dashboardFXML = "";

                switch (role) {
                    case "Manager":
                        dashboardFXML = "/view/ManagerView.fxml"; // Giả sử bạn sẽ tạo file này
                        break;
                    case "Sale":
                        dashboardFXML = "/view/SaleView.fxml";
                        break;
                    case "Accountant":
                        dashboardFXML = "/view/AccountantView.fxml";
                        break;
                    default:
                        errorLabel.setText("Role không hợp lệ: " + role);
                        return;
                }

                switchScene(event, dashboardFXML, acc);

            } catch (IOException e) {
                e.printStackTrace();
                errorLabel.setText("Lỗi khi tải màn hình: " + e.getMessage());
            }

        } else {
            // Đăng nhập thất bại
            errorLabel.setText("Sai tên đăng nhập hoặc mật khẩu!");
        }
    }

    // Hàm hỗ trợ chuyển cảnh (Scene)
    private void switchScene(ActionEvent event, String fxmlPath, Account account) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();

        // Lấy controller của màn hình mới (Manager, Sale, hoặc Accountant)
        Object controller = loader.getController();

        // KIỂM TRA VÀ TRUYỀN TÀI KHOẢN CHO KẾ TOÁN
        if (controller instanceof AccountantController) {
            AccountantController accountantCtrl = (AccountantController) controller;
            accountantCtrl.setCurrentAccount(account); 
        } 
        // TRUYỀN TÀI KHOẢN CHO SALE
        else if (controller instanceof SaleController) {
            SaleController saleCtrl = (SaleController) controller;
            saleCtrl.setAccount(account);
        }

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
        stage.centerOnScreen();
    }
}