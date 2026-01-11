package com.example;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import ctrl.AccountCtrl;
import ctrl.ApprovalRequestCtrl;
import ctrl.BookingCtrl;
import ctrl.CustomerCtrl;
import ctrl.InvoiceCtrl;
import ctrl.LoginCtrl;
import ctrl.ReportCtrl;
import ctrl.TourCtrl;
import model.Account;
import model.ApprovalRequest;
import model.Booking;
import model.Customer;
import model.Invoice;
import model.Report;
import model.Staff;
import model.Tour;
import repository.AccountRepository;
import repository.ApprovalRequestRepository;
import repository.BookingRepository;
import repository.CustomerRepository;
import repository.InvoiceRepository;
import repository.ReportRepository;
import repository.StaffRepository;
import repository.TourRepository;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    // --- RAM DATABASE ---
    private static List<Account> RAM_Accounts = new ArrayList<>();
    private static List<Staff> RAM_Staffs = new ArrayList<>();
    private static List<Tour> RAM_Tours = new ArrayList<>();
    private static List<Customer> RAM_Customers = new ArrayList<>();
    private static List<Booking> RAM_Bookings = new ArrayList<>();
    private static List<Invoice> RAM_Invoices = new ArrayList<>();
    private static List<ApprovalRequest> RAM_Requests = new ArrayList<>();
    private static List<Report> RAM_Reports = new ArrayList<>();

    // SESSION
    private static Account currentAccount = null;
    private static Staff currentStaff = null;

    public static void main(String[] args) {
        loadAllDataToRAM();
        checkAndCreateDefaultAdmin();

        while (true) {
            System.out.println("\n====== TOUR MANAGEMENT SYSTEM ======");

            if (currentAccount == null) {
                handleLogin();
                continue;
            }

            String role = currentAccount.getRole().toLowerCase().trim();
            System.out.println("User: " + currentAccount.getUsername() + " | Role: " + role.toUpperCase());

            switch (role) {
                case "admin":
                    menuForAdmin();
                    break;
                case "manager":
                    menuForManager();
                    break;
                case "sale":
                    menuForSale();
                    break;
                case "accountant":
                    menuForAccountant();
                    break;
                default:
                    System.err.println("Access Denied: Invalid Role [" + role + "]");
                    forceLogout();
                    break;
            }
        }
    }

    // ==========================================================
    // DATA (RAM)
    // ==========================================================
    private static void loadAllDataToRAM() {
        System.out.println("Loading data...");
        RAM_Accounts = AccountRepository.loadAccountsFromFile();
        RAM_Staffs = StaffRepository.loadStaffsFromFile();
        RAM_Tours = TourRepository.loadToursFromFile();
        RAM_Customers = CustomerRepository.loadCustomersFromFile();
        RAM_Bookings = BookingRepository.loadBookingsFromFile();
        RAM_Invoices = InvoiceRepository.loadInvoicesFromFile();
        RAM_Requests = ApprovalRequestRepository.loadFromFile();
        RAM_Reports = ReportRepository.loadFromFile();
    }

    private static void reloadAccountsAndStaffs() {
        RAM_Accounts = AccountRepository.loadAccountsFromFile();
        RAM_Staffs = StaffRepository.loadStaffsFromFile();
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

    private static void reloadInvoices() {
        RAM_Invoices = InvoiceRepository.loadInvoicesFromFile();
    }

    private static void reloadRequests() {
        RAM_Requests = ApprovalRequestRepository.loadFromFile();
    }

    private static void reloadReports() {
        RAM_Reports = ReportRepository.loadFromFile();
    }

    // ==========================================================
    // AUTH
    // ==========================================================
    private static void handleLogin() {
        System.out.println("--- LOGIN ---");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        Account acc = LoginCtrl.authenticate(username, password, RAM_Accounts);
        if (acc == null) {
            System.err.println("=> Login Failed! (Wrong info or Account Deactivated)");
            return;
        }

        currentAccount = acc;
        currentStaff = null;
        for (Staff s : RAM_Staffs) {
            if (s.getStaffID().equals(acc.getOwnerID())) {
                currentStaff = s;
                break;
            }
        }

        System.out.println("=> Login Successful!");
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
            accCtrl.changeAccountPassword(currentAccount, newPass, RAM_Accounts);
            System.out.println("=> Password changed! Please login again.");
            forceLogout();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    // ==========================================================
    // ADMIN MENU
    // ==========================================================
    private static void menuForAdmin() {
        showManagerNotificationBar(); // admin can also approve
        System.out.println("\n--- ADMIN MENU ---");
        System.out.println("1. Manage Pending Approvals");
        System.out.println("2. Manage Accounts");
        System.out.println("3. Manage Employees");
        System.out.println("4. Manage Tours");
        System.out.println("5. Change Password");
        System.out.println("0. Logout");
        System.out.print("Choose: ");

        int choice = inputInt();
        switch (choice) {
            case 1:
                manageApprovals();
                break;
            case 2:
                manageAccounts();
                break;
            case 3:
                manageEmployees();
                break;
            case 4:
                manageTours();
                break;
            case 5:
                handleChangePassword();
                break;
            case 0:
                forceLogout();
                break;
            default:
                System.out.println("Invalid choice!");
        }
    }

    // ==========================================================
    // MANAGER MENU
    // ==========================================================
    private static void menuForManager() {
        showManagerNotificationBar();
        System.out.println("\n--- MANAGER MENU ---");
        System.out.println("1. Manage Pending Approvals");
        System.out.println("2. Manage Accounts");
        System.out.println("3. Manage Employees");
        System.out.println("4. Manage Tours");
        System.out.println("5. Change Password");
        System.out.println("0. Logout");
        System.out.print("Choose: ");

        int choice = inputInt();
        switch (choice) {
            case 1:
                manageApprovals();
                break;
            case 2:
                manageAccounts();
                break;
            case 3:
                manageEmployees();
                break;
            case 4:
                manageTours();
                break;
            case 5:
                handleChangePassword();
                break;
            case 0:
                forceLogout();
                break;
            default:
                System.out.println("Invalid choice!");
        }
    }

    private static void showManagerNotificationBar() {
        long pending = RAM_Requests.stream().filter(r -> r != null && "PENDING".equalsIgnoreCase(r.status)).count();
        if (pending > 0) {
            System.out.println("[NOTIFICATION] Pending approvals: " + pending);
        } else {
            System.out.println("[NOTIFICATION] No pending approvals.");
        }
    }

    // ==========================================================
    // MANAGER FEATURES
    // ==========================================================
    private static void manageApprovals() {
        System.out.println("\n--- PENDING APPROVAL REQUESTS ---");
        boolean has = false;
        for (ApprovalRequest r : RAM_Requests) {
            if (r != null && "PENDING".equalsIgnoreCase(r.status)) {
                has = true;
                System.out.println("ID: " + r.requestId + " | Type: " + r.type + " | By: " + r.createdByStaffId + " | Reason: " + r.reason);
                if ("INVOICE_EDIT".equalsIgnoreCase(r.type)) {
                    System.out.println("   invoiceId=" + r.invoiceId + " => newAmount=" + r.newInvoiceAmount);
                }
                if ("REFUND".equalsIgnoreCase(r.type)) {
                    System.out.println("   bookingId=" + r.bookingId + " refund=" + r.refundAmount + " penalty=" + r.penaltyAmount);
                }
            }
        }
        if (!has) {
            System.out.println("(none)");
            return;
        }

        System.out.print("Enter Request ID to process (or blank to back): ");
        String rid = scanner.nextLine().trim();
        if (rid.isEmpty()) return;

        ApprovalRequest target = null;
        for (ApprovalRequest r : RAM_Requests) {
            if (r != null && rid.equalsIgnoreCase(r.requestId)) {
                target = r;
                break;
            }
        }
        if (target == null) {
            System.out.println("Request Not Found!");
            return;
        }

        System.out.print("Approve or Reject? (a/r): ");
        String ar = scanner.nextLine().trim();
        System.out.print("Manager note: ");
        String note = scanner.nextLine();

        try {
            if (ar.equalsIgnoreCase("a")) {
                ApprovalRequestCtrl.approveRequest(target, currentStaff.getStaffID(), note, RAM_Requests, RAM_Invoices, RAM_Bookings);
                System.out.println("=> Approved.");
                reloadInvoices();
            } else if (ar.equalsIgnoreCase("r")) {
                ApprovalRequestCtrl.rejectRequest(target, currentStaff.getStaffID(), note, RAM_Requests);
                System.out.println("=> Rejected.");
            } else {
                System.out.println("Invalid action.");
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void manageAccounts() {
        AccountCtrl accCtrl = new AccountCtrl();

        System.out.println("\n--- ACCOUNT MANAGEMENT ---");
        System.out.println("1. List Accounts");
        System.out.println("2. Add Account");
        System.out.println("3. Edit Account");
        System.out.println("4. Deactivate/Activate Account");
        System.out.println("0. Back");
        System.out.print("Choose: ");
        int c = inputInt();

        switch (c) {
            case 1:
                for (Account a : RAM_Accounts) {
                    System.out.println(a.accountID + " | " + a.username + " | role=" + a.role + " | owner=" + a.ownerID + " | active=" + a.isActive);
                }
                break;
            case 2:
                System.out.println("--- Staff List ---");
                for (Staff s : RAM_Staffs) {
                    String status = (s.getEnDate() == null || s.getEnDate().equals("null")) ? "Active" : "Inactive";
                    System.out.println("ID: " + s.getStaffID() + " | " + s.getFullName() + " | " + status);
                }
                System.out.print("Staff ID: ");
                String sid = scanner.nextLine().trim();
                if (findStaffByID(sid) == null) return;

                System.out.print("Username: ");
                String u = scanner.nextLine().trim();
                if (isUsernameTaken(u)) {
                    System.out.println("Username already exists!");
                    return;
                }
                System.out.print("Role (admin/manager/sale/accountant): ");
                String r = scanner.nextLine().trim().toLowerCase();

                String pass = accCtrl.createAccount(u, r, sid);
                reloadAccountsAndStaffs();
                System.out.println("=> Account Created! Password: " + pass);
                break;
            case 3:
                System.out.print("Account ID: ");
                String aid = scanner.nextLine().trim();
                Account a = findAccountById(aid);
                if (a == null) return;
                System.out.print("New username (blank skip): ");
                String nu = scanner.nextLine();
                if (!nu.isBlank() && isUsernameTakenExcept(nu.trim(), a.accountID)) {
                    System.out.println("Username already exists!");
                    return;
                }
                System.out.print("New role (blank skip): ");
                String nr = scanner.nextLine();
                System.out.print("Active? (true/false/blank skip): ");
                String act = scanner.nextLine().trim();
                Boolean active = null;
                if (!act.isEmpty()) active = Boolean.parseBoolean(act);
                accCtrl.updateAccountInfo(a, nu, nr, active, RAM_Accounts);
                System.out.println("=> Updated.");
                break;
            case 4:
                System.out.print("Account ID: ");
                String id = scanner.nextLine().trim();
                Account acc = findAccountById(id);
                if (acc == null) return;
                if (acc.isActive) {
                    accCtrl.deactivateAccount(acc, RAM_Accounts);
                    System.out.println("=> Deactivated.");
                } else {
                    accCtrl.activateAccount(acc, RAM_Accounts);
                    System.out.println("=> Activated.");
                }
                break;
            case 0:
            default:
                break;
        }
    }

    private static void manageEmployees() {
        AccountCtrl accCtrl = new AccountCtrl();
        System.out.println("\n--- EMPLOYEE MANAGEMENT ---");
        System.out.println("1. List Employees");
        System.out.println("2. Add Employee");
        System.out.println("3. Edit Employee");
        System.out.println("4. Deactivate Employee (lock all accounts)");
        System.out.println("0. Back");
        System.out.print("Choose: ");

        int c = inputInt();
        switch (c) {
            case 1:
                for (Staff s : RAM_Staffs) {
                    String status = (s.getEnDate() == null || s.getEnDate().equals("null")) ? "Active" : "Inactive";
                    System.out.println(s.staffID + " | " + s.getFullName() + " | " + status);
                }
                break;
            case 2:
                System.out.print("Full Name: ");
                String name = scanner.nextLine();
                System.out.print("DOB (yyyy-MM-dd): ");
                String dob = scanner.nextLine();
                System.out.print("Phone: ");
                String phone = scanner.nextLine();
                System.out.print("CCCD: ");
                String cccd = scanner.nextLine();
                System.out.print("Email: ");
                String email = scanner.nextLine();
                accCtrl.createStaff(name, dob, phone, cccd, email);
                reloadAccountsAndStaffs();
                System.out.println("=> Staff Added!");
                break;
            case 3:
                System.out.print("Enter Staff ID to Update: ");
                String sid = scanner.nextLine().trim();
                Staff target = findStaffByID(sid);
                if (target == null) return;
                System.out.print("New Name (blank skip): ");
                String n = scanner.nextLine();
                System.out.print("New Phone (blank skip): ");
                String p = scanner.nextLine();
                System.out.print("New CCCD (blank skip): ");
                String cc = scanner.nextLine();
                System.out.print("New Email (blank skip): ");
                String em = scanner.nextLine();
                accCtrl.updateStaffInfo(target, n, p, cc, em, RAM_Staffs);
                System.out.println("=> Staff Updated!");
                break;
            case 4:
                System.out.print("Enter Staff ID to Deactivate: ");
                String did = scanner.nextLine().trim();
                Staff s = findStaffByID(did);
                if (s == null) return;
                System.out.print("Confirm deactivate (y/n): ");
                if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
                    accCtrl.deactivateStaff(s, RAM_Accounts, RAM_Staffs);
                    System.out.println("=> Staff deactivated.");
                }
                break;
            case 0:
            default:
                break;
        }
    }

    private static void manageTours() {
        System.out.println("\n--- TOUR MANAGEMENT ---");
        System.out.println("1. View Tours");
        System.out.println("2. Add Tour");
        System.out.println("3. Edit Tour");
        System.out.println("0. Back");
        System.out.print("Choose: ");
        int c = inputInt();

        switch (c) {
            case 1:
                viewAllTours();
                break;
            case 2:
                createNewTour();
                reloadTours();
                break;
            case 3:
                updateTour();
                break;
            case 0:
            default:
                break;
        }
    }

    // ==========================================================
    // SALE MENU
    // ==========================================================
    private static void menuForSale() {
        showSaleNotificationBar();
        System.out.println("\n--- SALE MENU ---");
        System.out.println("1. Customer Management");
        System.out.println("2. Booking Management");
        System.out.println("3. Change Password");
        System.out.println("0. Logout");
        System.out.print("Choose: ");
        int choice = inputInt();
        switch (choice) {
            case 1:
                saleCustomerManagement();
                break;
            case 2:
                saleBookingManagement();
                break;
            case 3:
                handleChangePassword();
                break;
            case 0:
                forceLogout();
                break;
            default:
                break;
        }
    }

    private static void showSaleNotificationBar() {
        long count = 0;
        for (Booking b : RAM_Bookings) {
            if (b == null || b.isCancelled) continue;
            if (b.debtAmount <= 0) continue;
            Tour t = findTourById(b.tourID);
            if (t == null) continue;
            long daysToStart = ChronoUnit.DAYS.between(LocalDate.now(), t.startDate);
            if (daysToStart <= 10 && daysToStart >= 0) {
                count++;
            }
        }
        if (count > 0) {
            System.out.println("[NOTIFICATION] Customers chưa đóng hết tiền (<=10 ngày khởi hành): " + count);
        } else {
            System.out.println("[NOTIFICATION] No urgent unpaid bookings.");
        }
    }

    private static void saleCustomerManagement() {
        System.out.println("\n--- CUSTOMER MANAGEMENT ---");
        System.out.println("1. List Customers");
        System.out.println("2. Add Customer");
        System.out.println("3. Edit Customer");
        System.out.println("0. Back");
        System.out.print("Choose: ");
        int c = inputInt();
        switch (c) {
            case 1:
                for (Customer cu : RAM_Customers) {
                    System.out.println(cu.customerID + " | " + cu.getFullName() + " | " + cu.getPhoneNumber());
                }
                break;
            case 2:
                createNewCustomer();
                reloadCustomers();
                break;
            case 3:
                System.out.print("Customer ID: ");
                String cid = scanner.nextLine().trim();
                Customer target = findCustomerById(cid);
                if (target == null) return;
                System.out.print("New Name (blank skip): ");
                String n = scanner.nextLine();
                System.out.print("New Phone (blank skip): ");
                String p = scanner.nextLine();
                System.out.print("New Email (blank skip): ");
                String e = scanner.nextLine();
                CustomerCtrl.updateCustomerInfo(target, n, p, e, RAM_Customers);
                System.out.println("=> Customer updated.");
                break;
            case 0:
            default:
                break;
        }
    }

    private static void saleBookingManagement() {
        System.out.println("\n--- BOOKING MANAGEMENT ---");
        System.out.println("1. Create Booking");
        System.out.println("2. Update People in Booking");
        System.out.println("3. Cancel Booking (refund needs manager approval)");
        System.out.println("0. Back");
        System.out.print("Choose: ");
        int c = inputInt();
        switch (c) {
            case 1:
                createBooking();
                reloadBookings();
                reloadTours();
                break;
            case 2:
                updateBookingPeople();
                break;
            case 3:
                cancelBooking();
                break;
            case 0:
            default:
                break;
        }
    }

    // ==========================================================
    // ACCOUNTANT MENU
    // ==========================================================
    private static void menuForAccountant() {
        showAccountantNotificationBar();
        System.out.println("\n--- ACCOUNTANT MENU ---");
        System.out.println("1. Create Invoice (Payment)");
        System.out.println("2. Request Edit Invoice (need manager approval)");
        System.out.println("3. Reports");
        System.out.println("4. Change Password");
        System.out.println("0. Logout");
        System.out.print("Choose: ");
        int choice = inputInt();
        switch (choice) {
            case 1:
                createInvoice();
                break;
            case 2:
                requestEditInvoice();
                break;
            case 3:
                reportMenu();
                break;
            case 4:
                handleChangePassword();
                break;
            case 0:
                forceLogout();
                break;
            default:
                break;
        }
    }

    private static void showAccountantNotificationBar() {
        long pendingMine = 0;
        for (ApprovalRequest r : RAM_Requests) {
            if (r == null) continue;
            if (!"INVOICE_EDIT".equalsIgnoreCase(r.type)) continue;
            if (!"PENDING".equalsIgnoreCase(r.status)) continue;
            if (currentStaff != null && currentStaff.staffID != null && currentStaff.staffID.equals(r.createdByStaffId)) {
                pendingMine++;
            }
        }
        if (pendingMine > 0) {
            System.out.println("[NOTIFICATION] Your pending invoice edit requests: " + pendingMine);
        } else {
            System.out.println("[NOTIFICATION] No pending invoice edit requests.");
        }
    }

    // ==========================================================
    // TOUR helpers
    // ==========================================================
    private static void viewAllTours() {
        System.out.printf("%-10s %-20s %-12s %-10s %-12s\n", "ID", "Name", "Price", "Avail", "Start");
        for (Tour t : RAM_Tours) {
            int avail = t.getMaxPeople() - t.getBookedPeople();
            System.out.printf("%-10s %-20s %-12.0f %-10d %-12s\n", t.getTourID(), t.getDescription(), t.getPricePerPerson(), avail, t.getStartDate());
        }
    }

    private static void createNewTour() {
        try {
            System.out.print("Name: ");
            String desc = scanner.nextLine();
            System.out.print("Dep Point: ");
            String dep = scanner.nextLine();
            System.out.print("Price: ");
            double price = Double.parseDouble(scanner.nextLine());
            System.out.print("Max People: ");
            int max = Integer.parseInt(scanner.nextLine());
            System.out.print("Start (yyyy-MM-dd): ");
            String start = scanner.nextLine();
            System.out.print("End (yyyy-MM-dd): ");
            String end = scanner.nextLine();
            TourCtrl.createTour(desc, dep, price, max, start, end, desc);
            reloadTours(); // sync RAM with file
            System.out.println("=> Tour Created & Saved!");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void updateTour() {
        System.out.print("Enter Tour ID: ");
        String tid = scanner.nextLine().trim();
        Tour target = findTourById(tid);
        if (target == null) {
            System.out.println("Not Found!");
            return;
        }

        try {
            System.out.print("New Name: ");
            String desc = scanner.nextLine();
            System.out.print("New Dep: ");
            String dep = scanner.nextLine();
            System.out.print("New Price: ");
            double price = Double.parseDouble(scanner.nextLine());
            System.out.print("New Max: ");
            int max = Integer.parseInt(scanner.nextLine());
            System.out.print("New Start: ");
            String start = scanner.nextLine();
            System.out.print("New End: ");
            String end = scanner.nextLine();

            TourCtrl.updateTourInfo(target, desc, dep, price, max, start, end, target.getBookedPeople(), RAM_Tours);
            System.out.println("=> Tour Updated!");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    // ==========================================================
    // SALE helpers
    // ==========================================================
    private static void createNewCustomer() {
        System.out.print("Name: ");
        String n = scanner.nextLine();
        System.out.print("DOB (yyyy-MM-dd): ");
        String d = scanner.nextLine();
        System.out.print("Phone: ");
        String p = scanner.nextLine();
        System.out.print("Email: ");
        String e = scanner.nextLine();
        CustomerCtrl.createCustomer(n, d, p, e);
        System.out.println("=> Customer Added!");
    }

    private static void createBooking() {
        try {
            System.out.print("Customer ID: ");
            String cid = scanner.nextLine().trim();
            Customer cus = findCustomerById(cid);
            if (cus == null) {
                System.out.println("Customer Not Found!");
                return;
            }

            System.out.print("Tour ID: ");
            String tid = scanner.nextLine().trim();
            Tour tour = findTourById(tid);
            if (tour == null) {
                System.out.println("Tour Not Found!");
                return;
            }

            System.out.print("People: ");
            int num = Integer.parseInt(scanner.nextLine());
            int avail = tour.maxPeople - tour.bookedPeople;
            if (num > avail) {
                System.out.println("Not enough seats. Available: " + avail);
                return;
            }

            BookingCtrl.createBooking(cus, tour, num, currentStaff, RAM_Tours);
            System.out.println("=> Booking Success!");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void updateBookingPeople() {
        System.out.print("Booking ID: ");
        String bid = scanner.nextLine().trim();
        Booking b = findBookingById(bid);
        if (b == null) return;
        Tour t = findTourById(b.tourID);
        if (t == null) {
            System.out.println("Tour Not Found!");
            return;
        }
        System.out.print("New people: ");
        int newPeople = inputInt();
        try {
            BookingCtrl.updateBookingPeople(b, t, newPeople, RAM_Bookings, RAM_Tours);
            System.out.println("=> Updated people in booking.");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void cancelBooking() {
        System.out.print("Booking ID: ");
        String bid = scanner.nextLine().trim();
        Booking b = findBookingById(bid);
        if (b == null) return;
        Tour t = findTourById(b.tourID);
        if (t == null) {
            System.out.println("Tour Not Found!");
            return;
        }
        System.out.print("Reason: ");
        String reason = scanner.nextLine();

        try {
            BookingCtrl.CancelResult res = BookingCtrl.cancelBooking(b, t, reason, RAM_Bookings, RAM_Tours);
            System.out.println("=> Cancelled. DaysToStart=" + res.daysToStart + " | penaltyRate=" + (res.rate * 100) + "% | penalty=" + res.penaltyAmount + " | refund=" + res.refundAmount);

            if (res.refundAmount > 0) {
                ApprovalRequest r = ApprovalRequestCtrl.createRefundRequest(b.bookingID, res.refundAmount, res.penaltyAmount,
                        reason, currentStaff.getStaffID(), currentAccount.role, RAM_Requests);
                System.out.println("=> Refund request sent to Manager. RequestId=" + r.requestId);
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    // ==========================================================
    // ACCOUNTANT helpers
    // ==========================================================
    private static void createInvoice() {
        try {
            System.out.print("Booking ID: ");
            String bid = scanner.nextLine().trim();
            Booking b = findBookingById(bid);
            if (b == null) return;

            System.out.println("Total: " + b.totalAmount + " | Debt: " + b.debtAmount);
            System.out.print("Pay Amount: ");
            double amt = Double.parseDouble(scanner.nextLine());

            InvoiceCtrl.createInvoice(b, amt, RAM_Bookings);
            System.out.println("=> Paid!");
            reloadInvoices();
            reloadBookings();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void requestEditInvoice() {
        System.out.println("\n--- INVOICE LIST ---");
        for (Invoice i : RAM_Invoices) {
            System.out.println(i.invoiceID + " | booking=" + i.bookingID + " | amount=" + i.totalAmount + " | created=" + i.createdAt);
        }
        System.out.print("Invoice ID: ");
        String iid = scanner.nextLine().trim();
        Invoice inv = findInvoiceById(iid);
        if (inv == null) return;

        System.out.print("New amount: ");
        double newAmt = Double.parseDouble(scanner.nextLine());
        System.out.print("Reason: ");
        String reason = scanner.nextLine();

        ApprovalRequest r = ApprovalRequestCtrl.createInvoiceEditRequest(inv.invoiceID, newAmt, reason, currentStaff.getStaffID(), currentAccount.role, RAM_Requests);
        System.out.println("=> Sent request to Manager. RequestId=" + r.requestId);
    }

    private static void reportMenu() {
        System.out.println("\n--- REPORTS ---");
        System.out.println("1. List reports");
        System.out.println("2. Create revenue report");
        System.out.println("3. Export report to CSV");
        System.out.println("0. Back");
        System.out.print("Choose: ");
        int c = inputInt();
        switch (c) {
            case 1:
                for (Report r : RAM_Reports) {
                    System.out.println(r.reportId + " | " + r.type + " | " + r.fromDate + ".." + r.toDate + " | revenue=" + r.totalRevenue + " | file=" + r.exportedFile);
                }
                break;
            case 2:
                System.out.print("From (yyyy-MM-dd): ");
                LocalDate from = LocalDate.parse(scanner.nextLine().trim());
                System.out.print("To (yyyy-MM-dd): ");
                LocalDate to = LocalDate.parse(scanner.nextLine().trim());
                Report rep = ReportCtrl.createRevenueReport(from, to, currentStaff.getStaffID(), RAM_Invoices, RAM_Bookings, RAM_Reports);
                System.out.println("=> Report created: " + rep.reportId + " | revenue=" + rep.totalRevenue);
                break;
            case 3:
                System.out.print("Report ID: ");
                String rid = scanner.nextLine().trim();
                Report target = findReportById(rid);
                if (target == null) return;
                try {
                    java.io.File f = ReportCtrl.exportReportToCsv(target, RAM_Invoices);
                    ReportRepository.saveToFile(RAM_Reports);
                    System.out.println("=> Exported: " + f.getAbsolutePath());
                } catch (Exception e) {
                    System.err.println("Error: " + e.getMessage());
                }
                break;
            case 0:
            default:
                break;
        }
    }

    // ==========================================================
    // HELPERS
    // ==========================================================
    private static int inputInt() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (Exception e) {
            return -1;
        }
    }

    private static Staff findStaffByID(String id) {
        for (Staff s : RAM_Staffs) {
            if (s != null && s.staffID != null && s.staffID.equals(id)) return s;
        }
        System.out.println("Staff ID Not Found!");
        return null;
    }

    private static Account findAccountById(String id) {
        for (Account a : RAM_Accounts) {
            if (a != null && a.accountID != null && a.accountID.equals(id)) return a;
        }
        System.out.println("Account ID Not Found!");
        return null;
    }

    private static boolean isUsernameTaken(String username) {
        if (username == null) return false;
        for (Account a : RAM_Accounts) {
            if (a != null && a.username != null && a.username.equalsIgnoreCase(username)) return true;
        }
        return false;
    }

    private static boolean isUsernameTakenExcept(String username, String exceptAccountId) {
        if (username == null) return false;
        for (Account a : RAM_Accounts) {
            if (a == null || a.username == null) continue;
            if (exceptAccountId != null && exceptAccountId.equals(a.accountID)) continue;
            if (a.username.equalsIgnoreCase(username)) return true;
        }
        return false;
    }

    private static Tour findTourById(String id) {
        for (Tour t : RAM_Tours) {
            if (t != null && t.tourID != null && t.tourID.equals(id)) return t;
        }
        return null;
    }

    private static Customer findCustomerById(String id) {
        for (Customer c : RAM_Customers) {
            if (c != null && c.customerID != null && c.customerID.equals(id)) return c;
        }
        System.out.println("Customer ID Not Found!");
        return null;
    }

    private static Booking findBookingById(String id) {
        for (Booking b : RAM_Bookings) {
            if (b != null && b.bookingID != null && b.bookingID.equals(id)) return b;
        }
        System.out.println("Booking ID Not Found!");
        return null;
    }

    private static Invoice findInvoiceById(String id) {
        for (Invoice i : RAM_Invoices) {
            if (i != null && i.invoiceID != null && i.invoiceID.equals(id)) return i;
        }
        System.out.println("Invoice ID Not Found!");
        return null;
    }

    private static Report findReportById(String id) {
        for (Report r : RAM_Reports) {
            if (r != null && r.reportId != null && r.reportId.equalsIgnoreCase(id)) return r;
        }
        System.out.println("Report Not Found!");
        return null;
    }

    private static void checkAndCreateDefaultAdmin() {
        if (RAM_Accounts == null || RAM_Accounts.isEmpty()) {
            System.out.println("--> Creating Default Admin...");
            Staff adminStaff = new Staff("Admin", "2000-01-01", "0900", "001", "admin@sys.com");
            StaffRepository.appendStaffToFile(adminStaff);
            Account adminAccount = new Account("admin", "123456", "admin", adminStaff.getStaffID());
            AccountRepository.appendAccountToFile(adminAccount);
            reloadAccountsAndStaffs();
            System.out.println("--> Done. User: admin / Pass: 123456");
        }
    }
}
