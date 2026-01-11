package ctrl;

import java.time.LocalDate;
import java.util.List;

import expection.DuplicateException;
import expection.InvalidDateException;
import expection.TourAlreadyBookedException;
import model.Booking;
import model.Tour;
import repository.BookingRepository;
import repository.TourRepository;
public class TourCtrl {
    List<Tour> tours = TourRepository.loadToursFromFile();
    // Tạo tour mới
    public void createTour(String ID,String description, String departurePoint, double pricePerPerson, int maxPeople, String startDate, String endDate, String tourName) throws InvalidDateException, DuplicateException {
        for (Tour tour: tours){
            if ((tour.getTourID()).equals(ID)){
                throw new DuplicateException("Đã có ID tour này rồi");
            }
        }
        Tour newTour = new Tour(ID, description, departurePoint, pricePerPerson, maxPeople, startDate, endDate, 0);
        if  (newTour.startDate.isBefore(LocalDate.now())) { // Ngoại lệ nếu ngày khởi hành là ngày đã qua
            throw new InvalidDateException("Ngày khởi hành không được là ngày đã qua.");
        }else if (LocalDate.parse(endDate).isBefore(LocalDate.parse(startDate))) { // Ngoại lệ nếu ngày kết thúc trước ngày khởi hành
            throw new InvalidDateException("Ngày kết thúc phải sau ngày khởi hành.");
        }
        else{
            tours.add(newTour);
            TourRepository.saveToursToFile(tours);
        }
    }
    // Cập nhật thông tin tour khi đã biết tour đó ko cần throw vì GUI sẽ chọn
    public void updateTourInfo(String ID, String description, String departurePoint, double pricePerPerson, int maxPeople, String startDate, String endDate, int bookedPeople) throws InvalidDateException, TourAlreadyBookedException {
        // Tìm Tour theo ID 
        for (Tour tour: tours){
            if ((tour.getTourID()).equals(ID)){
                List<Booking> bookings = BookingRepository.loadBookingsFromFile();
                // Duyệt xem có ai booking tour này chưa nếu ko thì báo lỗi
                for (Booking booking: bookings){
                    if ((booking.getBookingID()).equals(ID)){
                        throw new TourAlreadyBookedException("Đã có người booking Tour này");
                    }
                }
                if  (LocalDate.parse(startDate).isBefore(LocalDate.now())) {    
                    throw new InvalidDateException("Ngày khởi hành không được là ngày đã qua.");
                }else if (LocalDate.parse(endDate).isBefore(LocalDate.parse(startDate))) { // Ngoại lệ nếu ngày kết thúc trước ngày khởi hành
                    throw new InvalidDateException("Ngày kết thúc phải sau ngày khởi hành.");
                }else{
                    tour.setDescription(description);
                    tour.setDeparturePoint(departurePoint);
                    tour.setPricePerPerson(pricePerPerson);
                    tour.setMaxPeople(maxPeople);
                    tour.setStartDate(startDate);
                    tour.setEndDate(endDate);
                    tour.bookedPeople = bookedPeople;       
                    TourRepository.saveToursToFile(tours);
                }
            }
        }
    }
}
