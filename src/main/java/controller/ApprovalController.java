package controller;

import java.util.List;

import ctrl.ApprovalCtrl;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.ApprovalRequest;
import model.BookingCancelRequest;
import model.InvoiceEditRequest;
import repository.BookingCancelRequestRepository;
import repository.InvoiceEditRequestRepository;

public class ApprovalController {

    @FXML
    private TableView<ApprovalRequest> tableRequests;
    @FXML
    private TableColumn<ApprovalRequest, String> colId;
    @FXML
    private TableColumn<ApprovalRequest, String> colType;
    @FXML
    private TableColumn<ApprovalRequest, String> colStaff;
    @FXML
    private TableColumn<ApprovalRequest, String> colReason;
    @FXML
    private TableColumn<ApprovalRequest, String> colDetail;
    @FXML
    private TableColumn<ApprovalRequest, String> colStatus;

    private ApprovalCtrl approvalCtrl;
    
    // Giả lập ID của Manager đang đăng nhập (Bạn cần lấy từ UserSession thực tế)
    private final String CURRENT_MANAGER_ID = "MGR001"; 

    public ApprovalController() {
        this.approvalCtrl = new ApprovalCtrl();
    }

    @FXML
    public void initialize() {
        setupColumns();
        loadData();
    }

    private void setupColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("requestId"));
        colStaff.setCellValueFactory(new PropertyValueFactory<>("staffId"));
        colReason.setCellValueFactory(new PropertyValueFactory<>("reason"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Cột Loại Yêu Cầu: Kiểm tra kiểu đối tượng để hiển thị tên
        colType.setCellValueFactory(cellData -> {
            ApprovalRequest req = cellData.getValue();
            if (req instanceof InvoiceEditRequest) {
                return new SimpleStringProperty("Sửa Hóa Đơn");
            } else if (req instanceof BookingCancelRequest) {
                return new SimpleStringProperty("Hủy Đặt Phòng");
            }
            return new SimpleStringProperty("Khác");
        });

        // Cột Chi tiết: Hiển thị thông tin riêng biệt tùy loại
        colDetail.setCellValueFactory(cellData -> {
            ApprovalRequest req = cellData.getValue();
            if (req instanceof InvoiceEditRequest) {
                double amount = ((InvoiceEditRequest) req).getNewAmount();
                return new SimpleStringProperty("Mới: " + String.format("%,.0f", amount));
            } else if (req instanceof BookingCancelRequest) {
                BookingCancelRequest bcr = (BookingCancelRequest) req;
                return new SimpleStringProperty("Hoàn: " + String.format("%,.0f", bcr.getRefundAmount()));
            }
            return new SimpleStringProperty("-");
        });
    }

    @FXML
    private void handleRefresh() {
        loadData();
    }

    private void loadData() {
        // Load danh sách từ Repository (hoặc thông qua Ctrl nếu Ctrl có hàm get)
        // Vì code ApprovalCtrl bạn gửi không có hàm getter list, ta gọi trực tiếp Repo tại đây
        List<InvoiceEditRequest> invoiceReqs = InvoiceEditRequestRepository.loadFromFile();
        List<BookingCancelRequest> bookingReqs = BookingCancelRequestRepository.loadFromFile();

        ObservableList<ApprovalRequest> list = FXCollections.observableArrayList();
        
        // Chỉ hiện các yêu cầu PENDING (Chờ duyệt)
        for (InvoiceEditRequest r : invoiceReqs) {
            if ("PENDING".equalsIgnoreCase(r.getStatus())) list.add(r);
        }
        for (BookingCancelRequest r : bookingReqs) {
            if ("PENDING".equalsIgnoreCase(r.getStatus())) list.add(r);
        }

        tableRequests.setItems(list);
    }

    @FXML
    private void handleApprove() {
        ApprovalRequest selected = tableRequests.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Lỗi", "Vui lòng chọn một yêu cầu để duyệt.");
            return;
        }

        // Gọi hàm approve tương ứng trong ApprovalCtrl
        if (selected instanceof InvoiceEditRequest) {
            approvalCtrl.approveInvoiceEdit(selected.getRequestId(), CURRENT_MANAGER_ID);
        } else if (selected instanceof BookingCancelRequest) {
            approvalCtrl.approveBookingCancel(selected.getRequestId(), CURRENT_MANAGER_ID);
        }

        showAlert("Thành công", "Đã duyệt yêu cầu thành công!");
        loadData(); // Refresh lại bảng
    }

    @FXML
    private void handleReject() {
        ApprovalRequest selected = tableRequests.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Lỗi", "Vui lòng chọn một yêu cầu để từ chối.");
            return;
        }

        // Gọi hàm reject trong ApprovalCtrl
        // Lưu ý: Code ApprovalCtrl của bạn cần ManagerNote, ở đây tôi hardcode mẫu
        // Thực tế bạn nên hiện 1 Dialog nhỏ để Manager nhập lý do từ chối.
        boolean result = approvalCtrl.reject(selected.getRequestId(), CURRENT_MANAGER_ID, "Manager rejected via GUI");

        if (result) {
            showAlert("Thành công", "Đã từ chối yêu cầu.");
            loadData();
        } else {
            showAlert("Lỗi", "Không thể từ chối yêu cầu này.");
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