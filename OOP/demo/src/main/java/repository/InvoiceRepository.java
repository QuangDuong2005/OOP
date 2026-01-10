package repository;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import model.Invoice;
public class InvoiceRepository {
    // file json lưu trữ Invoice
    private static final String FILE_PATH = "demo/src/main/resources/data/Invoice.json";
    //  phương thức để lưu Invoice vào file JSON
    public static void saveInvoicesToFile(List<Invoice> invoices) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT); // Định dạng đẹp
        try {
            mapper.writeValue(new File(FILE_PATH), invoices);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
    //  phương thức để đọc Invoice từ file JSON
    public static List<Invoice> loadInvoicesFromFile() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            File file = new File(FILE_PATH);
            return mapper.readValue(file, mapper.getTypeFactory().constructCollectionType(List.class, Invoice.class));
        } catch (IOException e) {
            System.err.println("Error loading invoices from file");
        }
        return new ArrayList<>();
    }
    // Ghi thêm Invoice vào file JSON
    public static void appendInvoiceToFile(Invoice newInvoice) {
        List<Invoice> invoices = loadInvoicesFromFile();
        invoices.add(newInvoice);
        saveInvoicesToFile(invoices);
    }
}