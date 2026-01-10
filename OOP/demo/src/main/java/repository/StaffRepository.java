package repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import model.Staff;

public class StaffRepository {

    // file json lưu trữ Staff

    private static final String FILE_PATH = "demo/src/main/resources/data/Staff.json";

    //  phương thức để lưu Staff vào file JSON

    public static void saveStaffsToFile(List<Staff> staffs) {

        ObjectMapper mapper = new ObjectMapper();

        mapper.enable(SerializationFeature.INDENT_OUTPUT); // Định dạng đẹp

        try {

            mapper.writeValue(new File(FILE_PATH), staffs);

        } catch (IOException e) {

            System.err.println(e.getMessage());

        }

    }

    //  phương thức để đọc Staff từ file JSON

    public static List<Staff> loadStaffsFromFile() {

        ObjectMapper mapper = new ObjectMapper();

        try {

            File file = new File(FILE_PATH);

            return mapper.readValue(file, mapper.getTypeFactory().constructCollectionType(List.class, Staff.class));

        } catch (IOException e) {

            System.err.println("Error loading staffs from file");

            System.err.println(e.getMessage());

        }

        return new ArrayList<>();

    }

    // Ghi thêm Staff vào file JSON

    public static void appendStaffToFile(Staff newStaff) {

        List<Staff> staffs = loadStaffsFromFile();

        staffs.add(newStaff);

        saveStaffsToFile(staffs);

    }

}