package controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import ctrl.StaffCtrl;
import exception.DuplicateException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Staff;

public class AddEmployeeController implements Initializable {

    // --- Khai báo các biến khớp với FXML ---
    @FXML private TextField txtID;       // [MỚI] Phải có ô này để nhập ID
    @FXML private TextField txtFullName;
    @FXML private DatePicker dpDob;
    @FXML private TextField txtPhone;
    @FXML private TextField txtCCCD;     // [MỚI] Phải có ô này để nhập CCCD
    @FXML private TextField txtEmail;
    @FXML private ComboBox<String> cbPosition;
    
    @FXML private Button btnSave;
    @FXML private Button btnCancel;

    private StaffCtrl staffCtrl; // Gọi Logic từ lớp này
    private Staff currentStaff;  // Biến kiểm tra xem đang Thêm hay Sửa

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        staffCtrl = new StaffCtrl(); // Khởi tạo controller logic
        
        // Khởi tạo ComboBox chức vụ
        cbPosition.getItems().addAll("Manager", "Accountant", "Sales", "Tour Guide");
    }

    /**
     * Hàm nhận dữ liệu từ màn hình danh sách để thực hiện chức năng SỬA
     */
    public void setEditData(Staff staff) {
        this.currentStaff = staff;

        // Đổ dữ liệu cũ lên form
        txtID.setText(staff.getStaffID());
        txtFullName.setText(staff.getFullName());
        txtPhone.setText(staff.getPhoneNumber()); // Chú ý: staff.getPhoneNumber()
        txtCCCD.setText(staff.getCccd());         // Chú ý: staff.getCccd()
        txtEmail.setText(staff.getEmail());
        cbPosition.setValue(staff.getRole());

        // Xử lý ngày sinh
        if (staff.getDateOfBirth() != null && !staff.getDateOfBirth().isEmpty()) {
            try {
                dpDob.setValue(LocalDate.parse(staff.getDateOfBirth()));
            } catch (Exception e) {
                // Bỏ qua lỗi parse ngày nếu format sai
            }
        }

        // --- QUAN TRỌNG KHI SỬA ---
        // Không cho phép sửa ID (vì ID là khóa chính để tìm kiếm trong updateStaffInfo)
        txtID.setDisable(true); 
        btnSave.setText("Cập nhật");
    }

    @FXML
    private void handleSaveEmployee() {
        // 1. Lấy dữ liệu từ giao diện
        String id = txtID.getText().trim();
        String fullName = txtFullName.getText().trim();
        String phone = txtPhone.getText().trim();
        String cccd = txtCCCD.getText().trim();
        String email = txtEmail.getText().trim();
        String role = cbPosition.getValue();
        
        // Lấy chuỗi ngày sinh
        String dobString = "";
        if (dpDob.getValue() != null) {
            dobString = dpDob.getValue().toString();
        }

        // 2. Validate cơ bản (Kiểm tra rỗng)
        if (id.isEmpty() || fullName.isEmpty() || cccd.isEmpty() || role == null) {
            showAlert(Alert.AlertType.ERROR, "Thiếu thông tin", "Vui lòng nhập ID, Tên, CCCD và Chức vụ.");
            return;
        }

        try {
            if (currentStaff == null) {
                // --- TRƯỜNG HỢP THÊM MỚI ---
                // Gọi hàm createStaff của StaffCtrl
                staffCtrl.createStaff(id, fullName, dobString, phone, cccd, email, role);
                
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã thêm nhân viên mới: " + id);
            } else {
                // --- TRƯỜNG HỢP CẬP NHẬT ---
                // Gọi hàm updateStaffInfo của StaffCtrl
                // Lưu ý: Hàm updateStaffInfo của bạn hiện tại chỉ hỗ trợ update: Name, Phone, CCCD, Email
                staffCtrl.updateStaffInfo(id, fullName, phone, cccd, email);
                
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã cập nhật thông tin nhân viên.");
            }

            // Đóng cửa sổ sau khi xong
            closeWindow();

        } catch (DuplicateException e) {
            // Bắt lỗi trùng ID từ StaffCtrl ném ra
            showAlert(Alert.AlertType.ERROR, "Lỗi trùng lặp", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi hệ thống", "Có lỗi xảy ra: " + e.getMessage());
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