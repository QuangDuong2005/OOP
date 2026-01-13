package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Tour;
import repository.TourRepository;

public class TourController implements Initializable {

    @FXML private TableView<Tour> tourTable;
    
    // Khai báo các cột khớp với fxml
    @FXML private TableColumn<Tour, String> colId;
    @FXML private TableColumn<Tour, String> colDescription; // Dùng cột Tên cũ để hiện Mô tả
    @FXML private TableColumn<Tour, String> colDeparture;   // Cột điểm đi (cần thêm vào FXML nếu chưa có)
    @FXML private TableColumn<Tour, Double> colPrice;
    @FXML private TableColumn<Tour, Integer> colCapacity;
    @FXML private TableColumn<Tour, String> colStart;
    @FXML private TableColumn<Tour, String> colEnd;
    @FXML private TableColumn<Tour, String> colStatus;      // Cột trạng thái
    
    @FXML private TextField txtSearch;

    private ObservableList<Tour> tourList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Cấu hình cột (Map với tên thuộc tính trong Model Tour)
        // Lưu ý: Chuỗi trong ngoặc phải khớp với phần sau chữ "get" của Model
        // Ví dụ: getTourID -> "tourID", getPricePerPerson -> "pricePerPerson"
        
        colId.setCellValueFactory(new PropertyValueFactory<>("tourID"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colDeparture.setCellValueFactory(new PropertyValueFactory<>("departurePoint"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("pricePerPerson"));
        colCapacity.setCellValueFactory(new PropertyValueFactory<>("maxPeople"));
        colStart.setCellValueFactory(new PropertyValueFactory<>("startDate")); // Getter trả về String
        colEnd.setCellValueFactory(new PropertyValueFactory<>("endDate"));     // Getter trả về String
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Định dạng hiển thị tiền tệ
        colPrice.setCellFactory(tc -> new TableCell<Tour, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("%,.0f VNĐ", price));
                }
            }
        });
        
        // Định dạng hiển thị Trạng thái (đổi màu chữ)
        colStatus.setCellFactory(tc -> new TableCell<Tour, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equals("Đang diễn ra")) {
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    } else if (item.equals("Đã kết thúc")) {
                        setStyle("-fx-text-fill: red;");
                    } else {
                        setStyle("-fx-text-fill: blue;");
                    }
                }
            }
        });

        // 2. Load dữ liệu
        loadData();
        setupSearch();
    }

    public void loadData() {
        tourList.clear();
        tourList.addAll(TourRepository.loadToursFromFile());
        tourTable.setItems(tourList);
        tourTable.refresh();
    }

    private void setupSearch() {
        FilteredList<Tour> filteredData = new FilteredList<>(tourList, p -> true);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(tour -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();

                // Tìm theo ID, Mô tả hoặc Điểm đi
                if (tour.getTourID().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (tour.getDescription().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (tour.getDeparturePoint().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });
        tourTable.setItems(filteredData);
    }

    @FXML
    private void handleAdd(ActionEvent event) {
        openDialog(null);
    }

    @FXML
    private void handleEdit(ActionEvent event) {
        Tour selected = tourTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            openDialog(selected);
        } else {
            showAlert(Alert.AlertType.WARNING, "Chưa chọn Tour", "Vui lòng chọn tour cần sửa!");
        }
    }


    private void openDialog(Tour tour) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TourDialog.fxml"));
            Parent root = loader.load();

            TourDialogController ctrl = loader.getController();
            if (tour != null) {
                ctrl.setEditData(tour);
            }

            Stage stage = new Stage();
            stage.setTitle(tour == null ? "Thêm Tour Mới" : "Cập Nhật Tour");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            loadData(); // Load lại sau khi đóng dialog

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể mở form: " + e.getMessage());
        }
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}