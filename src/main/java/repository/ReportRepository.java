package repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import model.Report;
import util.DataStore;
import util.JsonMapperFactory;

public class ReportRepository {
    private static final String FILE_NAME = "Report.json";

    public static void saveToFile(List<Report> reports) {
        ObjectMapper mapper = JsonMapperFactory.create();
        try {
            mapper.writeValue(DataStore.getDataFile(FILE_NAME), reports);
        } catch (IOException e) {
            System.err.println("Error saving reports: " + e.getMessage());
        }
    }

    public static List<Report> loadFromFile() {
        ObjectMapper mapper = JsonMapperFactory.create();
        try {
            File file = DataStore.getDataFile(FILE_NAME);
            List<Report> reports = mapper.readValue(file,
                    mapper.getTypeFactory().constructCollectionType(List.class, Report.class));
            return reports;
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

}
