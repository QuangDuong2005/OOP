package model;

public class Account {
    // đếm số tài khoản đã tạo
    public String username;
    public String password;
    public String role; // Vai trò: "admin", "manager", "staff", "accountant"
    public String ownerID; // Mã nhân viên sử dụng tài khoản
    public boolean isActive; // Trạng thái: "active" hoặc "inactive"
    // Constructor rỗng
    public Account(){};
    // Constructor khi tạo tài khoản mới
    public Account(String username, String password, String role, String ownerID) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.ownerID = ownerID;
        this.isActive = true;
    }
    // Getters
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public String getRole() {
        return role;
    }
    public String getOwnerID() {
        return ownerID;
    }
    public boolean getIsActive() {
        return isActive;
    }
    // Setters
    public void setPassword(String newPassword) {
        this.password = newPassword;
    }
    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
    public void setUsername(String newUsername) {
        this.username = newUsername;
    }
    public void setRole(String newRole) {
        this.role = newRole;
    }
    public void setOwnerID(String newOwnerID) {
        this.ownerID = newOwnerID;
    }

}