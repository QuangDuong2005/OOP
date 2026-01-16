package repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import model.InvoiceEditRequest;
import util.DataStore;
import util.JsonMapperFactory;

public class InvoiceEditRequestRepository {

    private static final String FILE_NAME = "InvoiceEditRequest.json";

    public static void saveToFile(List<InvoiceEditRequest> requests) {
        ObjectMapper mapper = JsonMapperFactory.create();
        try {
            mapper.writeValue(DataStore.getDataFile(FILE_NAME), requests);
        } catch (IOException e) {
            System.err.println("Error saving InvoiceEditRequests: " + e.getMessage());
        }
    }

    public static List<InvoiceEditRequest> loadFromFile() {
        ObjectMapper mapper = JsonMapperFactory.create();
        try {
            File file = DataStore.getDataFile(FILE_NAME);
            List<InvoiceEditRequest> list = mapper.readValue(
                    file,
                    mapper.getTypeFactory().constructCollectionType(List.class, InvoiceEditRequest.class)
            );
            return list;
        } catch (IOException e) {
            System.err.println("Error loading InvoiceEditRequests: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
