package controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AccountDialogController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtOwnerID;
    @FXML private ComboBox<String> comboRole;
    @FXML private Label lblError;

    @FXML
    public void initialize() {
        // Nạp dữ liệu vào ComboBox Role
        comboRole.getItems().addAll("Manager", "Sale", "Accountant");
    }

    @FXML
    void handleSave() {
        // Logic lưu sẽ viết ở đây
        System.out.println("Bấm Lưu!");
    }

    @FXML
    void handleCancel() {
        // Đóng cửa sổ dialog
        Stage stage = (Stage) txtUsername.getScene().getWindow();
        stage.close();
    }
    // Hàm này sẽ được gọi khi bấm nút "Đổi Mật khẩu"
    public void setPasswordOnlyMode() {
        // Giả sử tên biến của bạn là txtUsername và comboRole
        // Nếu tên khác, hãy sửa lại cho khớp với @FXML của bạn
        if (txtUsername != null) txtUsername.setDisable(true); // Khóa tên đăng nhập
        if (comboRole != null) comboRole.setDisable(true);     // Khóa quyền
        
        // (Tùy chọn) Đổi tiêu đề cửa sổ nếu có label tiêu đề
        // lblTitle.setText("CẬP NHẬT MẬT KHẨU"); 
    }
}