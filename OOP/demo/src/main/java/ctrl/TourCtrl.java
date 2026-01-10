package ctrl;

import java.time.LocalDate;
import java.util.List;

import expection.InvalidDateException;
import model.Tour;
import repository.TourRepository;
public class TourCtrl {
    // Tạo tour mới
    public static void createTour(String description, String departurePoint, double pricePerPerson, int maxPeople, String startDate, String endDate, String tourName) throws InvalidDateException {
        Tour newTour = new Tour(description, departurePoint, pricePerPerson, maxPeople, startDate, endDate, 0);
        if  (newTour.startDate.isBefore(LocalDate.now())) { // Ngoại lệ nếu ngày khởi hành là ngày đã qua
            throw new InvalidDateException("Ngày khởi hành không được là ngày đã qua.");
        }else if (LocalDate.parse(endDate).isBefore(LocalDate.parse(startDate))) { // Ngoại lệ nếu ngày kết thúc trước ngày khởi hành
            throw new InvalidDateException("Ngày kết thúc phải sau ngày khởi hành.");
        }
        else{
            TourRepository.appendTourToFile(newTour);
        }
    }
    // Cập nhật thông tin tour khi đã biết tour đó
    public static void updateTourInfo(Tour tour, String description, String departurePoint, double pricePerPerson, int maxPeople, String startDate, String endDate, int bookedPeople, List<Tour> tours) throws InvalidDateException {
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
