package model;

public class BookingCancelRequest extends ApprovalRequest {

    private double refundAmount;
    private double penaltyAmount;

    public BookingCancelRequest() {}

    public BookingCancelRequest(String bookingId,
                                double refundAmount,
                                double penaltyAmount,
                                String staffId,
                                String reason) {
        super(bookingId, staffId, "Staff", reason);
        this.refundAmount = refundAmount;
        this.penaltyAmount = penaltyAmount;
    }

    // ===== GETTERS & SETTERS =====

    public double getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(double refundAmount) {
        this.refundAmount = refundAmount;
    }

    public double getPenaltyAmount() {
        return penaltyAmount;
    }

    public void setPenaltyAmount(double penaltyAmount) {
        this.penaltyAmount = penaltyAmount;
    }
}
