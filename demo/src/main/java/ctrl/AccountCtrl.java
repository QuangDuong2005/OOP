package ctrl;

import java.util.List;

import exception.DuplicateException;
import model.Account;
import repository.AccountRepository;

public class AccountCtrl {
    List<Account> accounts = AccountRepository.loadAccountsFromFile();
    // Chuỗi để tạo mk ngẫu nhiên
    private static final String ALPHA_NUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$";

    // Tạo Account ko cần kiểm tra owner ID vì trong GUI hiển thị bảng staff
    public String createAccount(String username, String role, String ownerID) throws DuplicateException {
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int index = (int) (Math.random() * ALPHA_NUMERIC.length());
            password.append(ALPHA_NUMERIC.charAt(index));
        }
        // không cho tạo nếu mà có cùng tên tài khoản
        boolean a = true;
        for (Account account: accounts){
            if ((account.getUsername()).equals(username)){
                a = false;
                break;
            }
        }
        if (a){
            Account newAccount = new Account(username.trim(), (password.toString()).trim(), role.trim(), ownerID.trim());
            accounts.add(newAccount);
            AccountRepository.saveAccountsToFile(accounts);
            return password.toString();
        }else{
            throw new DuplicateException("Tài khoản đã tồn tại");
        }
    }
    // Cập nhật mật khẩu 
    public void changeAccountPassword(String username, String newPassword) throws exception.InvalidPasswordException {
        for (Account account: accounts){
            if ((account.getUsername()).equals(username)){
                if (newPassword == null || newPassword.trim().length() < 6) {
                    throw new exception.InvalidPasswordException("Mật khẩu phải có ít nhất 6 ký tự.");
                }
                if (newPassword.equals(account.getPassword())) {
                    throw new exception.InvalidPasswordException("Mật khẩu mới phải khác mật khẩu cũ.");
                }
                account.setPassword(newPassword);
                AccountRepository.saveAccountsToFile(accounts);
            }
        }
    }
    // Khóa tài khoản
    public void deactivateAccount(String username) {
        for (Account account: accounts){
            if ((account.getUsername()).equals(username)){
                account.setIsActive(false);
                AccountRepository.saveAccountsToFile(accounts);
            }
        }
    }
    // Mở lại tài khoản
    public void activateAccount(String username) {
        for (Account account: accounts){
            if ((account.getUsername()).equals(username)){
                account.setIsActive(true);
                AccountRepository.saveAccountsToFile(accounts);  
            }
        }
    }
}
