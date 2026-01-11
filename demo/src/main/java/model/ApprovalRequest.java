package model;

import java.time.LocalDate;

/**
 * Simple approval workflow for actions that require Manager approval.
 */
public class ApprovalRequest {
    public static int numberRequests = 0;

    public String requestId;
    public String type; // INVOICE_EDIT, REFUND
    public String status; // PENDING, APPROVED, REJECTED
    public LocalDate createdAt;
    public String createdByStaffId;
    public String createdByRole;

    // For invoice edit
    public String invoiceId;
    public double newInvoiceAmount;

    // For refund request (booking cancel)
    public String bookingId;
    public double refundAmount;
    public double penaltyAmount;

    public String reason;

    // Processed
    public LocalDate processedAt;
    public String processedByStaffId;
    public String managerNote;

    public ApprovalRequest() {}

    public static ApprovalRequest createInvoiceEdit(String invoiceId, double newAmount, String reason, String byStaffId, String byRole) {
        ApprovalRequest r = new ApprovalRequest();
        r.requestId = "R" + String.format("%03d", ++numberRequests);
        r.type = "INVOICE_EDIT";
        r.status = "PENDING";
        r.createdAt = LocalDate.now();
        r.createdByStaffId = byStaffId;
        r.createdByRole = byRole;
        r.invoiceId = invoiceId;
        r.newInvoiceAmount = newAmount;
        r.reason = reason;
        return r;
    }

    public static ApprovalRequest createRefund(String bookingId, double refundAmount, double penaltyAmount, String reason, String byStaffId, String byRole) {
        ApprovalRequest r = new ApprovalRequest();
        r.requestId = "R" + String.format("%03d", ++numberRequests);
        r.type = "REFUND";
        r.status = "PENDING";
        r.createdAt = LocalDate.now();
        r.createdByStaffId = byStaffId;
        r.createdByRole = byRole;
        r.bookingId = bookingId;
        r.refundAmount = refundAmount;
        r.penaltyAmount = penaltyAmount;
        r.reason = reason;
        return r;
    }
}
