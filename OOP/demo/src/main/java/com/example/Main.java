package com.example;

import java.util.List;
import java.util.Scanner;

import ctrl.AccountCtrl;
import ctrl.BookingCtrl;
import ctrl.CustomerCtrl;
import ctrl.InvoiceCtrl;
import ctrl.LoginCtrl;
import ctrl.TourCtrl;
import model.Account;
import model.Booking;
import model.Customer;
import model.Staff;
import model.Tour;
import repository.AccountRepository;
import repository.BookingRepository;
import repository.CustomerRepository;
import repository.StaffRepository;
import repository.TourRepository;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    
    // --- RAM DATABASE (Chỉ load 1 lần hoặc khi cần thiết) ---
    private static List<Account> RAM_Accounts = null; 
    private static List<Staff>   RAM_Staffs   = null;
    private static List<Tour>    RAM_Tours    = null;
    private static List<Customer> RAM_Customers = null;
    private static List<Booking>  RAM_Bookings  = null;
    // (Invoice ít dùng cập nhật nên có thể load khi cần)

    // SESSION
    private static Account currentAccount = null; 
    private static Staff currentStaff = null;    

    public static void main(String[] args) {
        // 1. LOAD TOÀN BỘ DỮ LIỆU VÀO RAM NGAY KHI KHỞI ĐỘNG
        loadAllDataToRAM();
        
        // Tạo admin ảo nếu hệ thống trắng trơn
        checkAndCreateDefaultAdmin();

        while (true) {
            System.out.println("\n====== TOUR MANAGEMENT SYSTEM ======");
            if (currentAccount == null) {
                handleLogin();
            } else {
                String role = currentAccount.getRole().toLowerCase().trim();
                System.out.println("User: " + currentAccount.getUsername() + " | Role: " + role.toUpperCase());
                
                switch (role) {
                    case "admin":      menuForAdmin();      break;
                    case "manager":    menuForManager();    break;
                    case "sale":       menuForSale();       break;
                    case "accountant": menuForAccountant(); break;
                    default:
                        System.err.println("Access Denied: Invalid Role [" + role + "]");
                        forceLogout();
                        break;
                }
            }
        }
    }

    // ==========================================================
    // DATA MANAGEMENT (RAM)
    // ==========================================================
    private static void loadAllDataToRAM() {
        System.out.println("Loading data...");
        RAM_Accounts  = AccountRepository.loadAccountsFromFile();
        RAM_Staffs    = StaffRepository.loadStaffsFromFile();
        RAM_Tours     = TourRepository.loadToursFromFile();
        RAM_Customers = CustomerRepository.loadCustomersFromFile();
        RAM_Bookings  = BookingRepository.loadBookingsFromFile();
    }

    // Chỉ gọi hàm này sau khi TẠO MỚI (Create) để lấy dữ liệu vừa thêm
    private static void reloadAccountsAndStaffs() {
        RAM_Accounts = AccountRepository.loadAccountsFromFile();
        RAM_Staffs   = StaffRepository.loadStaffsFromFile();
    }
    private static void reloadTours() {
        RAM_Tours = TourRepository.loadToursFromFile();
    }
    private static void reloadCustomers() {
        RAM_Customers = CustomerRepository.loadCustomersFromFile();
    }
    private static void reloadBookings() {
        RAM_Bookings = BookingRepository.loadBookingsFromFile();
    }

    // ==========================================================
    // AUTHENTICATION
    // ==========================================================
    private static void handleLogin() {
        System.out.println("--- LOGIN ---");
        System.out.print("Username: "); String username = scanner.nextLine().trim();
        System.out.print("Password: "); String password = scanner.nextLine().trim();

        // Dùng RAM để check login (Nhanh)
        Account acc = LoginCtrl.authenticate(username, password, RAM_Accounts);

        if (acc != null) {
            currentAccount = acc; 
            // Map sang Staff
            for (Staff s : RAM_Staffs) {
                if (s.getStaffID().equals(acc.getOwnerID())) {
                    currentStaff = s;
                    break;
                }
            }
            System.out.println("=> Login Successful!");
        } else {
            System.err.println("=> Login Failed! (Wrong info or Account Deactivated)");
        }
    }

    private static void forceLogout() {
        currentAccount = null;
        currentStaff = null;
        System.out.println("=> Logged out.");
    }

    private static void handleChangePassword() {
        System.out.println("\n--- CHANGE PASSWORD ---");
        System.out.print("New Password (min 6 chars): ");
        String newPass = scanner.nextLine().trim();

        AccountCtrl accCtrl = new AccountCtrl();
        try {
            // Truyền List RAM vào để sửa trực tiếp
            accCtrl.changeAccountPassword(currentAccount, newPass, RAM_Accounts);
            System.out.println("=> Password changed! Please login again.");
            forceLogout();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    // ==========================================================
    // 1. ADMIN MENU (Full Features)
    // ==========================================================
    private static void menuForAdmin() {
        System.out.println("\n--- ADMIN MENU ---");
        System.out.println("1. Add New Staff");
        System.out.println("2. Create Account for Staff");
        System.out.println("3. Update Staff Info");
        System.out.println("4. Deactivate Staff (Block Access)"); // CHỨC NĂNG BẠN YÊU CẦU
        System.out.println("5. Change Password");
        System.out.println("0. Logout");
        System.out.print("Choose: ");
        
        int choice = inputInt();
        switch (choice) {
            case 1: 
                subMenuAddStaff(); 
                reloadAccountsAndStaffs(); // Reload vì vừa Create
                break;
            case 2: 
                subMenuAddAccount(); 
                reloadAccountsAndStaffs(); // Reload vì vừa Create
                break;
            case 3: 
                subMenuUpdateStaff(); 
                // Không cần reload, Update sửa thẳng vào RAM
                break; 
            case 4: 
                subMenuDeactivateStaff(); 
                // Không cần reload, Deactivate sửa thẳng vào RAM
                break;
            case 5: handleChangePassword(); break;
            case 0: forceLogout(); break;
            default: System.out.println("Invalid choice!");
        }
    }

    private static void subMenuAddStaff() {
        AccountCtrl accCtrl = new AccountCtrl();
        System.out.print("Full Name: "); String name = scanner.nextLine();
        System.out.print("DOB (yyyy-MM-dd): "); String dob = scanner.nextLine();
        System.out.print("Phone: "); String phone = scanner.nextLine();
        System.out.print("CCCD: "); String cccd = scanner.nextLine();
        System.out.print("Email: "); String email = scanner.nextLine();
        accCtrl.createStaff(name, dob, phone, cccd, email);
        System.out.println("=> Staff Added!");
    }

    private static void subMenuAddAccount() {
        AccountCtrl accCtrl = new AccountCtrl();
        System.out.println("--- Staff List ---");
        for(Staff s : RAM_Staffs) {
            String status = (s.getEnDate() == null || s.getEnDate().equals("null")) ? "Active" : "Inactive";
            System.out.println("ID: " + s.getStaffID() + " | " + s.getFullName() + " | " + status);
        }
        
        System.out.print("Staff ID: "); String sid = scanner.nextLine();
        System.out.print("Username: "); String u = scanner.nextLine();
        System.out.print("Role (admin/manager/sale/accountant): "); String r = scanner.nextLine().toLowerCase();

        String pass = accCtrl.createAccount(u, r, sid);
        System.out.println("=> Account Created! Password: " + pass);
    }

    private static void subMenuUpdateStaff() {
        System.out.print("Enter Staff ID to Update: "); String sid = scanner.nextLine();
        Staff target = findStaffByID(sid);
        if(target == null) return;

        System.out.println("Updating for: " + target.getFullName());
        System.out.print("New Name: "); String name = scanner.nextLine();
        System.out.print("New Phone: "); String phone = scanner.nextLine();
        System.out.print("New CCCD: "); String cccd = scanner.nextLine();
        System.out.print("New Email: "); String email = scanner.nextLine();

        AccountCtrl accCtrl = new AccountCtrl();
        // Truyền RAM_Staffs vào để sửa trực tiếp
        accCtrl.updateStaffInfo(target, name, phone, cccd, email, RAM_Staffs);
        System.out.println("=> Staff Updated in RAM & File!");
    }

    private static void subMenuDeactivateStaff() {
        System.out.print("Enter Staff ID to Deactivate: "); String sid = scanner.nextLine();
        Staff target = findStaffByID(sid);
        if(target == null) return;

        System.out.print("Are you sure you want to deactivate " + target.getFullName() + "? (y/n): ");
        if(scanner.nextLine().equalsIgnoreCase("y")) {
            AccountCtrl accCtrl = new AccountCtrl();
            // Truyền cả 2 List RAM vào
            accCtrl.deactivateStaff(target, RAM_Accounts, RAM_Staffs);
            System.out.println("=> Staff Deactivated. Account Locked.");
        }
    }

    // ==========================================================
    // 2. MANAGER MENU (Tour)
    // ==========================================================
    private static void menuForManager() {
        System.out.println("\n--- MANAGER MENU ---");
        System.out.println("1. View Tours");
        System.out.println("2. Create Tour");
        System.out.println("3. Update Tour");
        System.out.println("4. Change Password");
        System.out.println("0. Logout");
        int choice = inputInt();
        switch (choice) {
            case 1: viewAllTours(); break;
            case 2: 
                createNewTour(); 
                reloadTours(); // Reload vì Create
                break;
            case 3: 
                updateTour(); 
                // No reload
                break;
            case 4: handleChangePassword(); break;
            case 0: forceLogout(); break;
        }
    }

    private static void viewAllTours() {
        System.out.printf("%-10s %-20s %-15s %-10s\n", "ID", "Name", "Price", "Avail");
        for (Tour t : RAM_Tours) {
            System.out.printf("%-10s %-20s %-15.0f %-10d\n", 
                t.getTourID(), t.getDescription(), t.getPricePerPerson(), (t.getMaxPeople() - t.getBookedPeople()));
        }
    }

    private static void createNewTour() {
        try {
            System.out.print("Name: "); String desc = scanner.nextLine();
            System.out.print("Dep Point: "); String dep = scanner.nextLine();
            System.out.print("Price: "); double price = Double.parseDouble(scanner.nextLine());
            System.out.print("Max People: "); int max = Integer.parseInt(scanner.nextLine());
            System.out.print("Start (yyyy-MM-dd): "); String start = scanner.nextLine();
            System.out.print("End (yyyy-MM-dd): "); String end = scanner.nextLine();
            TourCtrl.createTour(desc, dep, price, max, start, end, desc);
            System.out.println("=> Tour Created!");
        } catch (Exception e) { System.err.println("Error: " + e.getMessage()); }
    }

    private static void updateTour() {
        System.out.print("Enter Tour ID: "); String tid = scanner.nextLine();
        Tour target = null;
        for(Tour t : RAM_Tours) if(t.getTourID().equals(tid)) target = t;
        
        if(target == null) { System.out.println("Not Found!"); return; }
        
        try {
            System.out.print("New Name: "); String desc = scanner.nextLine();
            System.out.print("New Dep: "); String dep = scanner.nextLine();
            System.out.print("New Price: "); double price = Double.parseDouble(scanner.nextLine());
            System.out.print("New Max: "); int max = Integer.parseInt(scanner.nextLine());
            System.out.print("New Start: "); String start = scanner.nextLine();
            System.out.print("New End: "); String end = scanner.nextLine();
            
            // Gọi Update với RAM List
            TourCtrl.updateTourInfo(target, desc, dep, price, max, start, end, target.getBookedPeople(), RAM_Tours);
            System.out.println("=> Tour Updated!");
        } catch(Exception e) { System.err.println("Error: " + e.getMessage()); }
    }

    // ==========================================================
    // 3. SALE MENU (Customer & Booking)
    // ==========================================================
    private static void menuForSale() {
        System.out.println("\n--- SALE MENU ---");
        System.out.println("1. Customer List");
        System.out.println("2. Add Customer");
        System.out.println("3. Create Booking");
        System.out.println("4. Update Booking"); // Yêu cầu của bạn
        System.out.println("5. Change Password");
        System.out.println("0. Logout");
        int choice = inputInt();
        switch (choice) {
            case 1: 
                for(Customer c : RAM_Customers) System.out.println(c.getCustomerID() + " | " + c.getFullName());
                break;
            case 2: 
                createNewCustomer();
                reloadCustomers(); // Reload
                break;
            case 3: 
                createBooking();
                reloadBookings(); 
                reloadTours(); // Booking ảnh hưởng số chỗ của Tour
                break;
            case 4: 
                updateBooking();
                // No reload needed usually, but logic depends on impl
                break;
            case 5: handleChangePassword(); break;
            case 0: forceLogout(); break;
        }
    }
    
    private static void createNewCustomer() {
        System.out.print("Name: "); String n = scanner.nextLine();
        System.out.print("DOB: "); String d = scanner.nextLine();
        System.out.print("Phone: "); String p = scanner.nextLine();
        System.out.print("Email: "); String e = scanner.nextLine();
        CustomerCtrl.createCustomer(n, d, p, e);
        System.out.println("=> Customer Added!");
    }

    private static void createBooking() {
        try {
            System.out.print("Customer ID: "); String cid = scanner.nextLine();
            Customer cus = null;
            for(Customer c : RAM_Customers) if(c.getCustomerID().equals(cid)) cus = c;
            if(cus == null) { System.out.println("Customer Not Found!"); return; }

            System.out.print("Tour ID: "); String tid = scanner.nextLine();
            Tour tour = null;
            for(Tour t : RAM_Tours) if(t.getTourID().equals(tid)) tour = t;
            if(tour == null) { System.out.println("Tour Not Found!"); return; }

            System.out.print("People: "); int num = Integer.parseInt(scanner.nextLine());
            
            // Truyền RAM_Tours để cập nhật chỗ trống ngay lập tức
            BookingCtrl.createBooking(cus, tour, num, currentStaff, RAM_Tours);
            System.out.println("=> Booking Success!");
        } catch(Exception e) { System.err.println("Error: " + e.getMessage()); }
    }
    
    private static void updateBooking() {
        // Ví dụ update trạng thái thanh toán hoặc số tiền
        System.out.print("Booking ID: "); String bid = scanner.nextLine();
        Booking target = null;
        for(Booking b : RAM_Bookings) if(b.getBookingID().equals(bid)) target = b;
        if(target == null) { System.out.println("Booking Not Found!"); return; }

        System.out.print("Update Total Amount: "); double tot = Double.parseDouble(scanner.nextLine());
        System.out.print("Update Debt: "); double debt = Double.parseDouble(scanner.nextLine());
        
        BookingCtrl.updateBookingInfo(target, tot, debt);
        System.out.println("=> Booking Info Updated!");
    }

    // ==========================================================
    // 4. ACCOUNTANT MENU (Invoice)
    // ==========================================================
    private static void menuForAccountant() {
        System.out.println("\n--- ACCOUNTANT MENU ---");
        System.out.println("1. Create Invoice");
        System.out.println("2. Change Password");
        System.out.println("0. Logout");
        int choice = inputInt();
        switch (choice) {
            case 1: createInvoice(); break;
            case 2: handleChangePassword(); break;
            case 0: forceLogout(); break;
        }
    }

    private static void createInvoice() {
        try {
            System.out.print("Booking ID: "); String bid = scanner.nextLine();
            Booking b = null;
            // Tìm trong RAM cho nhanh
            for(Booking bk : RAM_Bookings) if(bk.getBookingID().equals(bid)) b = bk;
            
            if(b == null) { System.out.println("Not Found!"); return; }
            System.out.println("Total: " + b.getTotalAmount() + " | Debt: " + b.getDebtAmount());

            System.out.print("Pay Amount: "); double amt = Double.parseDouble(scanner.nextLine());
            InvoiceCtrl.createInvoice(b, amt);
            System.out.println("=> Paid!");
        } catch(Exception e) { System.err.println("Error: " + e.getMessage()); }
    }

    // ==========================================================
    // HELPERS
    // ==========================================================
    private static int inputInt() {
        try { return Integer.parseInt(scanner.nextLine()); } catch (Exception e) { return -1; }
    }
    
    private static Staff findStaffByID(String id) {
        for(Staff s : RAM_Staffs) {
            if(s.getStaffID().equals(id)) return s;
        }
        System.out.println("Staff ID Not Found!");
        return null;
    }

    private static void checkAndCreateDefaultAdmin() {
        if (RAM_Accounts == null || RAM_Accounts.isEmpty()) {
            System.out.println("--> Creating Default Admin...");
            Staff adminStaff = new Staff("Admin", "2000-01-01", "0900", "001", "admin@sys.com");
            StaffRepository.appendStaffToFile(adminStaff);
            Account adminAccount = new Account("admin", "123456", "admin", adminStaff.getStaffID());
            AccountRepository.appendAccountToFile(adminAccount);
            
            // Reload RAM ngay để đăng nhập được luôn
            reloadAccountsAndStaffs(); 
            System.out.println("--> Done. User: admin / Pass: 123456");
        }
    }
}