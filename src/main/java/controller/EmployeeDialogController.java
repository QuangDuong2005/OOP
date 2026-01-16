package controller;

import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Staff;
import repository.StaffRepository;

public class EmployeeDialogController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtFullName;
    @FXML private ComboBox<String> comboRole;
    @FXML private Label lblError;

    private EmployeeController parentController;
    private Staff currentStaff = null; 

    @FXML
    public void initialize() {
        comboRole.getItems().addAll("Manager", "Sale", "Accountant", "Guide", "Admin");
        comboRole.getSelectionModel().selectFirst();
    }

    public void setParentController(EmployeeController parent) {
        this.parentController = parent;
    }

    public void setEditData(Staff staff) {
        this.currentStaff = staff;
        txtUsername.setText(staff.getStaffID());
        txtUsername.setDisable(true);
        txtFullName.setText(staff.getFullName());
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

        // SỬA: Gọi đúng loadStaffsFromFile
        List<Staff> list = StaffRepository.loadStaffsFromFile();

        if (currentStaff == null) {
            for (Staff s : list) {
                if (s.getStaffID().equalsIgnoreCase(id)) {
                    lblError.setText("Mã nhân viên đã tồn tại!");
                    return;
                }
            }
            
            // Tạo nhân viên mới với dữ liệu mặc định cho các trường thiếu
            Staff newStaff = new Staff(
                id, 
                name, 
                "2000-01-01", 
                "0000000000", 
                "000000000000", 
                "", 
                role
            );
            list.add(newStaff);
            
        } else {
            for (Staff s : list) {
                if (s.getStaffID().equals(currentStaff.getStaffID())) {
                    s.setFullName(name);
                    s.setRole(role);
                    break;
                }
            }
        }

        // SỬA: Gọi đúng saveStaffsToFile
        StaffRepository.saveStaffsToFile(list);

        parentController.loadData();
        handleCancel();
    }

    @FXML
    void handleCancel() {
        Stage stage = (Stage) txtUsername.getScene().getWindow();
        stage.close();
    }
}