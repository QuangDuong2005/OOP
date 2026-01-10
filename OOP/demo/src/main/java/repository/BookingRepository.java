package repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import model.Booking;
public class BookingRepository {
    // file json lưu trữ Booking
    private static final String FILE_PATH = "demo/src/main/resources/data/Booking.json";
    // Tạo hàm để lưu danh sách Booking vào file JSON
    public static void saveBookingsToFile(List<Booking> bookings) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT); // Định dạng đẹp
        try {
            mapper.writeValue(new File(FILE_PATH), bookings);
        } catch (IOException e) {
            System.err.println("Error saving bookings to file");
        }
    }
    // Tạo hàm để đọc danh sách Booking từ file JSON
    public static List<Booking> loadBookingsFromFile() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            File file = new File(FILE_PATH);
            return mapper.readValue(file, mapper.getTypeFactory().constructCollectionType(List.class, Booking.class));
        } catch (IOException e) {
            System.err.println("Error loading bookings from file");
            return new ArrayList<>();
        }
    }
    // Ghi thêm Booking vào file JSON
    public static void appendBookingToFile(Booking newBooking) {
        List<Booking> bookings = loadBookingsFromFile();
        bookings.add(newBooking);
        saveBookingsToFile(bookings);
    }   
}