package ctrl;

import java.time.LocalDate;
import java.util.List;

import model.Booking;
import model.BookingCancelRequest;
import model.Invoice;
import model.InvoiceEditRequest;
import repository.BookingCancelRequestRepository;
import repository.BookingRepository;
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
    public void approveBookingCancel(String requestId, String managerId) {
        // Load danh sách Booking và Request
        List<Booking> bookings = BookingRepository.loadBookingsFromFile();

        for (BookingCancelRequest req : bookingRequests) {
            // Tìm request đang PENDING
            if (req.getRequestId().equals(requestId) && "PENDING".equalsIgnoreCase(req.getStatus())) {
                // 1. Cập nhật Booking
                for (Booking b : bookings) {
                    if (b.getBookingID().equals(requestId)) { // requestId chính là bookingId
                        // Cập nhật trạng thái hủy và ghi chú
                        b.setIsCancelled(true); 
                        // b.setCancellationReason("Approved by " + managerId); // Nếu có trường này
                        break;
                    }
                }
                // 2. Cập nhật Request
                req.setStatus("APPROVED");
                req.setProcessedAt(LocalDate.now().toString());
                req.setProcessedByStaffId(managerId);

                // 3. Lưu lại
                BookingRepository.saveBookingsToFile(bookings);
                BookingCancelRequestRepository.saveToFile(bookingRequests);
                System.out.println("Đã duyệt hủy phòng thành công.");
                break;
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
