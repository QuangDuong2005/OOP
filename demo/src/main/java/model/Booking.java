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
    public String status;

    // Huỷ booking / hoàn tiền
    public boolean isCancelled;
    public LocalDate cancelledAt;
    public String cancellationReason;
    public double cancellationPenalty;
    public double refundAmount;
    // Hàm tính status booking
    public String calculateStatus() {
        if (this.isCancelled) {
            this.status = "Đã hủy";
            return this.status;
        }
        if (this.debtAmount == 0) {
            this.status = "Đã thanh toán";
        } else if (this.totalAmount - this.debtAmount >= this.totalAmount*0.1) {
            this.status = "Đã đặt cọc";
        } else {
            this.status = "Đang khởi tạo";
        }
        return this.status;
    }
    // Contructor rỗng
    public Booking(){};
    // Constructor khi tạo booking mới
    public Booking(String ID, String customerID, String tourID, int numberOfPeople, String createdBy, double totalAmount, double debtAmount) {
        this.bookingID = ID;
        this.customerID = customerID;
        this.tourID = tourID;
        this.numberOfPeople = numberOfPeople;
        this.bookingDate = LocalDate.now();
        this.createdBy = createdBy;
        this.totalAmount = totalAmount;
        this.debtAmount = debtAmount;

        this.isCancelled = false;
        this.cancelledAt = null;
        this.cancellationReason = null;
        this.cancellationPenalty = 0;
        this.refundAmount = 0;
        this.status = this.calculateStatus();
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

    public boolean getIsCancelled() {
        return isCancelled;
    }

    public String getCancelledAt() {
        return cancelledAt == null ? "null" : cancelledAt.toString();
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public double getCancellationPenalty() {
        return cancellationPenalty;
    }

    public double getRefundAmount() {
        return refundAmount;
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

    public void setIsCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    public void setCancelledAt(String cancelledAtString) {
        if (cancelledAtString == null || cancelledAtString.equals("null") || cancelledAtString.isEmpty()) {
            this.cancelledAt = null;
        } else {
            this.cancelledAt = LocalDate.parse(cancelledAtString);
        }
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public void setCancellationPenalty(double cancellationPenalty) {
        this.cancellationPenalty = cancellationPenalty;
    }

    public void setRefundAmount(double refundAmount) {
        this.refundAmount = refundAmount;
    }
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    public void setBookingDate(String bookingDateString) {
        this.bookingDate = LocalDate.parse(bookingDateString);
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

    public double getPaidAmount() {
        return Math.max(0, this.totalAmount - this.debtAmount);
    }
}
