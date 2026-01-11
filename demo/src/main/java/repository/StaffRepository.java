package repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import model.Staff;
import util.DataStore;
import util.JsonMapperFactory;

public class StaffRepository {
    private static final String FILE_NAME = "Staff.json";

    public static void saveStaffsToFile(List<Staff> staffs) {
        ObjectMapper mapper = JsonMapperFactory.create();
        try {
            mapper.writeValue(DataStore.getDataFile(FILE_NAME), staffs);
        } catch (IOException e) {
            System.err.println("Error saving staffs to file: " + e.getMessage());
        }
    }

    public static List<Staff> loadStaffsFromFile() {
        ObjectMapper mapper = JsonMapperFactory.create();
        try {
            File file = DataStore.getDataFile(FILE_NAME);
            List<Staff> staffs = mapper.readValue(file,
                    mapper.getTypeFactory().constructCollectionType(List.class, Staff.class));
            syncAutoIncrementCounter(staffs);
            return staffs;
        } catch (IOException e) {
            System.err.println("Error loading staffs from file: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static void appendStaffToFile(Staff newStaff) {
        List<Staff> staffs = loadStaffsFromFile();
        staffs.add(newStaff);
        saveStaffsToFile(staffs);
    }

    private static void syncAutoIncrementCounter(List<Staff> staffs) {
        int max = 0;
        for (Staff s : staffs) {
            if (s == null || s.staffID == null) continue;
            try {
                String digits = s.staffID.replaceAll("\\D", "");
                if (!digits.isEmpty()) max = Math.max(max, Integer.parseInt(digits));
            } catch (Exception ignore) {}
        }
        Staff.numberStaff = Math.max(Staff.numberStaff, max);
    }
}