package ctrl;

import java.util.List;

import model.Account;
import model.Staff;
import repository.AccountRepository;
import repository.StaffRepository;

public class AccountCtrl {
    private static final String ALPHA_NUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$";

    // =====================
    // STAFF
    // =====================
    public void createStaff(String fullName, String dobString, String phoneNumber, String CCCD, String email) {
        Staff newStaff = new Staff(fullName, dobString, phoneNumber, CCCD, email);
        StaffRepository.appendStaffToFile(newStaff);
    }

    public void updateStaffInfo(Staff staff, String fullName, String phoneNumber, String CCCD, String email, List<Staff> staffs) {
        if (fullName != null && !fullName.isBlank()) staff.setFullName(fullName);
        if (phoneNumber != null && !phoneNumber.isBlank()) staff.setPhoneNumber(phoneNumber);
        if (CCCD != null && !CCCD.isBlank()) staff.setCccd(CCCD);
        if (email != null && !email.isBlank()) staff.setEmail(email);

        StaffRepository.saveStaffsToFile(staffs);
    }

    /**
     * Deactivate staff and all their accounts.
     */
    public void deactivateStaff(Staff staff, List<Account> accounts, List<Staff> staffs) {
        staff.setEnDate(java.time.LocalDate.now().toString());
        staff.setIsActive(false);

        for (Account acc : accounts) {
            if (acc.getOwnerID().equals(staff.getStaffID())) {
                acc.setIsActive(false);
            }
        }

        StaffRepository.saveStaffsToFile(staffs);
        AccountRepository.saveAccountsToFile(accounts);
    }

    // =====================
    // ACCOUNT
    // =====================
    public String createAccount(String username, String role, String ownerID) {
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int index = (int) (Math.random() * ALPHA_NUMERIC.length());
            password.append(ALPHA_NUMERIC.charAt(index));
        }
        Account newAccount = new Account(username, password.toString(), role, ownerID);
        AccountRepository.appendAccountToFile(newAccount);
        return password.toString();
    }

    public void updateAccountInfo(Account account, String newUsername, String newRole, Boolean isActive, List<Account> accounts) {
        if (newUsername != null && !newUsername.trim().isEmpty()) {
            account.setUsername(newUsername.trim());
        }
        if (newRole != null && !newRole.trim().isEmpty()) {
            account.setRole(newRole.trim().toLowerCase());
        }
        if (isActive != null) {
            account.setIsActive(isActive);
        }
        AccountRepository.saveAccountsToFile(accounts);
    }

    public void changeAccountPassword(Account account, String newPassword, List<Account> accounts) throws expection.InvalidPasswordException {
        if (newPassword == null || newPassword.trim().length() < 6) {
            throw new expection.InvalidPasswordException("Mật khẩu phải có ít nhất 6 ký tự.");
        }
        if (newPassword.equals(account.getPassword())) {
            throw new expection.InvalidPasswordException("Mật khẩu mới phải khác mật khẩu cũ.");
        }

        account.setPassword(newPassword);
        AccountRepository.saveAccountsToFile(accounts);
    }

    public void deactivateAccount(Account account, List<Account> accounts) {
        account.setIsActive(false);
        AccountRepository.saveAccountsToFile(accounts);
    }

    public void activateAccount(Account account, List<Account> accounts) {
        account.setIsActive(true);
        AccountRepository.saveAccountsToFile(accounts);
    }
}
