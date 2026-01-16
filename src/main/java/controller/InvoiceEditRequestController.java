package controller;

import ctrl.InvoiceCtrl;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Account;
import model.Invoice;

public class InvoiceEditRequestController {
    @FXML private Label lblInvoiceID;
    @FXML private TextField txtNewAmount;
    @FXML private TextArea txtReason;

    private Invoice selectedInvoice;
    private Account currentAccount; // Thêm để lấy staffID
    private InvoiceCtrl invoiceCtrl = new InvoiceCtrl();

    public void setInvoiceInfo(Invoice invoice, Account account) {
        this.selectedInvoice = invoice;
        this.currentAccount = account;
        this.lblInvoiceID.setText(invoice.getInvoiceID());
    }

    @FXML
    void handleSubmit() {
        try {
            String invID = selectedInvoice.getInvoiceID();
            double amount = Double.parseDouble(txtNewAmount.getText());
            String reason = txtReason.getText();
            String staffID = currentAccount.getUsername(); // Lấy staffID từ account

            if (reason.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Vui lòng nhập lý do!").show();
                return;
            }

            // Gọi đúng hàm InvoiceEdit với 4 tham số như bạn đã viết
            invoiceCtrl.InvoiceEdit(invID, amount, reason, staffID);

            new Alert(Alert.AlertType.INFORMATION, "Đã gửi yêu cầu chỉnh sửa thành công!").showAndWait();
            handleCancel();
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Số tiền không hợp lệ!").show();
        }
    }

    @FXML
    void handleCancel() {
        ((Stage) lblInvoiceID.getScene().getWindow()).close();
    }
}