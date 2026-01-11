package repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import model.ApprovalRequest;
import util.DataStore;
import util.JsonMapperFactory;

public class ApprovalRequestRepository {
    private static final String FILE_NAME = "ApprovalRequest.json";

    public static void saveToFile(List<ApprovalRequest> requests) {
        ObjectMapper mapper = JsonMapperFactory.create();
        try {
            mapper.writeValue(DataStore.getDataFile(FILE_NAME), requests);
        } catch (IOException e) {
            System.err.println("Error saving approval requests: " + e.getMessage());
        }
    }

    public static List<ApprovalRequest> loadFromFile() {
        ObjectMapper mapper = JsonMapperFactory.create();
        try {
            File file = DataStore.getDataFile(FILE_NAME);
            List<ApprovalRequest> requests = mapper.readValue(file,
                    mapper.getTypeFactory().constructCollectionType(List.class, ApprovalRequest.class));
            syncAutoIncrementCounter(requests);
            return requests;
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public static void appendToFile(ApprovalRequest r) {
        List<ApprovalRequest> requests = loadFromFile();
        requests.add(r);
        saveToFile(requests);
    }

    private static void syncAutoIncrementCounter(List<ApprovalRequest> requests) {
        int max = 0;
        for (ApprovalRequest r : requests) {
            if (r == null || r.requestId == null) continue;
            try {
                String digits = r.requestId.replaceAll("\\D", "");
                if (!digits.isEmpty()) max = Math.max(max, Integer.parseInt(digits));
            } catch (Exception ignore) {}
        }
        ApprovalRequest.numberRequests = Math.max(ApprovalRequest.numberRequests, max);
    }
}
