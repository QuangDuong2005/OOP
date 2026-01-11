package model;

public class InvoiceEditRequest extends ApprovalRequest {

    private double newAmount;

    public InvoiceEditRequest() {}

    public InvoiceEditRequest(String invoiceId, double newAmount,
                              String staffId, String reason) {
        super(invoiceId, staffId, "Accountant", reason);
        this.newAmount = newAmount;
    }

    // ===== GETTERS & SETTERS =====

    public double getNewAmount() {
        return newAmount;
    }

    public void setNewAmount(double newAmount) {
        this.newAmount = newAmount;
    }
}
