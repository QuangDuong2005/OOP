package ctrl;

import java.util.List;

import model.Booking;
import model.Invoice;
import model.InvoiceEditRequest;
import repository.BookingRepository;
import repository.InvoiceEditRequestRepository;
import repository.InvoiceRepository;
public class InvoiceCtrl {
    List<Invoice> invoices = InvoiceRepository.loadInvoicesFromFile();
    // Lập hóa đơn (thanh toán)
    public void createInvoice(String bookingID, double paidAmount, String invoiceID) {
        List<Booking> bookings = BookingRepository.loadBookingsFromFile();
        for (Booking booking:bookings){
            if ((booking.getBookingID()).equals(bookingID)){
                if (paidAmount <= 0) {
                    throw new IllegalArgumentException("Số tiền thanh toán phải > 0");
                }
                if (booking.isCancelled) {
                    throw new IllegalStateException("Booking đã bị hủy");
                }

                // Update debt
                double totalPaid = booking.totalAmount - booking.debtAmount + paidAmount;
                booking.debtAmount = Math.max(0, booking.totalAmount - totalPaid);
                booking.calculateStatus();

                // Create invoice
                Invoice newInvoice = new Invoice(invoiceID,booking.getBookingID(), paidAmount);
                invoices.add(newInvoice);
                InvoiceRepository.saveInvoicesToFile(invoices);

                // Persist booking changes
                BookingRepository.saveBookingsToFile(bookings);
                break;
            }
        }
    }

    // Sửa hóa đơn để gửi Quản lý
    public void InvoiceEdit(String invoiceID, double newTotalAmount, String reason, String staffID) {
        List<InvoiceEditRequest> invoiceEditRequests = InvoiceEditRequestRepository.loadFromFile();
        InvoiceEditRequest req = new InvoiceEditRequest(invoiceID, newTotalAmount, staffID, reason);
        invoiceEditRequests.add(req);
        InvoiceEditRequestRepository.saveToFile(invoiceEditRequests);
    }
}
