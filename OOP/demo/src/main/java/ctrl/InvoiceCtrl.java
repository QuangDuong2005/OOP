package ctrl;
import model.Booking;
import model.Invoice;
import repository.InvoiceRepository;
public class InvoiceCtrl {
    // Tạo hóa đơn mới khi biết booking, số tiền đã thanh toán, ngày tạo hóa đơn
    public static void createInvoice(Booking booking, double paidAmount) {
        // Tính tổng số tiền đã thanh toán
        double totalPaid = booking.totalAmount - booking.debtAmount + paidAmount;
        // Cập nhật dư nợ trong booking
        booking.debtAmount = booking.totalAmount - totalPaid;
        booking.calculateStatus();
        // Tạo hóa đơn mới
        Invoice newInvoice = new Invoice(booking.getBookingID(), paidAmount);
        // Cập nhật lại file hóa đơn và booking
        InvoiceRepository.appendInvoiceToFile(newInvoice);
        // Lưu lại booking đã cập nhật
        BookingCtrl.updateBookingInfo(booking, booking.totalAmount, booking.debtAmount);
    }
}
