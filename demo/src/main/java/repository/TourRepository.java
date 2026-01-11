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
            syncAutoIncrementCounter(tours);
            return tours;
        } catch (IOException e) {
            System.err.println("Error loading tours from file: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static void appendTourToFile(Tour newTour) {
        List<Tour> tours = loadToursFromFile();
        tours.add(newTour);
        saveToursToFile(tours);
    }

    private static void syncAutoIncrementCounter(List<Tour> tours) {
        int max = 0;
        for (Tour t : tours) {
            if (t == null || t.tourID == null) continue;
            try {
                String digits = t.tourID.replaceAll("\\D", "");
                if (!digits.isEmpty()) max = Math.max(max, Integer.parseInt(digits));
            } catch (Exception ignore) {}
        }
        Tour.numberTours = Math.max(Tour.numberTours, max);
    }
}
