package controller;

import java.time.LocalDate;
import java.util.List;

import ctrl.ReportCtrl;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Account;
import model.Booking;
import model.Invoice;
import model.Report;
import repository.BookingRepository;
import repository.InvoiceRepository;
import repository.ReportRepository;

public class FinancialReportController {
    @FXML private DatePicker dpFrom, dpTo;
    @FXML private Label lblRevenue, lblInvoiceCount, lblBookingCount;
    @FXML private TableView<Report> tblReports;
    @FXML private TableColumn<Report, String> colId, colFrom, colTo;
    @FXML private TableColumn<Report, Double> colRevenue;

    private Account currentAccount;
    private Report lastCreatedReport;

    public void setAccount(Account account) {
        this.currentAccount = account;
    }

    @FXML
    public void initialize() {
        // Thiết lập bảng
        colId.setCellValueFactory(new PropertyValueFactory<>("reportId"));
        colFrom.setCellValueFactory(new PropertyValueFactory<>("fromDate"));
        colTo.setCellValueFactory(new PropertyValueFactory<>("toDate"));
        colRevenue.setCellValueFactory(new PropertyValueFactory<>("totalRevenue"));
        
        // Mặc định chọn tháng hiện tại
        dpFrom.setValue(LocalDate.now().withDayOfMonth(1));
        dpTo.setValue(LocalDate.now());
        
        refreshTable();
    }

    @FXML
    void handleGenerateReport() {
        if (currentAccount == null) {
            showAlert("Lỗi", "Không xác định được nhân viên đang đăng nhập!");
            return;
        }

        try {
            LocalDate from = dpFrom.getValue();
            LocalDate to = dpTo.getValue();
            
            // Lấy dữ liệu nguồn
            List<Invoice> invoices = InvoiceRepository.loadInvoicesFromFile();
            List<Booking> bookings = BookingRepository.loadBookingsFromFile();
            List<Report> cache = ReportRepository.loadFromFile();

            // Gọi hàm tạo báo cáo từ ReportCtrl của bạn
            lastCreatedReport = ReportCtrl.createRevenueReport(from, to, currentAccount.getUsername(), invoices, bookings, cache);

            // Cập nhật giao diện
            lblRevenue.setText(String.format("%,.0f VNĐ", lastCreatedReport.getTotalRevenue()));
            lblInvoiceCount.setText(String.valueOf(lastCreatedReport.getTotalInvoices()));
            lblBookingCount.setText(String.valueOf(lastCreatedReport.getTotalBookings()));
            
            refreshTable();
            showAlert("Thành công", "Đã tạo báo cáo doanh thu mới.");
            
        } catch (Exception e) {
            showAlert("Lỗi", e.getMessage());
        }
    }

    @FXML
    void handleExportCsv() {
        if (lastCreatedReport == null) {
            showAlert("Thông báo", "Vui lòng xem báo cáo trước khi xuất file!");
            return;
        }
        try {
            List<Invoice> invoices = InvoiceRepository.loadInvoicesFromFile();
            ReportCtrl.exportReportToCsv(lastCreatedReport, invoices);
            showAlert("Thành công", "Báo cáo đã được xuất ra thư mục 'exports'");
        } catch (Exception e) {
            showAlert("Lỗi", "Không thể xuất file: " + e.getMessage());
        }
    }

    private void refreshTable() {
        tblReports.setItems(FXCollections.observableArrayList(ReportRepository.loadFromFile()));
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}