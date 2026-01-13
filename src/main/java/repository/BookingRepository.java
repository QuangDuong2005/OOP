package repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import model.Booking;
import util.DataStore;
import util.JsonMapperFactory;

public class BookingRepository {
    private static final String FILE_NAME = "Booking.json";

    public static void saveBookingsToFile(List<Booking> bookings) {
        ObjectMapper mapper = JsonMapperFactory.create();
        try {
            mapper.writeValue(DataStore.getDataFile(FILE_NAME), bookings);
        } catch (IOException e) {
            System.err.println("Error saving bookings to file: " + e.getMessage());
        }
    }

    public static List<Booking> loadBookingsFromFile() {
        ObjectMapper mapper = JsonMapperFactory.create();
        try {
            File file = DataStore.getDataFile(FILE_NAME);
            List<Booking> bookings = mapper.readValue(file,
                    mapper.getTypeFactory().constructCollectionType(List.class, Booking.class));
            return bookings;
        } catch (IOException e) {
            System.err.println("Error loading bookings from file: " + e.getMessage());
            return new ArrayList<>();
        }
    }


}