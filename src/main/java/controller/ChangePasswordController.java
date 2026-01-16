package controller;

import ctrl.AccountCtrl; // Import Controller của bạn
import exception.InvalidPasswordException; // Import Exception để bắt lỗi
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import model.Account;

public class ChangePasswordController {

    @FXML private PasswordField txtOldPass;
    @FXML private PasswordField txtNewPass;
    @FXML private PasswordField txtConfirmPass;
    @FXML private Label lblError;

    private Account currentAccount; 
    private AccountCtrl accountCtrl; // Khai báo biến AccountCtrl

    public ChangePasswordController() {
        // Khởi tạo AccountCtrl
        this.accountCtrl = new AccountCtrl();
    }

    public void setCurrentAccount(Account account) {
        this.currentAccount = account;
    }

    @FXML
    void handleSave(ActionEvent event) {
        String oldPass = txtOldPass.getText();
        String newPass = txtNewPass.getText();
        String confirmPass = txtConfirmPass.getText();

        // 1. Kiểm tra nhập liệu trống
        if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            lblError.setText("Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        // 2. Kiểm tra mật khẩu cũ (Logic này AccountCtrl không có nên ta phải làm ở đây)
        if (!oldPass.equals(currentAccount.getPassword())) {
            lblError.setText("Mật khẩu cũ không chính xác!");
            return;
        }

        // 3. Kiểm tra xác nhận mật khẩu
        if (!newPass.equals(confirmPass)) {
            lblError.setText("Mật khẩu xác nhận không khớp!");
            return;
        }

        // 4. Gọi AccountCtrl để xử lý logic nghiệp vụ và lưu file
        try {
            // Gọi hàm từ AccountCtrl của bạn
            accountCtrl.changeAccountPassword(currentAccount.getUsername(), newPass);
            
            // Nếu không bị lỗi thì cập nhật lại mật khẩu cho session hiện tại
            currentAccount.setPassword(newPass);
            
            System.out.println("Đổi mật khẩu thành công: " + currentAccount.getUsername());
            closeDialog();
            
        } catch (InvalidPasswordException e) {
            // Bắt lỗi từ AccountCtrl ném ra (VD: Mật khẩu < 6 ký tự, trùng mật khẩu cũ)
            lblError.setText(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            lblError.setText("Lỗi hệ thống: " + e.getMessage());
        }
    }

    @FXML
    void handleCancel(ActionEvent event) {
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) txtOldPass.getScene().getWindow();
        stage.close();
    }
}