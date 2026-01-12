package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Staff;
import repository.StaffRepository;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class EmployeeController {

    @FXML private TableView<Staff> employeeTable;
    
    // Lưu ý: Kiểu dữ liệu trong TableColumn phải khớp với Model
    @FXML private TableColumn<Staff, String> colId;     // Map với staffID
    @FXML private TableColumn<Staff, String> colName;   // Map với fullName (từ class Person)
    @FXML private TableColumn<Staff, String> colRole;   // Map với role
    @FXML private TableColumn<Staff, Boolean> colStatus; // Map với isActive

    private ObservableList<Staff> staffList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Cấu hình cột bảng:
        // Chuỗi trong PropertyValueFactory phải khớp với tên biến trong Staff (hoặc getter bỏ chữ get)
        colId.setCellValueFactory(new PropertyValueFactory<>("staffID")); 
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName")); 
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("isActive"));

        loadData();
    }

    public void loadData() {
        staffList.clear();
        List<Staff> data = StaffRepository.loadStaff();
        staffList.addAll(data);
        employeeTable.setItems(staffList);
    }

    @FXML
    void handleAdd(ActionEvent event) {
        openDialog(null); // Null = Thêm mới
    }

    @FXML
    void handleEdit(ActionEvent event) {
        Staff selected = employeeTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            openDialog(selected); // Có dữ liệu = Sửa
        } else {
            showAlert("Lỗi", "Vui lòng chọn nhân viên cần sửa!");
        }
    }

    @FXML
    void handleDelete(ActionEvent event) {
        Staff selected = employeeTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Logic nghỉ việc: Set ngày nghỉ là hôm nay và isActive = false
            if (selected.getIsActive()) {
                selected.setIsActive(false);
                selected.setEnDate(LocalDate.now().toString()); // Set ngày nghỉ
            } else {
                // Nếu muốn kích hoạt lại
                selected.setIsActive(true);
                selected.setEnDate("null"); // Set ngày nghỉ về null
            }
            
            // Lưu lại file
            StaffRepository.saveStaff(staffList);
            employeeTable.refresh();
            
            String status = selected.getIsActive() ? "Kích hoạt lại" : "Cho nghỉ việc";
            showAlert("Thành công", "Đã " + status + " nhân viên: " + selected.getStaffID());
        } else {
            showAlert("Lỗi", "Chưa chọn nhân viên!");
        }
    }

    private void openDialog(Staff staff) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/EmployeeDialog.fxml"));
            Parent root = loader.load();

            EmployeeDialogController dialogCtrl = loader.getController();
            dialogCtrl.setParentController(this); 
            
            if (staff != null) {
                dialogCtrl.setEditData(staff);
            }

            Stage stage = new Stage();
            stage.setTitle(staff == null ? "Thêm Nhân Viên" : "Cập Nhật Thông Tin");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }
}