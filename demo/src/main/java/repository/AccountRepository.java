package repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import model.Account;
import util.DataStore;
import util.JsonMapperFactory;

public class AccountRepository {
    private static final String FILE_NAME = "Account.json";

    public static void saveAccountsToFile(List<Account> accounts) {
        ObjectMapper mapper = JsonMapperFactory.create();
        try {
            mapper.writeValue(DataStore.getDataFile(FILE_NAME), accounts);
        } catch (IOException e) {
            System.err.println("Error saving accounts to file: " + e.getMessage());
        }
    }

    public static List<Account> loadAccountsFromFile() {
        ObjectMapper mapper = JsonMapperFactory.create();
        try {
            File file = DataStore.getDataFile(FILE_NAME);
            List<Account> accounts = mapper.readValue(file,
                    mapper.getTypeFactory().constructCollectionType(List.class, Account.class));
            syncAutoIncrementCounter(accounts);
            return accounts;
        } catch (IOException e) {
            System.err.println("Error loading accounts from file: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static void appendAccountToFile(Account newAccount) {
        List<Account> accounts = loadAccountsFromFile();
        accounts.add(newAccount);
        saveAccountsToFile(accounts);
    }

    private static void syncAutoIncrementCounter(List<Account> accounts) {
        int max = 0;
        for (Account a : accounts) {
            if (a == null || a.accountID == null) continue;
            try {
                String digits = a.accountID.replaceAll("\\D", "");
                if (!digits.isEmpty()) max = Math.max(max, Integer.parseInt(digits));
            } catch (Exception ignore) {}
        }
        Account.numberAccounts = Math.max(Account.numberAccounts, max);
    }
}
