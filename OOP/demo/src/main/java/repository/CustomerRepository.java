package repository;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import model.Customer;
public class CustomerRepository {
    private static final String FILE_PATH = "demo/src/main/resources/data/Customer.json";
    //  phương thức để lưu Customer vào file JSON
    public static void saveCustomersToFile(List<Customer> customers) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT); // Định dạng đẹp
        try {
            mapper.writeValue(new File(FILE_PATH), customers);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
    //  phương thức để đọc Customer từ file JSON
    public static List<Customer> loadCustomersFromFile() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            File file = new File(FILE_PATH);
            return mapper.readValue(file, mapper.getTypeFactory().constructCollectionType(List.class, Customer.class));
        } catch (IOException e) {
            System.err.println("Error loading customers from file");
        }
        return new ArrayList<>();
    }
    // Ghi thêm Customer vào file JSON
    public static void appendCustomerToFile(Customer newCustomer) {
        List<Customer> customers = loadCustomersFromFile();
        customers.add(newCustomer);
        saveCustomersToFile(customers);
    }
}
