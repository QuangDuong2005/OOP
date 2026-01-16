package controller;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import ctrl.CustomerCtrl;
import exception.DuplicateException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Customer;

public class CustomerDialogController {

    @FXML private TextField txtID;
    @FXML private TextField txtName;
    @FXML private TextField txtPhone;
    @FXML private TextField txtEmail;
    @FXML private DatePicker dpDob;
    @FXML private Label lblTitle;
    @FXML private Label lblError;

    private CustomerCtrl customerCtrl;
    private boolean isEditMode = false;

    // Không cần formatter riêng nữa vì Model của bạn đang đòi chuẩn ISO (yyyy-MM-dd)

    public void setCustomerCtrl(CustomerCtrl ctrl) {
        this.customerCtrl = ctrl;
    }

    public void setEditData(Customer customer) {
        isEditMode = true;
        lblTitle.setText("CẬP NHẬT KHÁCH HÀNG");
        
        txtID.setText(customer.getCustomerID());
        txtID.setDisable(true); 
        
        txtName.setText(customer.getFullName());
        txtPhone.setText(customer.getPhoneNumber());
        txtEmail.setText(customer.getEmail());

        // --- SỬA 1: Parse ngày sinh ---
        try {
            String dobStr = customer.getDateOfBirth(); 
            // Model lưu dạng "yyyy-MM-dd", nên dùng parse mặc định là được
            if (dobStr != null && !dobStr.isEmpty()) {
                LocalDate date = LocalDate.parse(dobStr); 
                dpDob.setValue(date);
            }
        } catch (DateTimeParseException e) {
            System.err.println("Lỗi parse ngày cũ: " + e.getMessage());
            dpDob.setValue(null);
        }
        
        // Nếu muốn cho phép sửa ngày sinh khi Update, hãy bỏ dòng này đi
        // dpDob.setDisable(true); 
    }

    @FXML
    void handleSave(ActionEvent event) {
        String id = txtID.getText().trim();
        String name = txtName.getText().trim();
        String phone = txtPhone.getText().trim();
        String email = txtEmail.getText().trim();
        
        // --- SỬA 2: Lấy ngày từ DatePicker ---
        LocalDate selectedDate = dpDob.getValue();
        String dob = "";
        
        if (selectedDate == null) {
            lblError.setText("Vui lòng chọn ngày sinh!");
            return;
        } else {
            // QUAN TRỌNG: Dùng toString() để ra chuỗi "yyyy-MM-dd" (có gạch ngang)
            // Model Customer sẽ hiểu định dạng này mà không bị lỗi.
            dob = selectedDate.toString(); 
        }

        if (id.isEmpty() || name.isEmpty() || phone.isEmpty()) {
            lblError.setText("Vui lòng nhập ID, Tên và SĐT!");
            return;
        }

        try {
            if (isEditMode) {
                // Update
                customerCtrl.updateCustomerInfo(id, name, phone, email);
            } else {
                // Create: Truyền chuỗi "yyyy-MM-dd" vào, Customer constructor sẽ chịu nhận
                customerCtrl.createCustomer(id, name, dob, phone, email);
            }
            closeDialog();
        } catch (DuplicateException e) {
            lblError.setText("Lỗi: " + e.getMessage());
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
        Stage stage = (Stage) txtID.getScene().getWindow();
        stage.close();
    }
}