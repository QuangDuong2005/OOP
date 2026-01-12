package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Staff;
import repository.StaffRepository;

import java.util.List;

public class EmployeeDialogController {

    @FXML private TextField txtUsername; // Dùng làm StaffID
    @FXML private PasswordField txtPassword; // Tạm thời chưa dùng vì Staff không chứa password
    @FXML private TextField txtFullName;
    @FXML private ComboBox<String> comboRole;
    @FXML private Label lblError;

    private EmployeeController parentController;
    private Staff currentStaff = null; 

    @FXML
    public void initialize() {
        // Các vai trò theo model của bạn
        comboRole.getItems().addAll("Manager", "Sale", "Accountant", "Guide", "Admin");
        comboRole.getSelectionModel().selectFirst();
    }

    public void setParentController(EmployeeController parent) {
        this.parentController = parent;
    }

    public void setEditData(Staff staff) {
        this.currentStaff = staff;
        
        // Điền dữ liệu cũ vào form
        txtUsername.setText(staff.getStaffID());
        txtUsername.setDisable(true); // Không cho sửa ID khi update
        
        txtFullName.setText(staff.getFullName()); // Hàm này lấy từ class cha Person
        comboRole.setValue(staff.getRole());
    }

    @FXML
    void handleSave() {
        String id = txtUsername.getText();
        String name = txtFullName.getText();
        String role = comboRole.getValue();

        if (id.isEmpty() || name.isEmpty()) {
            lblError.setText("Vui lòng nhập đủ ID và Họ tên!");
            return;
        }

        List<Staff> list = StaffRepository.loadStaff();

        if (currentStaff == null) {
            // --- THÊM MỚI ---
            // Check trùng ID
            for (Staff s : list) {
                if (s.getStaffID().equalsIgnoreCase(id)) {
                    lblError.setText("Mã nhân viên đã tồn tại!");
                    return;
                }
            }

            // TẠO NHÂN VIÊN MỚI
            // Vì form của bạn thiếu ô nhập Ngày sinh, SĐT, CCCD, Email...
            // Nên tôi để giá trị mặc định là "N/A" hoặc rỗng để code chạy được.
            // Bạn cần vào FXML vẽ thêm các ô TextField cho các trường này sau.
            Staff newStaff = new Staff(
                id,             // ID
                name,           // FullName
                "2000-01-01",   // DOB (Mặc định)
                "0000000000",   // Phone (Mặc định)
                "000000000000", // CCCD (Mặc định)
                "",             // Email (Mặc định)
                role            // Role
            );
            
            list.add(newStaff);
            
        } else {
            // --- CẬP NHẬT ---
            for (Staff s : list) {
                if (s.getStaffID().equals(currentStaff.getStaffID())) {
                    s.setFullName(name); // Setter của Person
                    s.setRole(role);
                    // Các trường khác giữ nguyên hoặc bạn phải thêm ô nhập để sửa
                    break;
                }
            }
        }

        // Lưu file JSON
        StaffRepository.saveStaff(list);

        // Refresh bảng
        parentController.loadData();
        handleCancel();
    }

    @FXML
    void handleCancel() {
        Stage stage = (Stage) txtUsername.getScene().getWindow();
        stage.close();
    }
}