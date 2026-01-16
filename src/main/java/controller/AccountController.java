package controller;

import java.io.IOException;
import java.util.List;

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
import javafx.stage.Stage;
import model.Account;
import repository.AccountRepository;

public class AccountController {

    @FXML
    private TableView<Account> userTable;
    @FXML
    private TableColumn<Account, String> colUsername;
    @FXML
    private TableColumn<Account, String> colOwnerID; // Sửa từ fullName -> ownerID
    @FXML
    private TableColumn<Account, String> colRole;
    @FXML
    private TableColumn<Account, Boolean> colStatus;

    // Danh sách hiển thị trên bảng
    private ObservableList<Account> accountList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Cấu hình cột: Tên chuỗi trong String phải khớp chính xác với tên biến trong model/Account.java
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colOwnerID.setCellValueFactory(new PropertyValueFactory<>("ownerID"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("isActive")); // isActive hoặc active

        loadData();
    }

    @FXML
    void loadData() {
        accountList.clear();
        List<Account> data = AccountRepository.loadAccountsFromFile();
        accountList.addAll(data);
        userTable.setItems(accountList);
    }

    @FXML
    void handleAdd(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CreateAccount.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Cấp tài khoản mới");
            stage.setScene(new Scene(root));
            // Chờ tạo xong để refresh lại bảng tài khoản
            stage.showAndWait(); 
            
            // Nhớ viết hàm load lại danh sách tài khoản ở đây
            loadData(); 
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleEdit(ActionEvent event) {
        Account selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Chưa chọn", "Vui lòng chọn tài khoản cần sửa.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/EditAccount.fxml"));
            Parent root = loader.load();

            // Truyền dữ liệu sang form sửa
            EditAccountController ctrl = loader.getController();
            ctrl.setEditData(selected);

            Stage stage = new Stage();
            stage.setTitle("Cập nhật tài khoản");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Sau khi form đóng, load lại bảng
            loadData(); 

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleDelete(ActionEvent event) {
        Account selected = userTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if ((selected.getRole()).equals("Manager")){
                showAlert("Lỗi", "Bạn không thể sửa tải khoản của quản lý");
            }else{
                // Logic khóa tài khoản (Soft Delete)
                boolean newStatus = !selected.getIsActive(); 
                selected.setIsActive(newStatus); 
                
                // Lưu lại vào file JSON
                AccountRepository.saveAccountsToFile(accountList);
                
                // Refresh bảng
                userTable.refresh();
                showAlert("Thành công", "Đã khóa tài khoản: " + selected.getUsername());
            }
        } else {
            showAlert("Lỗi", "Vui lòng chọn tài khoản cần khóa!");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
    // Hàm hiển thị thông báo
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
}