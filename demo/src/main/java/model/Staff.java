package model;

import java.time.LocalDate;
public class Staff extends Person {
    public String staffID;
    public LocalDate createdAt;
    public String cccd; 
    public String role;// Vai trò: 
    public String email;
    public LocalDate enDate;
    public boolean isActive; // Trạng thái: Đang làm việc hay đã nghỉ
    // Hàm kiểm tra trạng thái nhân viên
    public boolean updateStatus(){
        if (this.enDate != null){
            return false;
        } else {
            return true;
        }
    }
    // constructor rỗng
    public Staff(){};
    // Constructor khi tạo nhân viên mới
    public Staff(String ID,String fullName, String dobString, String phoneNumber, String CCCD, String email, String role) {
        super(fullName, dobString, phoneNumber);
        this.staffID = ID;
        this.createdAt = LocalDate.now();
        this.cccd = CCCD;
        this.email = email;
        this.isActive = true;
        this.enDate = null;
        this.role = role;
    }
    // Getters
    public String getStaffID() {
        return staffID;
    }
    public String getCccd() {
        return cccd;
    }
    public String getEmail() {
        return email;
    }
    public String getCreatedAt() {
        return createdAt.toString();
    }
    public boolean getisActive() {
        return isActive;
    }
    public String getEnDate(){
       if (this.enDate == null) {
            return "null"; // Hoặc trả về null nếu muốn
        }
        return this.enDate.toString();
    }
    public String getRole(){
        return role;
    }
    //setters
    public void setCccd(String CCCD) {
        this.cccd = CCCD;
    }   
    public void setEmail(String email) {
        this.email = email;
    }
    public void setCreatedAt(String createdAtString) {
        this.createdAt = LocalDate.parse(createdAtString);
    }
    public void setStaffID(String staffID) {
        this.staffID = staffID;
    }
    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
    public void setEnDate(String enDateString) {
        if (enDateString.equals("null")){
            this.enDate = null;
        }else{
            this.enDate = LocalDate.parse(enDateString);
        }
    }
    public void setRole(String role){
        this.role = role;
    }
}