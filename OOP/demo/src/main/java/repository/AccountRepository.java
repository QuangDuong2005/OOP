package repository;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import model.Account;
public class AccountRepository {
    // file json lưu trữ Account
    private static final String FILE_PATH = "demo/src/main/resources/data/Account.json";
    //  phương thức để lưu Account vào file JSON
    public static void saveAccountsToFile(List<Account> accounts) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT); // Định dạng đẹp
        try {
            mapper.writeValue(new File(FILE_PATH), accounts);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
    //  phương thức để đọc Account từ file JSON
    public static List<Account> loadAccountsFromFile() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            File file = new File(FILE_PATH);
            return mapper.readValue(file, mapper.getTypeFactory().constructCollectionType(List.class, Account.class));
        } catch (IOException e) {
            System.err.println("Error loading accounts from file");
            System.err.println("LỖI ĐỌC FILE ACCOUNT: " + e.getMessage());
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
    // Ghi thêm Account vào file JSON
    public static void appendAccountToFile(Account newAccount) {
        List<Account> accounts = loadAccountsFromFile();
        accounts.add(newAccount);
        saveAccountsToFile(accounts);
    }
}
