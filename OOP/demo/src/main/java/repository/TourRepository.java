package repository;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import model.Tour;
public class TourRepository {
    // file json lưu trữ Tour
    private static final String FILE_PATH = "demo/src/main/resources/data/Tour.json";
    //  phương thức để lưu Tour vào file JSON
    public static void saveToursToFile(List<Tour> tours) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT); // Định dạng đẹp
        try {
            mapper.writeValue(new File(FILE_PATH), tours);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
    //  phương thức để đọc Tour từ file JSON
    public static List<Tour> loadToursFromFile() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            File file = new File(FILE_PATH);
            return mapper.readValue(file, mapper.getTypeFactory().constructCollectionType(List.class, Tour.class));
        } catch (IOException e) {
            System.err.println("Error loading tours from file");
        }
        return new ArrayList<>();
    }
    // Ghi thêm Tour vào file JSON
    public static void appendTourToFile(Tour newTour) {
        List<Tour> tours = loadToursFromFile();
        tours.add(newTour);
        saveToursToFile(tours);
    }
}
