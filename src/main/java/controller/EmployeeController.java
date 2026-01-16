package controller;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Staff;
import repository.StaffRepository;

public class EmployeeController implements Initializable {
    
    @FXML private TableView<Staff> employeeTable;
    
    // Đảm bảo tên biến khớp với fx:id trong file FXML hiển thị danh sách
    @FXML private TableColumn<Staff, String> colId;
    @FXML private TableColumn<Staff, String> colName;
    @FXML private TableColumn<Staff, String> colRole; // Chức vụ
    @FXML private TableColumn<Staff, Boolean> colStatus; // Trạng thái

    private ObservableList<Staff> staffList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Cấu hình cột bảng (Map với tên thuộc tính trong class Staff)
        colId.setCellValueFactory(new PropertyValueFactory<>("staffID")); 
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName")); 
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("isActive"));

        // Tải dữ liệu ban đầu
        loadData();
    }

    /**
     * Hàm tải lại dữ liệu từ file/database lên bảng
     */
    public void loadData() {
        staffList.clear();
        // Gọi Repository để lấy danh sách mới nhất
        List<Staff> data = StaffRepository.loadStaffsFromFile();
        if (data != null) {
            staffList.addAll(data);
        }
        employeeTable.setItems(staffList);
        employeeTable.refresh();
    }

    @FXML
    void handleAdd(ActionEvent event) {
        // Mở form thêm mới (truyền null vì không phải là sửa)
        openDialog(null);
    }

    @FXML
    void handleEdit(ActionEvent event) {
        Staff selected = employeeTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            openDialog(selected);
        } else {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn nhân viên cần sửa!");
        }
    }

    @FXML
    void handleDelete(ActionEvent event) {
        Staff selected = employeeTable.getSelectionModel().getSelectedItem();
        
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Chưa chọn nhân viên!");
            return;
        }

        // Logic Soft Delete (Nghỉ việc/Kích hoạt lại)
        String actionName = selected.getIsActive() ? "cho nghỉ việc" : "kích hoạt lại";
        
        // Hỏi xác nhận trước khi thực hiện
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận");
        confirm.setHeaderText(null);
        confirm.setContentText("Bạn có chắc muốn " + actionName + " nhân viên " + selected.getFullName() + "?");
        
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Đảo ngược trạng thái
            boolean newStatus = !selected.getIsActive();
            selected.setIsActive(newStatus);
            
            // Cập nhật ngày nghỉ việc
            if (!newStatus) {
                selected.setEnDate(LocalDate.now().toString());
            } else {
                selected.setEnDate("null"); // Hoặc để rỗng tùy logic của bạn
            }
            
            // Lưu lại vào file thông qua Repository
            // Lưu ý: Repository nên có hàm update hoặc saveAll
            StaffRepository.saveStaffsToFile(staffList);
            
            // Refresh lại bảng
            employeeTable.refresh();
            
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã cập nhật trạng thái nhân viên thành công.");
        }
    }

    /**
     * Hàm mở cửa sổ dialog (Dùng chung cho Thêm và Sửa)
     * @param staff Nếu null -> Thêm mới. Nếu có data -> Sửa.
     */
    private void openDialog(Staff staff) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AddEmployee.fxml"));
            Parent root = loader.load();

            // Lấy controller của cửa sổ AddEmployee
            // Lưu ý: Phải đảm bảo AddEmployeeController có hàm setEditData nếu muốn dùng chức năng Sửa
            AddEmployeeController dialogCtrl = loader.getController();
            
            // Nếu là sửa, đổ dữ liệu vào form
            if (staff != null) {
                dialogCtrl.setEditData(staff); 
            }

            Stage stage = new Stage();
            stage.setTitle(staff == null ? "Thêm Nhân Viên Mới" : "Cập Nhật Thông Tin");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Chặn cửa sổ cha
            
            // --- QUAN TRỌNG: Chờ cửa sổ con đóng lại ---
            stage.showAndWait();

            // --- SAU KHI ĐÓNG: Tự động tải lại dữ liệu mới nhất ---
            loadData();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không mở được form: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.show();
    }
}