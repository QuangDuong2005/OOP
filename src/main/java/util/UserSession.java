package util; // Hoặc package model tùy bạn

import model.Account;

public class UserSession {
    private static UserSession instance;
    private Account account; // Tài khoản đang đăng nhập

    // Constructor private để chặn việc tạo mới lung tung
    private UserSession(Account account) {
        this.account = account;
    }

    // Hàm khởi tạo phiên làm việc (Gọi khi Login thành công)
    public static void setSession(Account account) {
        instance = new UserSession(account);
    }

    // Hàm lấy phiên làm việc hiện tại (Gọi ở các màn hình khác)
    public static UserSession getSession() {
        return instance;
    }

    // Hàm lấy thông tin tài khoản
    public Account getAccount() {
        return account;
    }

    // Hàm xóa phiên (Gọi khi Logout)
    public static void clear() {
        instance = null;
    }
}