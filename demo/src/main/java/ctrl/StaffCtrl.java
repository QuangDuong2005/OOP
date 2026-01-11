package ctrl;

import java.util.List;

import expection.DuplicateException;
import model.Account;
import model.Staff;
import repository.AccountRepository;
import repository.StaffRepository;

public class StaffCtrl {
    List<Staff> staffs = StaffRepository.loadStaffsFromFile();
    // tạo nhân viên ko 
    public void createStaff(String ID, String fullName, String dobString, String phoneNumber, String CCCD, String email, String role) throws DuplicateException {
        boolean a = true;
        // Xem có bị trùng ID ko
        for (Staff staff : staffs) {
            if ((staff.getStaffID()).equals(ID)){
                a = false;
                break;
            }   
        }
        if (a){
            Staff newStaff = new Staff(ID,fullName, dobString, phoneNumber, CCCD, email, role);
            staffs.add(newStaff);
            StaffRepository.saveStaffsToFile(staffs);
        }else{
            throw new DuplicateException("ID không được trùng");
        }
    }
    // Update thông tin
    public void updateStaffInfo(String staffID, String fullName, String phoneNumber, String CCCD, String email) {
        for (Staff staff : staffs) {
            if (staff.getStaffID().equals(staffID)) {
                if (fullName != null && !fullName.isBlank()) staff.setFullName(fullName);
                if (phoneNumber != null && !phoneNumber.isBlank()) staff.setPhoneNumber(phoneNumber);
                if (CCCD != null && !CCCD.isBlank()) staff.setCccd(CCCD);
                if (email != null && !email.isBlank()) staff.setEmail(email);
            }
        }
        StaffRepository.saveStaffsToFile(staffs);
    }

    /**
     * Deactivate staff and all their accounts.
     */
    // Nhân viên nghỉ việc thì cập nhật
    public void deactivateStaff(String staffID) {
        List<Account> accounts = AccountRepository.loadAccountsFromFile();
        for (Staff staff : staffs){
            if (staff.getStaffID().equals(staffID)) {
                    staff.setEnDate(java.time.LocalDate.now().toString());
                    staff.setIsActive(false);
                    // Nếu nhân viên đó có tài khoản thì ko cho hoạt động tài khoản
                    for (Account acc : accounts) {
                        if (acc.getOwnerID().equals(staff.getStaffID())) {
                            acc.setIsActive(false);
                            break;
                        }
                    }
                    StaffRepository.saveStaffsToFile(staffs);
                    AccountRepository.saveAccountsToFile(accounts);
                    break;
                } 
            }
    }
}
