package ctrl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import model.Booking;
import model.Customer;
import model.Staff;
import model.Tour;
import repository.BookingRepository;
import repository.TourRepository;

public class BookingCtrl {
    public static final int UPDATE_PEOPLE_CUTOFF_DAYS = 7; // <=7 days to start => lock

    // Tạo booking mới khi biết khách hàng, tour và người tạo booking
    public static void createBooking(Customer customer, Tour tour, int numberOfPeople, Staff staff, List<Tour> tours) {
        Booking newBooking = new Booking(
                customer.getCustomerID(),
                tour.getTourID(),
                numberOfPeople,
                staff.getStaffID(),
                tour.getPricePerPerson() * numberOfPeople,
                tour.getPricePerPerson() * numberOfPeople);

        BookingRepository.appendBookingToFile(newBooking);

        // cập nhật số chỗ của tour
        Tour targetTour = findTourInList(tour.getTourID(), tours);
        if (targetTour != null) {
            targetTour.updateBookedPeople(numberOfPeople);
            TourRepository.saveToursToFile(tours);
        }
    }

    // Cập nhật thông tin thanh toán (tổng tiền / dư nợ)
    public static void updateBookingPayment(Booking booking, double totalAmount, double debtAmount, List<Booking> bookings) {
        booking.totalAmount = totalAmount;
        booking.debtAmount = Math.max(0, debtAmount);
        booking.calculateStatus();
        BookingRepository.saveBookingsToFile(bookings);
    }

    /**
     * Update number of people in booking.
     * - Only allowed when days to tour start > UPDATE_PEOPLE_CUTOFF_DAYS
     * - Re-check tour capacity
     */
    public static void updateBookingPeople(Booking booking, Tour tour, int newPeople, List<Booking> bookings, List<Tour> tours) {
        if (booking.isCancelled) {
            throw new IllegalStateException("Booking đã bị hủy");
        }
        LocalDate start = tour.startDate;
        long daysToStart = ChronoUnit.DAYS.between(LocalDate.now(), start);
        if (daysToStart <= UPDATE_PEOPLE_CUTOFF_DAYS) {
            throw new IllegalStateException("Không thể sửa số người khi còn " + daysToStart + " ngày là khởi hành (<= " + UPDATE_PEOPLE_CUTOFF_DAYS + ")");
        }
        if (newPeople <= 0) {
            throw new IllegalArgumentException("Số người phải > 0");
        }

        int diff = newPeople - booking.numberOfPeople;
        if (diff == 0) return;

        Tour targetTour = findTourInList(tour.getTourID(), tours);
        if (targetTour == null) {
            throw new IllegalStateException("Không tìm thấy tour trong hệ thống");
        }
        int available = targetTour.maxPeople - targetTour.bookedPeople;
        if (diff > 0 && available < diff) {
            throw new IllegalStateException("Không đủ chỗ trống. Còn trống: " + available);
        }

        // update tour booked people
        targetTour.bookedPeople += diff;
        TourRepository.saveToursToFile(tours);

        // update booking amounts
        double paid = booking.getPaidAmount();
        booking.numberOfPeople = newPeople;
        booking.totalAmount = tour.pricePerPerson * newPeople;
        booking.debtAmount = Math.max(0, booking.totalAmount - paid);
        booking.calculateStatus();

        BookingRepository.saveBookingsToFile(bookings);
    }

    /**
     * Cancel booking and free tour seats. Penalty is computed based on days to start.
     * Refund amount is stored in booking (money handling is done via Manager approval).
     */
    public static CancelResult cancelBooking(Booking booking, Tour tour, String reason, List<Booking> bookings, List<Tour> tours) {
        if (booking.isCancelled) {
            throw new IllegalStateException("Booking đã bị hủy trước đó");
        }
        LocalDate today = LocalDate.now();
        if (!today.isBefore(tour.startDate)) {
            throw new IllegalStateException("Không thể hủy khi tour đã/đang khởi hành");
        }
        long daysToStart = ChronoUnit.DAYS.between(today, tour.startDate);
        double paid = booking.getPaidAmount();

        double rate = cancellationPenaltyRate(daysToStart);
        double penalty = Math.max(0, paid * rate);
        double refund = Math.max(0, paid - penalty);

        booking.isCancelled = true;
        booking.cancelledAt = today;
        booking.cancellationReason = reason;
        booking.cancellationPenalty = penalty;
        booking.refundAmount = refund;
        booking.calculateStatus();

        // free seats
        Tour targetTour = findTourInList(tour.tourID, tours);
        if (targetTour != null) {
            targetTour.bookedPeople = Math.max(0, targetTour.bookedPeople - booking.numberOfPeople);
            TourRepository.saveToursToFile(tours);
        }

        BookingRepository.saveBookingsToFile(bookings);

        return new CancelResult(daysToStart, rate, penalty, refund);
    }

    public static class CancelResult {
        public final long daysToStart;
        public final double rate;
        public final double penaltyAmount;
        public final double refundAmount;

        public CancelResult(long daysToStart, double rate, double penaltyAmount, double refundAmount) {
            this.daysToStart = daysToStart;
            this.rate = rate;
            this.penaltyAmount = penaltyAmount;
            this.refundAmount = refundAmount;
        }
    }

    // =====================
    // helpers
    // =====================
    private static Tour findTourInList(String tourId, List<Tour> tours) {
        if (tours == null) return null;
        for (Tour t : tours) {
            if (t != null && t.tourID != null && t.tourID.equals(tourId)) return t;
        }
        return null;
    }

    /**
     * A simple penalty policy (you can adjust later):
     * - >=30 days: 0%
     * - 15..29 days: 10%
     * - 7..14 days: 30%
     * - 1..6 days: 50%
     * - 0 day (same-day): 100%
     */
    public static double cancellationPenaltyRate(long daysToStart) {
        if (daysToStart >= 30) return 0.0;
        if (daysToStart >= 15) return 0.10;
        if (daysToStart >= 7) return 0.30;
        if (daysToStart >= 1) return 0.50;
        return 1.0;
    }
}
