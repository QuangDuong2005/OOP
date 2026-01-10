package model;
import java.time.LocalDate;
public class Invoice {
    // mã hóa đơn, mã booking, ngày tạo hóa đơn, tổng tiền
    public String invoiceID;
    public String bookingID;    
    public LocalDate createdAt;
    public double totalAmount;
    public static int numberInvoices = 0;
    // Constructor rỗng
    public Invoice(){};
    // Constructor khi tạo hóa đơn mới
    public Invoice(String bookingID, double totalAmount) {
        // Mã hóa đơn là theo định dang I001, I002, ...
        this.invoiceID = "I" + String.format("%03d", ++numberInvoices); 
        this.bookingID = bookingID;
        this.createdAt = LocalDate.now();
        this.totalAmount = totalAmount;
    }
    // Getters
    public String getInvoiceID() {
        return invoiceID;
    }  
    public String getBookingID() {
        return bookingID;
    }
    public String getCreatedAt() {
        return createdAt.toString();
    }
    public double getTotalAmount() {
        return totalAmount;
    }
    // Setters 
    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
    public void setBookingID(String bookingID) {
        this.bookingID = bookingID;
    }
    public void setCreatedAt(String createdAtString) {
        this.createdAt = LocalDate.parse(createdAtString);
    }
    public void setInvoiceID(String invoiceID) {
        this.invoiceID = invoiceID;
    }
    
}