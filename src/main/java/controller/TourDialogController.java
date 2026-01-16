package controller;

import ctrl.TourCtrl;
import exception.DuplicateException;
import exception.InvalidDateException;
import exception.TourAlreadyBookedException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Tour;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class TourDialogController implements Initializable {

    @FXML private TextField txtId;
    @FXML private TextArea txtDescription; // Dùng TextArea cho Mô tả
    @FXML private TextField txtDeparture;
    @FXML private TextField txtPrice;
    @FXML private TextField txtCapacity;
    @FXML private DatePicker dpStart;
    @FXML private DatePicker dpEnd;
    
    @FXML private Button btnSave;
    @FXML private Button btnCancel;

    private TourCtrl tourCtrl;
    private Tour currentTour; 

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tourCtrl = new TourCtrl();
    }

    // Đổ dữ liệu vào form khi bấm Sửa
    public void setEditData(Tour tour) {
        this.currentTour = tour;
        
        txtId.setText(tour.getTourID());
        txtId.setDisable(true); // Khóa ID
        
        txtDescription.setText(tour.getDescription());
        txtDeparture.setText(tour.getDeparturePoint());
        txtPrice.setText(String.valueOf(tour.getPricePerPerson())); 
        txtCapacity.setText(String.valueOf(tour.getMaxPeople()));

        // Chuyển String ngày (từ model) sang LocalDate (cho DatePicker)
        if (tour.getStartDate() != null) dpStart.setValue(LocalDate.parse(tour.getStartDate()));
        if (tour.getEndDate() != null) dpEnd.setValue(LocalDate.parse(tour.getEndDate()));
        
        btnSave.setText("Cập nhật");
    }

    @FXML
    private void handleSave() {
        // 1. Validate sơ bộ
        if (txtId.getText().isEmpty() || txtDescription.getText().isEmpty() || 
            dpStart.getValue() == null || dpEnd.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Thiếu thông tin", "Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        try {
            String id = txtId.getText().trim();
            String desc = txtDescription.getText().trim();
            String departure = txtDeparture.getText().trim();
            
            // Parse số
            double price = Double.parseDouble(txtPrice.getText().trim());
            int maxPeople = Integer.parseInt(txtCapacity.getText().trim());
            
            // Lấy String ngày
            String startDateStr = dpStart.getValue().toString();
            String endDateStr = dpEnd.getValue().toString();

            if (currentTour == null) {
                // --- THÊM MỚI ---
                // Lưu ý: Bạn cần sửa lại hàm createTour trong TourCtrl để bỏ tham số 'tourName' 
                // vì trong Model Tour không có tourName. 
                // Ở đây tôi giả định bạn đã bỏ tham số cuối cùng đó.
                
                tourCtrl.createTour(id, desc, departure, price, maxPeople, startDateStr, endDateStr); 
                
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã thêm tour mới.");
            } else {
                // --- CẬP NHẬT ---
                int bookedPeople = currentTour.getBookedPeople();
                
                tourCtrl.updateTourInfo(id, desc, departure, price, maxPeople, startDateStr, endDateStr, bookedPeople);
                
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã cập nhật tour.");
            }
            
            closeWindow();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi nhập liệu", "Giá và Số chỗ phải là số.");
        } catch (DuplicateException | InvalidDateException | TourAlreadyBookedException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi nghiệp vụ", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi hệ thống", e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}