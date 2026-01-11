package ctrl;

import java.time.LocalDate;
import java.util.List;

import model.Booking;
import model.Invoice;
import repository.InvoiceRepository;

public class InvoiceCtrl {
    // Lập hóa đơn (thanh toán)
    public static void createInvoice(Booking booking, double paidAmount, List<Booking> bookings) {
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
        Invoice newInvoice = new Invoice(booking.getBookingID(), paidAmount);
        InvoiceRepository.appendInvoiceToFile(newInvoice);

        // Persist booking changes
        BookingCtrl.updateBookingPayment(booking, booking.totalAmount, booking.debtAmount, bookings);
    }

    // Manager duyệt và áp dụng sửa hóa đơn
    public static void applyInvoiceEdit(Invoice invoice, double newTotalAmount, String note, String managerStaffId, List<Invoice> invoices) {
        invoice.totalAmount = newTotalAmount;
        invoice.lastModifiedAt = LocalDate.now();
        invoice.modifiedBy = managerStaffId;
        invoice.note = note;
        InvoiceRepository.saveInvoicesToFile(invoices);
    }
}
