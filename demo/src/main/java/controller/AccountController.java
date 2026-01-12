package controller;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
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
        // TODO: Mở Dialog thêm mới (Sẽ làm ở bước sau)
        showAlert("Thông báo", "Chức năng Thêm đang được xây dựng!");
    }

    @FXML
    void handleEdit(ActionEvent event) {
        Account selected = userTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // TODO: Mở Dialog sửa
            showAlert("Thông báo", "Bạn chọn sửa: " + selected.getUsername());
        } else {
            showAlert("Lỗi", "Vui lòng chọn tài khoản cần sửa!");
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
}