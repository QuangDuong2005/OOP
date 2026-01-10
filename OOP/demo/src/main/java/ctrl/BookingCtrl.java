package ctrl;
import java.util.List;

import model.Booking;
import model.Customer;
import model.Staff;
import model.Tour;
import repository.BookingRepository;
import repository.TourRepository;
public class BookingCtrl {
    // Tạo booking mới khi biết khách hàng, tour và người tạo tour
    public static void createBooking(Customer customer, Tour tour, int numberOfPeople, Staff staff, List <Tour> tours) {
        Booking newBooking = new Booking(customer.getCustomerID(), tour.getTourID(), numberOfPeople, staff.getStaffID(), tour.getPricePerPerson() * numberOfPeople, tour.getPricePerPerson() * numberOfPeople);
        // Tính tổng số tiền và dư nợ khi tạo booking
        newBooking.totalAmount = tour.getPricePerPerson() * numberOfPeople;
        newBooking.debtAmount = newBooking.totalAmount; // Mặc định lúc tạo booking là chưa thanh toán gì
        // Cập nhật lại file booking và tour
        BookingRepository.appendBookingToFile(newBooking);
        // tìm lại tour vì tour nạp lại từ file ở ô nhớ mới
        for (Tour t : tours) {
            if (t.getTourID().equals(tour.getTourID())) {
                tour = t;
                break;
            }
        }
        tour.updateBookedPeople(numberOfPeople);
        TourRepository.saveToursToFile(tours);
    }
    // cập nhật thông tin booking khi đã biết booking đó
    public static void updateBookingInfo(Booking booking, double totalAmount, double debtAmount) {
        booking.totalAmount = totalAmount;
        booking.debtAmount = debtAmount;
        booking.calculateStatus();
        BookingRepository.saveBookingsToFile(BookingRepository.loadBookingsFromFile());
    }
}
