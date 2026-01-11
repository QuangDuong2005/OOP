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
            syncAutoIncrementCounter(reports);
            return reports;
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public static void appendToFile(Report r) {
        List<Report> reports = loadFromFile();
        reports.add(r);
        saveToFile(reports);
    }

    private static void syncAutoIncrementCounter(List<Report> reports) {
        int max = 0;
        for (Report r : reports) {
            if (r == null || r.reportId == null) continue;
            try {
                String digits = r.reportId.replaceAll("\\D", "");
                if (!digits.isEmpty()) max = Math.max(max, Integer.parseInt(digits));
            } catch (Exception ignore) {}
        }
        Report.numberReports = Math.max(Report.numberReports, max);
    }
}
