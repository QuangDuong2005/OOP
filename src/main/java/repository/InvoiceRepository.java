package repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import model.Invoice;
import util.DataStore;
import util.JsonMapperFactory;

public class InvoiceRepository {
    private static final String FILE_NAME = "Invoice.json";

    public static void saveInvoicesToFile(List<Invoice> invoices){
        ObjectMapper mapper = JsonMapperFactory.create();
        try {
            mapper.writeValue(DataStore.getDataFile(FILE_NAME), invoices);
        } catch (IOException e) {
            System.err.println("Error saving invoices to file: " + e.getMessage());
        }
    }

    public static List<Invoice> loadInvoicesFromFile() {
        ObjectMapper mapper = JsonMapperFactory.create();
        try {
            File file = DataStore.getDataFile(FILE_NAME);
            List<Invoice> invoices = mapper.readValue(file,
                    mapper.getTypeFactory().constructCollectionType(List.class, Invoice.class));
            return invoices;
        } catch (IOException e) {
            System.err.println("Error loading invoices from file: " + e.getMessage());
            return new ArrayList<>();
        }
    }

}