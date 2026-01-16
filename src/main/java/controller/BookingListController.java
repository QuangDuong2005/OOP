package controller;

import ctrl.BookingCtrl;
import java.io.IOException;
import java.util.Optional;
import javafx.beans.property.SimpleObjectProperty;
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
import javafx.scene.control.TextInputDialog;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Booking;
import util.UserSession;

public class BookingListController {

    @FXML private TableView<Booking> tableBooking;
    @FXML private TableColumn<Booking, String> colID;
    @FXML private TableColumn<Booking, String> colCusID;
    @FXML private TableColumn<Booking, String> colTourID;
    @FXML private TableColumn<Booking, Integer> colPeople;
    @FXML private TableColumn<Booking, Double> colTotal;
    @FXML private TableColumn<Booking, String> colStatus; 

    private BookingCtrl bookingCtrl;
    private ObservableList<Booking> bookingList;
    
    private String currentStaffId;
    public void initialize() {
        bookingCtrl = new BookingCtrl();
        bookingList = FXCollections.observableArrayList();
        if (UserSession.getSession() != null) {
            // Giả sử model Account của bạn có hàm getUsername() hoặc getID()
            // Hãy thay .getUsername() bằng hàm get ID thực tế trong model Account của bạn
            this.currentStaffId = UserSession.getSession().getAccount().getUsername(); 
        } else {
            // Trường hợp chạy test mà chưa qua Login thì fallback về mặc định
            this.currentStaffId = "UNKNOWN"; 
        }
        // --- 1. Cấu hình các cột ---
        colID.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getBookingID()));
        colCusID.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCustomerID()));
        colTourID.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTourID()));
        colPeople.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getNumberOfPeople()));
        colTotal.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getTotalAmount()));
        
        // Cột trạng thái: Hiển thị "Chờ hủy" nếu đã gửi yêu cầu, ngược lại là "Đã đặt"
        colStatus.setCellValueFactory(cell -> {
            boolean isCancel = cell.getValue().getIsCancelled(); 
            return new SimpleStringProperty(isCancel ? "Đã gửi Hủy/Chờ duyệt" : "Đã đặt");
        });

        tableBooking.setItems(bookingList);
        loadData();
    }

    private void loadData() {
        // Lấy danh sách từ BookingCtrl và đổ vào bảng
        // LƯU Ý: Bạn cần thêm hàm getBookings() vào file BookingCtrl.java như tôi đã nhắc ở bước trước
        try {
             bookingList.setAll(bookingCtrl.getBookings());
             tableBooking.refresh();
        } catch (Exception e) {
            e.printStackTrace(); // In lỗi ra console để debug nếu có
        }
    }

    @FXML
    void handleAdd(ActionEvent event) {
        showDialog(null); // null = chế độ thêm mới
    }

    @FXML
    void handleEdit(ActionEvent event) {
        Booking selected = tableBooking.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Thông báo", "Vui lòng chọn đơn hàng để sửa", Alert.AlertType.WARNING);
            return;
        }
        if (selected.getIsCancelled()) {
            showAlert("Cảnh báo", "Đơn hàng này đang chờ hủy, không thể sửa!", Alert.AlertType.ERROR);
            return;
        }
        showDialog(selected); // Chế độ sửa
    }

    // --- XỬ LÝ GỬI YÊU CẦU HỦY ---
    @FXML
    void handleCancelRequest(ActionEvent event) {
        Booking selected = tableBooking.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Thông báo", "Vui lòng chọn đơn hàng cần hủy", Alert.AlertType.WARNING);
            return;
        }
        
        if (selected.getIsCancelled()) {
            showAlert("Thông báo", "Đơn này đã được gửi yêu cầu hủy trước đó.", Alert.AlertType.INFORMATION);
            return;
        }

        // Hiện hộp thoại hỏi lý do hủy
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Yêu cầu hủy Booking");
        dialog.setHeaderText("Gửi yêu cầu hủy cho Quản lý");
        dialog.setContentText("Nhập lý do hủy:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(reason -> {
            if (reason.trim().isEmpty()) {
                showAlert("Lỗi", "Phải nhập lý do để quản lý duyệt!", Alert.AlertType.ERROR);
                return;
            }

            try {
                // Gọi hàm xử lý nghiệp vụ hủy trong BookingCtrl
                bookingCtrl.cancelBooking(selected.getBookingID(), selected.getTourID(), reason);
                
                showAlert("Thành công", "Đã gửi yêu cầu hủy thành công!", Alert.AlertType.INFORMATION);
                loadData(); // Refresh lại bảng để cập nhật trạng thái
                
            } catch (IllegalStateException e) {
                showAlert("Không thể hủy", e.getMessage(), Alert.AlertType.ERROR);
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Lỗi hệ thống", e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    // Hàm mở cửa sổ nhập liệu (Dialog)
    private void showDialog(Booking booking) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/BookingDialog.fxml"));
            Parent root = loader.load();
            
            BookingDialogController dialogCtrl = loader.getController();
            dialogCtrl.setBookingCtrl(bookingCtrl);
            dialogCtrl.setCurrentStaffId(currentStaffId);
            
            // Nếu là sửa, truyền dữ liệu cũ vào
            if (booking != null) {
                dialogCtrl.setEditData(booking);
            }

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL); // Chặn cửa sổ chính cho đến khi đóng dialog
            stage.setTitle(booking == null ? "Tạo Booking Mới" : "Cập Nhật Booking");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            
            loadData(); // Load lại bảng sau khi đóng dialog
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể mở form nhập liệu: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}