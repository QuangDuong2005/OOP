package controller;

import ctrl.BookingCtrl;
import exception.DuplicateException;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Booking;
import model.Customer; // Nhớ import repository khách hàng của bạn
import model.Tour;
import repository.CustomerRepository;
import repository.TourRepository;
public class BookingDialogController {

    @FXML private TextField txtBookingID;
    @FXML private ComboBox<String> cbCustomerID; 
    @FXML private ComboBox<String> cbTourID;     
    @FXML private TextField txtNumPeople;
    @FXML private Label lblError;
    @FXML private Label lblTitle;

    private BookingCtrl bookingCtrl;
    private boolean isEditMode = false;
    private String currentStaffID;
    
    public void setBookingCtrl(BookingCtrl ctrl) {
        this.bookingCtrl = ctrl;
    }
    
    public void setCurrentStaffId(String id) {
        this.currentStaffID = id;
    }

    public void initialize() {
        List<Tour> tours = TourRepository.loadToursFromFile();
        for(Tour t : tours) {
            cbTourID.getItems().add(t.getTourID());
        }
        List<Customer> customers = CustomerRepository.loadCustomersFromFile();
        for(Customer t : customers) {
            cbCustomerID.getItems().add(t.getCustomerID());
        }
    }

    // Hàm gọi khi bấm nút Sửa: điền dữ liệu cũ vào form
    public void setEditData(Booking booking) {
        this.isEditMode = true;
        lblTitle.setText("CẬP NHẬT ĐƠN HÀNG");
        
        txtBookingID.setText(booking.getBookingID());
        txtBookingID.setDisable(true); // Không cho sửa ID
        
        cbCustomerID.setValue(booking.getCustomerID());
        cbCustomerID.setDisable(true); // Logic của bạn: không đổi khách khi sửa
        
        cbTourID.setValue(booking.getTourID());
        cbTourID.setDisable(true); // Logic của bạn: Update số người trên tour cũ
        
        txtNumPeople.setText(String.valueOf(booking.getNumberOfPeople()));
    }

    @FXML
    void handleSave(ActionEvent event) {
        String bId = txtBookingID.getText().trim();
        String cId = cbCustomerID.getValue();
        String tId = cbTourID.getValue();
        String numStr = txtNumPeople.getText().trim();

        if (bId.isEmpty() || cId == null || tId == null || numStr.isEmpty()) {
            lblError.setText("Vui lòng nhập đủ thông tin!");
            return;
        }

        try {
            int numPeople = Integer.parseInt(numStr);

            if (isEditMode) {
                // Gọi hàm Update số lượng người
                bookingCtrl.updateBookingPeople(bId, tId, numPeople);
            } else {
                // Gọi hàm Tạo mới
                bookingCtrl.createBooking(bId, cId, tId, numPeople, currentStaffID);
            }
            
            closeDialog(); // Đóng cửa sổ sau khi lưu thành công
            
        } catch (NumberFormatException e) {
            lblError.setText("Số người phải là số nguyên!");
        } catch (DuplicateException e) {
            lblError.setText("Lỗi: " + e.getMessage());
        } catch (IllegalStateException | IllegalArgumentException e) {
            lblError.setText("Lỗi nghiệp vụ: " + e.getMessage());
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
        Stage stage = (Stage) txtBookingID.getScene().getWindow();
        stage.close();
    }
}