package controller;

import java.net.URL;
import java.util.ResourceBundle;

import ctrl.AccountCtrl;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Account;

public class EditAccountController implements Initializable {

    @FXML private TextField txtUsername;
    @FXML private ComboBox<String> cbRole;
    @FXML private ComboBox<String> cbStatus; // Chứa "Hoạt động" / "Đã khóa"
    @FXML private TextField txtNewPass;
    @FXML private Button btnSave;
    @FXML private Button btnCancel;

    private AccountCtrl accountCtrl;
    private Account currentAccount;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        accountCtrl = new AccountCtrl();
        
        // Setup ComboBox
        cbRole.getItems().addAll("Admin", "Manager", "Staff");
        cbStatus.getItems().addAll("Hoạt động", "Đã khóa");
    }

    // Hàm nhận dữ liệu từ bảng quản lý
    public void setEditData(Account account) {
        this.currentAccount = account;
        
        txtUsername.setText(account.getUsername());
        cbRole.setValue(account.getRole());
        
        // Convert boolean isActive sang String hiển thị
        if (account.getIsActive()) {
            cbStatus.setValue("Hoạt động");
        } else {
            cbStatus.setValue("Đã khóa");
        }
    }

    @FXML
    private void handleSave() {
        if (currentAccount == null) return;

        try {
            String username = currentAccount.getUsername();

            // 1. Xử lý đổi quyền (Role)
            String newRole = cbRole.getValue();
            if (!newRole.equals(currentAccount.getRole())) {
                accountCtrl.updateAccountRole(username, newRole); // Cần thêm hàm này vào AccountCtrl như hướng dẫn ở trên
            }

            // 2. Xử lý đổi trạng thái (Status)
            String newStatusStr = cbStatus.getValue();
            boolean newStatus = newStatusStr.equals("Hoạt động");
            
            if (newStatus != currentAccount.getIsActive()) {
                if (newStatus) {
                    accountCtrl.activateAccount(username);
                } else {
                    accountCtrl.deactivateAccount(username);
                }
            }

            // 3. Xử lý đổi mật khẩu (chỉ đổi nếu người dùng nhập gì đó)
            String newPass = txtNewPass.getText().trim();
            if (!newPass.isEmpty()) {
                // Hàm này của bạn sẽ ném exception nếu pass < 6 ký tự
                accountCtrl.changeAccountPassword(username, newPass);
            }

            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã cập nhật tài khoản: " + username);
            closeWindow();

        } catch (Exception e) {
            // Bắt lỗi InvalidPasswordException từ AccountCtrl
            showAlert(Alert.AlertType.ERROR, "Lỗi cập nhật", e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}