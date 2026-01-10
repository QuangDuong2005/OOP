package ctrl;

import java.util.List;

import model.Account;
import model.Staff;
import repository.AccountRepository;
import repository.StaffRepository;

public class AccountCtrl {
    private static final String ALPHA_NUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$";
    // đọc list account và list staff
    // Tạo nhân viên mới
    public void createStaff(String fullName, String dobString, String phoneNumber, String CCCD, String email) {
        Staff newStaff = new Staff(fullName, dobString, phoneNumber, CCCD, email);
        StaffRepository.appendStaffToFile(newStaff);
    }
    // Tạo tài khoản mới
    public String createAccount(String username, String role, String ownerID) {
        //Sinh mật khẩu ngẫu nhiên
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int index = (int)(Math.random() * ALPHA_NUMERIC.length());
            password.append(ALPHA_NUMERIC.charAt(index));
        }
        Account newAccount = new Account(username, password.toString(), role, ownerID);
        AccountRepository.appendAccountToFile(newAccount);
        return password.toString();
    }
    // Cập nhật thông tin nhân viên khi đã biết nhân viên đó
    public void updateStaffInfo(Staff staff, String fullName, String phoneNumber, String CCCD, String email, List<Staff> staffs) {
        staff.setCccd(CCCD);
        staff.setEmail(email);
        staff.setFullName(fullName);
        staff.setPhoneNumber(phoneNumber);

        StaffRepository.saveStaffsToFile(staffs);
    }
    // Vô hiệu hóa nhân viên khi đã biết nhân viên đó và vô hiệu hóa tài khoản của nhân viên đó (ko cần biết tài khoản)
    public void deactivateStaff(Staff staff, List<Account> accounts, List<Staff> staffs) {
        staff.setEnDate(java.time.LocalDate.now().toString());
        // Tìm và vô hiệu hóa tài khoản của nhân viên này
        // Vì 1 nhân viên có thể có nhiều tài khoản
        for (Account acc : accounts) {
            if (acc.getOwnerID().equals(staff.getStaffID())) {
                acc.setIsActive(false);
            }
        }
        // Lưu lại thay đổi
        StaffRepository.saveStaffsToFile(staffs);
        AccountRepository.saveAccountsToFile(accounts);
    }
    // Thay đổi mật khẩu tài khoản khi đã biết tài khoản đó
    public void changeAccountPassword(Account account, String newPassword, List<Account> accounts) throws expection.InvalidPasswordException {
        if (newPassword.length() < 6) {
            throw new expection.InvalidPasswordException("Mật khẩu phải có ít nhất 6 ký tự.");
        }else if (newPassword.equals(account.getPassword())) {
            throw new expection.InvalidPasswordException("Mật khẩu mới phải khác mật khẩu cũ.");
        }else{
            account.setPassword(newPassword);
            AccountRepository.saveAccountsToFile(accounts);
        }
    }
}
