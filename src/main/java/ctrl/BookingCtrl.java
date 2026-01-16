package ctrl;

import exception.DuplicateException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import model.Booking;
import model.BookingCancelRequest;
import model.Tour;
import repository.BookingCancelRequestRepository;
import repository.BookingRepository;
import repository.TourRepository;

public class BookingCtrl {
    public final int UPDATE_PEOPLE_CUTOFF_DAYS = 7; // <=7 days to start => lock
    List<Booking> bookings = BookingRepository.loadBookingsFromFile();
    // Tạo booking mới khi biết khách hàng, tour và người tạo booking
    public void createBooking(String bookingID, String customerID, String tourID, int numberOfPeople, String staffID) throws DuplicateException, IllegalArgumentException {
        List<Tour> tours = TourRepository.loadToursFromFile();
        // Tìm tour với ID 
        Tour t = null;
        for (Tour tour: tours){
            if ((tour.getTourID()).equals(tourID)){
                t = tour;
                break;
            }
        }
        for (Booking booking: bookings){
            if ((booking.getBookingID()).equals(bookingID)){
                throw new DuplicateException("Đã có ID booking này");
            }
        }
        if (numberOfPeople > t.getMaxPeople() - t.getBookedPeople()){
            String mess = "Số tour chỉ còn lại " + (t.getMaxPeople() - t.getBookedPeople()) + "chỗ";
            throw new IllegalArgumentException(mess);
        }
        Booking newBooking = new Booking( bookingID,
                customerID,
                tourID,
                numberOfPeople,
                staffID, t.getPricePerPerson() * numberOfPeople, t.getPricePerPerson() * numberOfPeople);
        bookings.add(newBooking);
        BookingRepository.saveBookingsToFile(bookings);

        t.updateBookedPeople(numberOfPeople);
        TourRepository.saveToursToFile(tours);

    }

    // Thay đổi số người trong booking
    public void updateBookingPeople(String bookingID, String tourID, int newPeople) throws IllegalArgumentException,IllegalStateException {
        for (Booking booking: bookings){
            if ((booking.getBookingID()).equals(bookingID)){
                if (newPeople <= 0) {
                    throw new IllegalArgumentException("Số người phải > 0");
                }

                int diff = newPeople - booking.numberOfPeople;
                // Tìm kiếm tour theo booking
                List<Tour> tours = TourRepository.loadToursFromFile();
                Tour targetTour = null;
                for (Tour tour:tours){
                    if ((tour.getTourID()).equals(tourID)){
                        targetTour = tour;
                        break;
                    }
                }
                
                LocalDate start = targetTour.startDate;
                long daysToStart = ChronoUnit.DAYS.between(LocalDate.now(), start);
                if (daysToStart <= UPDATE_PEOPLE_CUTOFF_DAYS) {
                    throw new IllegalStateException("Không thể sửa số người khi còn " + daysToStart + " ngày là khởi hành (<= " + UPDATE_PEOPLE_CUTOFF_DAYS + ")");
                }
                if (targetTour == null) {
                    throw new IllegalStateException("Không tìm thấy tour trong hệ thống");
                }
                int available = targetTour.maxPeople - targetTour.bookedPeople;
                if (diff > 0 && available < diff) {
                    throw new IllegalStateException("Không đủ chỗ trống. Còn trống: " + available);
                }
                if (booking.isCancelled) {
                    throw new IllegalStateException("Booking đã bị hủy");
                }
                // update tour booked people
                targetTour.bookedPeople += diff;
                TourRepository.saveToursToFile(tours);

                // update booking amounts
                double paid = booking.paidAmount();
                booking.numberOfPeople = newPeople;
                booking.totalAmount = targetTour.pricePerPerson * newPeople;
                booking.debtAmount = Math.max(0, booking.totalAmount - paid);
                booking.calculateStatus();

                BookingRepository.saveBookingsToFile(bookings);
            }
        }

    }

    // Hủy booking thì tính vé phạt
    public void cancelBooking(String bookingID, String tourID, String reason) throws IllegalStateException {
        for (Booking booking: bookings){
            if ((booking.getBookingID()).equals(bookingID)){
                Tour t = null;
                List<Tour> tours = TourRepository.loadToursFromFile();
                // Tìm tour đó 
                for (Tour tour: tours){
                    if ((tour.getTourID()).equals(tourID)){
                        t = tour;
                        break;
                    }
                }
                if (booking.isCancelled) {
                    throw new IllegalStateException("Booking đã bị hủy trước đó");
                }
                LocalDate today = LocalDate.now();
                if (!today.isBefore(t.startDate)) {
                    throw new IllegalStateException("Không thể hủy khi tour đã/đang khởi hành");
                }
                // Tạm tính tiền phạt 
                long daysToStart = ChronoUnit.DAYS.between(today, t.startDate);
                double totalAmount = booking.getTotalAmount();

                double rate = cancellationPenaltyRate(daysToStart);
                double penalty = Math.min(totalAmount - booking.getDebtAmount(), totalAmount * rate); // Lấy min tiền hoặc bị phạt
                double refund = Math.max(0, totalAmount - booking.getDebtAmount() - penalty); // Số tiền phải hoàn trả

                booking.isCancelled = true;
                booking.cancelledAt = today;
                booking.cancellationReason = reason;
                booking.cancellationPenalty = penalty;
                booking.refundAmount = refund;
                booking.calculateStatus();

                // Viết lại số chỗ còn trống vào tour
                t.setBookedPeople(t.getBookedPeople()-booking.getNumberOfPeople());
                BookingRepository.saveBookingsToFile(bookings);
                // Gửi yêu cầu cho 
                List<BookingCancelRequest> reqs = BookingCancelRequestRepository.loadFromFile();
                BookingCancelRequest req = new BookingCancelRequest(bookingID, refund, penalty, booking.getCreatedBy(),reason);
                reqs.add(req);
                BookingCancelRequestRepository.saveToFile(reqs);
                break;
            }
        }
    }
    // Hàm hủy tự tính VD hủy do công ty
    public void cancelBooking(String bookingID, String tourID, String reason, double penalty, double refund) throws IllegalStateException {
        for (Booking booking: bookings){
            if ((booking.getBookingID()).equals(bookingID)){
                Tour t = null;
                List<Tour> tours = TourRepository.loadToursFromFile();
                // Tìm tour đó 
                for (Tour tour: tours){
                    if ((tour.getTourID()).equals(tourID)){
                        t = tour;
                        break;
                    }
                }
                if (booking.isCancelled) {
                    throw new IllegalStateException("Booking đã bị hủy trước đó");
                }
                LocalDate today = LocalDate.now();
                if (!today.isBefore(t.startDate)) {
                    throw new IllegalStateException("Không thể hủy khi tour đã/đang khởi hành");
                }

                booking.isCancelled = true;
                booking.cancelledAt = today;
                booking.cancellationReason = reason;
                booking.cancellationPenalty = penalty;
                booking.refundAmount = refund;
                booking.calculateStatus();

                // Viết lại số chỗ còn trống vào tour
                t.setBookedPeople(t.getBookedPeople()-booking.getNumberOfPeople());
                BookingRepository.saveBookingsToFile(bookings);
                // Gửi yêu cầu cho 
                List<BookingCancelRequest> reqs = BookingCancelRequestRepository.loadFromFile();
                BookingCancelRequest req = new BookingCancelRequest(bookingID, refund, penalty, booking.getCreatedBy(),reason);
                reqs.add(req);
                BookingCancelRequestRepository.saveToFile(reqs);
                break;
            }
        }

    }

    // Hàm tính phạt
    public static double cancellationPenaltyRate(long daysToStart) {
        if (daysToStart >= 30) return 0.0;
        if (daysToStart >= 20) return 0.10;
        if (daysToStart >= 7) return 0.30;
        if (daysToStart >= 3) return 0.50;
        return 1.0;
    }
    public List<Booking> getBookings() {
        return this.bookings;
    }
    
    // Nếu chưa có, thêm hàm này để load lại từ file (nếu cần thiết)
    public void refreshData() {
        this.bookings = BookingRepository.loadBookingsFromFile();
    }
}
