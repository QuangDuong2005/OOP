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
            syncAutoIncrementCounter(bookings);
            return bookings;
        } catch (IOException e) {
            System.err.println("Error loading bookings from file: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static void appendBookingToFile(Booking newBooking) {
        List<Booking> bookings = loadBookingsFromFile();
        bookings.add(newBooking);
        saveBookingsToFile(bookings);
    }

    private static void syncAutoIncrementCounter(List<Booking> bookings) {
        int max = 0;
        for (Booking b : bookings) {
            if (b == null || b.bookingID == null) continue;
            try {
                String digits = b.bookingID.replaceAll("\\D", "");
                if (!digits.isEmpty()) max = Math.max(max, Integer.parseInt(digits));
            } catch (Exception ignore) {}
        }
        Booking.numberBookings = Math.max(Booking.numberBookings, max);
    }
}