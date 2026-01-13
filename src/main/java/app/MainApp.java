package app;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        try {
            // 1. Chỉ đường dẫn đến file FXML (Nằm trong resources/view/)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Login.fxml"));
            
            // 2. Tải giao diện lên bộ nhớ
            Parent root = loader.load();
            
            // 3. Tạo một Scene (Cảnh) chứa giao diện đó
            Scene scene = new Scene(root);
            
            // 4. Cấu hình Cửa sổ chính (Stage)
            stage.setTitle("Hệ thống Quản lý Tour Du lịch");
            stage.setScene(scene);
            stage.setResizable(false); // Không cho phép user kéo giãn cửa sổ (tùy chọn)
            stage.show(); // Hiển thị lên màn hình

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("LỖI: Không tìm thấy file Login.fxml hoặc lỗi code trong Controller!");
            System.err.println("Vui lòng kiểm tra lại thư mục: src/main/resources/view/Login.fxml");
        }
    }

    public static void main(String[] args) {
        launch(args); // Lệnh bắt buộc để khởi động JavaFX
    }
}