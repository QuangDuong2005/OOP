package controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import ctrl.AccountCtrl;
import exception.DuplicateException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Staff;
import repository.StaffRepository;

public class CreateAccountController implements Initializable {

    // --- Bảng Nhân viên ---
    @FXML private TableView<Staff> tblStaff;
    @FXML private TableColumn<Staff, String> colID;
    @FXML private TableColumn<Staff, String> colName;
    @FXML private TableColumn<Staff, String> colPhone;
    @FXML private TableColumn<Staff, String> colStaffRole;

    // --- Form nhập liệu ---
    @FXML private TextField txtSelectedStaff; // Chỉ để hiển thị tên NV đã chọn
    @FXML private TextField txtUsername;
    @FXML private ComboBox<String> cbRole;
    @FXML private Button btnCreate;
    @FXML private Button btnCancel;

    private AccountCtrl accountCtrl;
    private Staff selectedStaff = null; // Lưu nhân viên đang được chọn

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        accountCtrl = new AccountCtrl();
        
        // 1. Cấu hình bảng nhân viên
        setupTable();
        
        // 2. Load dữ liệu nhân viên vào bảng
        loadStaffData();

        // 3. Cấu hình ComboBox quyền
        cbRole.getItems().addAll( "Manager", "Sale", "Accountant");
        cbRole.getSelectionModel().select("Manager");
        
        // 4. Lắng nghe sự kiện chọn dòng trong bảng
        tblStaff.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedStaff = newVal;
                // Hiển thị tên NV lên ô text field để dễ nhìn
                txtSelectedStaff.setText(newVal.getFullName() + " - " + newVal.getStaffID());
            }
        });
    }

    private void setupTable() {
        // Map các cột với thuộc tính trong Staff.java
        // Chú ý: Staff phải có getter tương ứng: getStaffID, getFullName...
        colID.setCellValueFactory(new PropertyValueFactory<>("staffID"));
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        
        // Nếu Staff lưu role là chuỗi thì dùng PropertyValueFactory
        colStaffRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        
        // Ví dụ phone (sửa lại tên thuộc tính nếu bên model khác)
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
    }

    private void loadStaffData() {
        // Lấy danh sách từ file/DB
        List<Staff> list = StaffRepository.loadStaffsFromFile();
        
        // Có thể lọc: Chỉ hiện những nhân viên ĐANG HOẠT ĐỘNG (isActive = true)
        // Hoặc lọc những người chưa có tài khoản (nếu logic phức tạp hơn)
        ObservableList<Staff> observableList = FXCollections.observableArrayList(list);
        tblStaff.setItems(observableList);
    }

    @FXML
    private void handleCreateAccount() {
        // 1. Kiểm tra đã chọn nhân viên chưa
        if (selectedStaff == null) {
            showAlert(Alert.AlertType.ERROR, "Chưa chọn nhân viên", "Vui lòng chọn một nhân viên từ danh sách bên trái!");
            return;
        }

        // 2. Lấy dữ liệu nhập
        String username = txtUsername.getText().trim();
        String role = cbRole.getValue();

        if (username.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Thiếu thông tin", "Vui lòng nhập Tên đăng nhập.");
            return;
        }

        try {
            // 3. Gọi AccountCtrl để tạo
            // ownerID lấy từ selectedStaff.getStaffID()
            String generatedPassword = accountCtrl.createAccount(username, role, selectedStaff.getStaffID());

            // 4. Hiển thị mật khẩu cho người dùng copy
            showPasswordDialog(username, generatedPassword);

            // 5. Đóng form sau khi xong
            closeWindow();

        } catch (DuplicateException e) {
            showAlert(Alert.AlertType.ERROR, "Trùng lặp", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi hệ thống", e.getMessage());
        }
    }

    private void showPasswordDialog(String user, String pass) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Tạo tài khoản thành công");
        alert.setHeaderText("Đã tạo tài khoản cho NV: " + selectedStaff.getFullName());
        
        TextArea textArea = new TextArea("Username: " + user + "\nPassword: " + pass);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);

        alert.getDialogPane().setContent(textArea);
        alert.showAndWait();
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