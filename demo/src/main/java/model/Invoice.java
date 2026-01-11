package model;
import java.time.LocalDate;
public class Invoice {
    // mã hóa đơn, mã booking, ngày tạo hóa đơn, tổng tiền
    public String invoiceID;
    public String bookingID;    
    public LocalDate createdAt;
    public double totalAmount;

    // Audit
    public LocalDate lastModifiedAt;
    public String modifiedBy; // staffId
    public String note;
    // Constructor rỗng
    public Invoice(){};
    // Constructor khi tạo hóa đơn mới
    public Invoice(String ID, String bookingID, double totalAmount) {
        // Mã hóa đơn là theo định dang I001, I002, ...
        this.invoiceID = ID; 
        this.bookingID = bookingID;
        this.createdAt = LocalDate.now();
        this.totalAmount = totalAmount;

        this.lastModifiedAt = null;
        this.modifiedBy = null;
        this.note = null;
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

    public String getLastModifiedAt() {
        return lastModifiedAt == null ? "null" : lastModifiedAt.toString();
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public String getNote() {
        return note;
    }
    // Setters 
    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setLastModifiedAt(String lastModifiedAtString) {
        if (lastModifiedAtString == null || lastModifiedAtString.equals("null") || lastModifiedAtString.isEmpty()) {
            this.lastModifiedAt = null;
        } else {
            this.lastModifiedAt = LocalDate.parse(lastModifiedAtString);
        }
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public void setNote(String note) {
        this.note = note;
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