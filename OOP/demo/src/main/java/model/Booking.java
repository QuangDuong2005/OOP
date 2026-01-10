package model;
import java.time.LocalDate;
public class Booking {
    // mã booking, mã khách hàng, mã tour, số người, ngày đặt, người tạo booking, Tổng số tiền, dư nợ, trạng thái(đang khởi tạo khi số tiền < 1 000 000 đồng, đã đặt cọc khi số tiền >= 100 000 đồng, đã thanh toán khi số tiền = tổng số tiền, )
    public String bookingID;
    public String customerID;
    public String tourID;
    public int numberOfPeople;
    public LocalDate bookingDate; 
    public String createdBy; // ID nhân viên tạo booking
    public double totalAmount;
    public double debtAmount;
    public static int numberBookings = 0;   
    public String status;
    // Hàm tính status booking
    public String calculateStatus() {
        if (this.debtAmount == 0) {
            this.status = "Đã thanh toán";
        } else if (this.totalAmount - this.debtAmount >= 1000000) {
            this.status = "Đã đặt cọc";
        } else {
            this.status = "Đang khởi tạo";
        }
        return this.status;
    }
    // Contructor rỗng
    public Booking(){};
    // Constructor khi tạo booking mới
    public Booking(String customerID, String tourID, int numberOfPeople, String createdBy, double totalAmount, double debtAmount) {
        this.bookingID = "B" + String.format("%03d", ++numberBookings);
        this.customerID = customerID;
        this.tourID = tourID;
        this.numberOfPeople = numberOfPeople;
        this.bookingDate = LocalDate.now();
        this.createdBy = createdBy;
        this.totalAmount = totalAmount;
        this.debtAmount = debtAmount;
        this.status = calculateStatus();
    }
    // Getters
    public String getBookingID() {
        return bookingID;
    }
    public String getCustomerID() {
        return customerID;
    }
    public String getTourID() {
        return tourID;
    }
    public int getNumberOfPeople() {
        return numberOfPeople;
    }
    public String getBookingDate() {
        return bookingDate.toString();
    }
    public String getCreatedBy() {
        return createdBy;
    }
    public double getTotalAmount() {
        return totalAmount;
    }
    public double getDebtAmount() {
        return debtAmount;
    }
    public String getStatus() {
        return status;
    }
    //setters
    public void setNumberOfPeople(int numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }
    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
    public void setDebtAmount(double debtAmount) {
        this.debtAmount = debtAmount;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }
    public void setTourID(String tourID) {
        this.tourID = tourID;
    }
    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }
    public void setBookingID(String bookingID) {
        this.bookingID = bookingID;
    }
    // Hàm khi thanh toán hóa đơn
    public void payInvoice(double amountPaid) {
        this.debtAmount -= amountPaid;
        this.status = calculateStatus();
    }
}
