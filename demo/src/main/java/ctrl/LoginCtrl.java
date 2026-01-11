package ctrl;
import java.util.List;

import model.Account;

public class LoginCtrl {
    // Phương thức để kiểm tra đăng nhập
    public static Account authenticate(String username, String password, List<Account> accounts) {
        if (accounts != null) {
            for (Account account : accounts) {
                if (account.username.equals(username) && account.password.equals(password) && account.isActive == true) {
                    return account; // Đăng nhập thành công
                }
            }
        }
        return null; // Đăng nhập thất bại
    }    
}
