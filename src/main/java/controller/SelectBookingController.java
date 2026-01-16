package controller;

import ctrl.InvoiceCtrl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Account;
import model.Booking;
import repository.BookingRepository;

public class SelectBookingController {
    @FXML private TableView<Booking> tblPendingBookings;
    @FXML private TableColumn<Booking, String> colBookingID, colCustomer;
    @FXML private TableColumn<Booking, Double> colPrice, colDebt;
    
    @FXML private TextField txtInvoiceID;
    @FXML private TextField txtPaidAmount;
    @FXML private TextField txtNote;

    private Account currentAccount;
    private InvoiceCtrl invoiceCtrl = new InvoiceCtrl();

    public void setAccount(Account account) {
        this.currentAccount = account;
    }

    @FXML
    public void initialize() {
        colBookingID.setCellValueFactory(new PropertyValueFactory<>("bookingID"));
        colCustomer.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        colDebt.setCellValueFactory(new PropertyValueFactory<>("debtAmount"));

        loadBookingData();
        
        // Tự động điền số tiền trả bằng số tiền còn nợ khi chọn dòng trong bảng
        tblPendingBookings.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                txtPaidAmount.setText(String.valueOf(newVal.getDebtAmount()));
            }
        });
    }

    private void loadBookingData() {
        try {
            // Chỉ hiện các Booking chưa hủy và còn nợ tiền
            ObservableList<Booking> list = FXCollections.observableArrayList();
            for (Booking b : BookingRepository.loadBookingsFromFile()) {
                if (!b.getIsCancelled() && b.getDebtAmount() > 0) {
                    list.add(b);
                }
            }
            tblPendingBookings.setItems(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void confirmCreate() {
        Booking selected = tblPendingBookings.getSelectionModel().getSelectedItem();
        String invID = txtInvoiceID.getText().trim();
        String amountStr = txtPaidAmount.getText().trim();

        // 1. Kiểm tra nhập liệu
        if (selected == null || invID.isEmpty() || amountStr.isEmpty()) {
            showAlert("Lỗi", "Vui lòng chọn Booking và nhập đầy đủ Mã HĐ, Số tiền!");
            return;
        }

        if (currentAccount == null) {
            showAlert("Lỗi", "Không xác định được nhân viên đang đăng nhập!");
            return;
        }

        try {
            double paidAmount = Double.parseDouble(amountStr);

            // 2. Gọi logic nghiệp vụ từ InvoiceCtrl của bạn
            // createInvoice(bookingID, paidAmount, invoiceID, staffID)
            invoiceCtrl.createInvoice(
                selected.getBookingID(), 
                paidAmount, 
                invID, 
                currentAccount.getUsername()
            );

            showAlert("Thành công", "Đã tạo hóa đơn " + invID + " cho Booking " + selected.getBookingID());
            handleCancel(); // Đóng popup

        } catch (NumberFormatException e) {
            showAlert("Lỗi", "Số tiền phải là một con số!");
        } catch (Exception e) {
            showAlert("Lỗi hệ thống", e.getMessage());
        }
    }

    @FXML
    void handleCancel() {
        Stage stage = (Stage) tblPendingBookings.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}