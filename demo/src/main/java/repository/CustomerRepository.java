package repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import model.Customer;
import util.DataStore;
import util.JsonMapperFactory;

public class CustomerRepository {
    private static final String FILE_NAME = "Customer.json";

    public static void saveCustomersToFile(List<Customer> customers) {
        ObjectMapper mapper = JsonMapperFactory.create();
        try {
            mapper.writeValue(DataStore.getDataFile(FILE_NAME), customers);
        } catch (IOException e) {
            System.err.println("Error saving customers to file: " + e.getMessage());
        }
    }

    public static List<Customer> loadCustomersFromFile() {
        ObjectMapper mapper = JsonMapperFactory.create();
        try {
            File file = DataStore.getDataFile(FILE_NAME);
            List<Customer> customers = mapper.readValue(file,
                    mapper.getTypeFactory().constructCollectionType(List.class, Customer.class));
            return customers;
        } catch (IOException e) {
            System.err.println("Error loading customers from file: " + e.getMessage());
            return new ArrayList<>();
        }
    }

}
