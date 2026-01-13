package model;
import java.time.LocalDate;

public abstract class Person {
    public  String fullName;
    public  LocalDate dateOfBirth;
    public  String phoneNumber;
    // Constructor rỗng 
    public Person(){};
    // Constructor có tham số
    public Person(String fullName, String dobString, String phoneNumber) {
        this.fullName = fullName;
        // Chuyển chuỗi "yyyy-MM-dd" (ví dụ 2000-12-25) sang LocalDate
        this.dateOfBirth = LocalDate.parse(dobString);
        this.phoneNumber = phoneNumber;
    }
    // getters
    public String getFullName() {
        return fullName;
    }

    public String getDateOfBirth() {
        return dateOfBirth.toString();
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    // setters
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    public void setDateOfBirth(String dobString) {
        this.dateOfBirth = LocalDate.parse(dobString);
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}