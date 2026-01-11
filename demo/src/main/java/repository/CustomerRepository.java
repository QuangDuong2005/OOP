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
            syncAutoIncrementCounter(customers);
            return customers;
        } catch (IOException e) {
            System.err.println("Error loading customers from file: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static void appendCustomerToFile(Customer newCustomer) {
        List<Customer> customers = loadCustomersFromFile();
        customers.add(newCustomer);
        saveCustomersToFile(customers);
    }

    private static void syncAutoIncrementCounter(List<Customer> customers) {
        int max = 0;
        for (Customer c : customers) {
            if (c == null || c.customerID == null) continue;
            try {
                String digits = c.customerID.replaceAll("\\D", "");
                if (!digits.isEmpty()) max = Math.max(max, Integer.parseInt(digits));
            } catch (Exception ignore) {}
        }
        Customer.numberCustomers = Math.max(Customer.numberCustomers, max);
    }
}
