package ctrl;

import java.time.LocalDate;
import java.util.List;

import model.ApprovalRequest;
import model.Booking;
import model.Invoice;
import repository.ApprovalRequestRepository;

public class ApprovalRequestCtrl {
    public static ApprovalRequest createRefundRequest(String bookingId, double refundAmount, double penaltyAmount, String reason, String byStaffId, String byRole, List<ApprovalRequest> requestCache) {
        ApprovalRequest r = ApprovalRequest.createRefund(bookingId, refundAmount, penaltyAmount, reason, byStaffId, byRole);
        requestCache.add(r);
        ApprovalRequestRepository.saveToFile(requestCache);
        return r;
    }

    public static ApprovalRequest createInvoiceEditRequest(String invoiceId, double newAmount, String reason, String byStaffId, String byRole, List<ApprovalRequest> requestCache) {
        ApprovalRequest r = ApprovalRequest.createInvoiceEdit(invoiceId, newAmount, reason, byStaffId, byRole);
        requestCache.add(r);
        ApprovalRequestRepository.saveToFile(requestCache);
        return r;
    }

    public static void approveRequest(ApprovalRequest req, String managerStaffId, String managerNote,
                                      List<ApprovalRequest> requestCache,
                                      List<Invoice> invoices,
                                      List<Booking> bookings) {
        if (!"PENDING".equalsIgnoreCase(req.status)) {
            throw new IllegalStateException("Yêu cầu không ở trạng thái PENDING");
        }

        req.status = "APPROVED";
        req.processedAt = LocalDate.now();
        req.processedByStaffId = managerStaffId;
        req.managerNote = managerNote;

        // apply actions
        if ("INVOICE_EDIT".equalsIgnoreCase(req.type)) {
            Invoice inv = findInvoice(req.invoiceId, invoices);
            if (inv == null) {
                throw new IllegalStateException("Không tìm thấy hóa đơn: " + req.invoiceId);
            }
            InvoiceCtrl.applyInvoiceEdit(inv, req.newInvoiceAmount, "Approved: " + safe(managerNote), managerStaffId, invoices);
        }
        // REFUND: currently only records approval. (Thực tế chi trả do kế toán/manager xử lý.)

        ApprovalRequestRepository.saveToFile(requestCache);
    }

    public static void rejectRequest(ApprovalRequest req, String managerStaffId, String managerNote, List<ApprovalRequest> requestCache) {
        if (!"PENDING".equalsIgnoreCase(req.status)) {
            throw new IllegalStateException("Yêu cầu không ở trạng thái PENDING");
        }
        req.status = "REJECTED";
        req.processedAt = LocalDate.now();
        req.processedByStaffId = managerStaffId;
        req.managerNote = managerNote;
        ApprovalRequestRepository.saveToFile(requestCache);
    }

    private static Invoice findInvoice(String id, List<Invoice> invoices) {
        if (id == null || invoices == null) return null;
        for (Invoice i : invoices) {
            if (i != null && id.equals(i.invoiceID)) return i;
        }
        return null;
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }
}
