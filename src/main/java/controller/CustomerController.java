package controller;

import java.io.IOException;

import ctrl.CustomerCtrl;
import javafx.beans.property.SimpleStringProperty;
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
import model.Customer;

public class CustomerController {

    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, String> colID;
    @FXML private TableColumn<Customer, String> colName;
    @FXML private TableColumn<Customer, String> colPhone;
    @FXML private TableColumn<Customer, String> colEmail;
    @FXML private TableColumn<Customer, String> colDob;

    private CustomerCtrl customerCtrl;
    private ObservableList<Customer> customerList;

    public void initialize() {
        customerCtrl = new CustomerCtrl();
        customerList = FXCollections.observableArrayList();

        // Cấu hình các cột
        colID.setCellValueFactory(new PropertyValueFactory<>("customerID")); //
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        
        // Vì dobString trong model là String
        colDob.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDateOfBirth()));

        customerTable.setItems(customerList);
        loadData();
    }

    private void loadData() {
        customerList.clear();
        customerList.addAll(customerCtrl.getCustomers());
    }

    @FXML
    void handleRefresh(ActionEvent event) {
        loadData();
    }

    @FXML
    void handleAdd(ActionEvent event) {
        openDialog(null);
    }

    @FXML
    void handleEdit(ActionEvent event) {
        Customer selected = customerTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Lỗi", "Vui lòng chọn khách hàng để sửa!");
            return;
        }
        openDialog(selected);
    }

    private void openDialog(Customer customer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/CustomerDialog.fxml"));
            Parent root = loader.load();

            CustomerDialogController dialogCtrl = loader.getController();
            dialogCtrl.setCustomerCtrl(this.customerCtrl); // Truyền Ctrl sang để xử lý logic
            
            if (customer != null) {
                dialogCtrl.setEditData(customer);
            }

            Stage stage = new Stage();
            stage.setTitle(customer == null ? "Thêm Khách Hàng" : "Cập Nhật Thông Tin");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
            loadData(); // Tải lại bảng sau khi đóng dialog

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}