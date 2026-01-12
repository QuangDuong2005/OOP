package repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import model.BookingCancelRequest;
import util.DataStore;
import util.JsonMapperFactory;

public class BookingCancelRequestRepository {

    private static final String FILE_NAME = "BookingCancelRequest.json";

    public static void saveToFile(List<BookingCancelRequest> requests) {
        ObjectMapper mapper = JsonMapperFactory.create();
        try {
            mapper.writeValue(DataStore.getDataFile(FILE_NAME), requests);
        } catch (IOException e) {
            System.err.println("Error saving BookingCancelRequests: " + e.getMessage());
        }
    }

    public static List<BookingCancelRequest> loadFromFile() {
        ObjectMapper mapper = JsonMapperFactory.create();
        try {
            File file = DataStore.getDataFile(FILE_NAME);
            List<BookingCancelRequest> list = mapper.readValue(
                    file,
                    mapper.getTypeFactory().constructCollectionType(List.class, BookingCancelRequest.class)
            );
            return list;
        } catch (IOException e) {
            System.err.println("Error loading BookingCancelRequests: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
