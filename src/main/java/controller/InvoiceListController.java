package controller;

import java.io.IOException;

import ctrl.InvoiceCtrl;
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
import model.Account; // Import Model Account
import model.Invoice;
import repository.InvoiceRepository;

public class InvoiceListController {
    @FXML private TableView<Invoice> tblInvoices;
    @FXML private TableColumn<Invoice, String> colID, colBookingID, colDate, colStaff, colNote;
    @FXML private TableColumn<Invoice, Double> colAmount;

    private InvoiceCtrl invoiceCtrl = new InvoiceCtrl();
    private Account currentAccount; // 1. Khai báo biến lưu tài khoản hiện tại

    // Hàm để nhận tài khoản từ AccountantController truyền sang
    public void setAccount(Account account) {
        this.currentAccount = account;
    }

    @FXML
    public void initialize() {
        colID.setCellValueFactory(new PropertyValueFactory<>("invoiceID"));
        colBookingID.setCellValueFactory(new PropertyValueFactory<>("bookingID"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        colStaff.setCellValueFactory(new PropertyValueFactory<>("createdByStaffId"));
        colNote.setCellValueFactory(new PropertyValueFactory<>("note"));

        loadData(); // 2. Gọi hàm load dữ liệu lúc khởi tạo
    }

    private void loadData() {
        ObservableList<Invoice> data = FXCollections.observableArrayList(InvoiceRepository.loadInvoicesFromFile());
        tblInvoices.setItems(data);
    }

    @FXML
    void handleEditRequest() {
        Invoice selected = tblInvoices.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Vui lòng chọn hóa đơn!").show();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/InvoiceEditRequestView.fxml"));
            Parent root = loader.load();
            
            InvoiceEditRequestController ctrl = loader.getController();
            // Truyền cả hóa đơn và tài khoản hiện tại
            ctrl.setInvoiceInfo(selected, this.currentAccount);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Yêu cầu sửa hóa đơn");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void openCreateInvoicePopup(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SelectBookingPopup.fxml"));
            Parent root = loader.load();
            
            SelectBookingController popupCtrl = loader.getController();
            
            // TRUYỀN TÀI KHOẢN VÀO POPUP
            if (this.currentAccount != null) {
                popupCtrl.setAccount(this.currentAccount);
            }

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Chọn Booking");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            
            loadData(); // Load lại bảng hóa đơn sau khi tạo xong
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}