package ctrl;

import java.time.LocalDate;
import java.util.List;

import model.BookingCancelRequest;
import model.Invoice;
import model.InvoiceEditRequest;
import repository.BookingCancelRequestRepository;
import repository.InvoiceEditRequestRepository;
import repository.InvoiceRepository;

public class ApprovalCtrl {

    private List<InvoiceEditRequest> invoiceRequests = InvoiceEditRequestRepository.loadFromFile();
    private List<BookingCancelRequest> bookingRequests = BookingCancelRequestRepository.loadFromFile();

    // ================== APPROVE ==================
    // Đồng ý sửa hóa đơn 
    public void approveInvoiceEdit(String requestId, String managerId) {
        List<Invoice> invoices = InvoiceRepository.loadInvoicesFromFile();
        for (InvoiceEditRequest req: invoiceRequests){
            if ((req.getRequestId()).equals(requestId)){
                // 1. Cập nhật hóa đơn
                for (Invoice invoice: invoices){
                    if ((invoice.getInvoiceID()).equals(requestId)){
                        invoice.setTotalAmount(req.getNewAmount());
                        invoice.setLastModifiedAt(LocalDate.now().toString());
                        invoice.setModifiedBy(managerId);
                        invoice.setNote("Approved invoice edit request ");

                        // 2. Cập nhật request
                        req.setStatus("APPROVED");
                        req.setProcessedAt((LocalDate.now()).toString());
                        req.setProcessedByStaffId(managerId);

                        // 3. Lưu
                        InvoiceRepository.saveInvoicesToFile(invoices);
                        InvoiceEditRequestRepository.saveToFile(invoiceRequests);
                        break;
                    }
                }
            }
        }
    }


    // ================== REJECT ==================

    public boolean reject(String requestId, String managerStaffId, String note) {
        // Invoice Edit
        for (InvoiceEditRequest req : invoiceRequests) {
            if (req.getRequestId().equals(requestId) && req.getStatus().equals("PENDING")) {
                req.setStatus("REJECTED");
                req.setProcessedAt(LocalDate.now().toString());
                req.setProcessedByStaffId(managerStaffId);
                req.setManagerNote(note);

                InvoiceEditRequestRepository.saveToFile(invoiceRequests);
                return true;
            }
        }

        // Booking Cancel
        for (BookingCancelRequest req : bookingRequests) {
            if (req.getRequestId().equals(requestId) && req.getStatus().equals("PENDING")) {
                req.setStatus("REJECTED");
                req.setProcessedAt(LocalDate.now().toString());
                req.setProcessedByStaffId(managerStaffId);
                req.setManagerNote(note);

                BookingCancelRequestRepository.saveToFile(bookingRequests);
                return true;
            }
        }

        return false;
    }
}
