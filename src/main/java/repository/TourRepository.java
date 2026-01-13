package repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import model.Tour;
import util.DataStore;
import util.JsonMapperFactory;

public class TourRepository {
    private static final String FILE_NAME = "Tour.json";

    public static void saveToursToFile(List<Tour> tours) {
        ObjectMapper mapper = JsonMapperFactory.create();
        try {
            mapper.writeValue(DataStore.getDataFile(FILE_NAME), tours);
        } catch (IOException e) {
            System.err.println("Error saving tours to file: " + e.getMessage());
        }
    }

    public static List<Tour> loadToursFromFile() {
        ObjectMapper mapper = JsonMapperFactory.create();
        try {
            File file = DataStore.getDataFile(FILE_NAME);
            List<Tour> tours = mapper.readValue(file,
                    mapper.getTypeFactory().constructCollectionType(List.class, Tour.class));
            return tours;
        } catch (IOException e) {
            System.err.println("Error loading tours from file: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
