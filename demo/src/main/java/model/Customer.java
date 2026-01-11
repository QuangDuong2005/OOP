package model;

import java.time.LocalDate;
public class Customer extends Person {
    // Biến chung cho lớp Customer
    public String customerID;
    public String email;
    public LocalDate createdAt;
    public Customer(){};
    // Constructor 1: Dùng để tạo khách hàng mới
    public Customer(String ID, String fullName, String dobString, String phoneNumber, String email) {
        super(fullName, dobString, phoneNumber);
        this.email = email;
        this.customerID = ID; // Tạo customerID tự động theo định dạng C001, C002, ...
        this.createdAt = LocalDate.now();
    }

    // Constructor khi không có email
    public Customer(String ID,String fullName, String dobString, String phoneNumber) {
        super(fullName, dobString, phoneNumber);
        this.email = "Chưa có email";
        this.customerID = ID; // Tạo customerID tự động theo định dạng C001, C002, ...
        this.createdAt = LocalDate.now();
    }
    // Getters
    public String getEmail() {
        return email;
    }
    public String getCustomerID() {
        return customerID;
    }
    public String getCreatedAt() {
        return createdAt.toString();
    }
    // Setters
    public void setEmail(String email) {
        this.email = email;
    }
    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }
    public void setCreatedAt(String createdAtString) {
        this.createdAt = LocalDate.parse(createdAtString);
    }
}