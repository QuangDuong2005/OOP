package model;

import java.time.LocalDate;
public class Tour {
    public static int numberTours = 0;
    public String tourID;
    public String description;
    public String departurePoint;
    public double pricePerPerson;
    public int maxPeople;
    public int bookedPeople;
    public LocalDate startDate;
    public LocalDate endDate; 
    public String status; // Chưa khởi hành, Đang diễn ra, Đã kết thúc
    // constructor rỗng
    public Tour(){};
    // Constructor khi tạo tour mới
    public Tour(String description, String departurePoint, double pricePerPerson, int maxPeople, String startDateString, String endDateString, int bookedPeople) {
        this.tourID = "T" + String.format("%03d", ++numberTours);
        this.description = description;
        this.departurePoint = departurePoint;
        this.pricePerPerson = pricePerPerson;
        this.maxPeople = maxPeople;
        this.bookedPeople = bookedPeople;
        this.startDate = LocalDate.parse(startDateString);
        this.endDate = LocalDate.parse(endDateString);
        this.status = determineStatus();
    }
    // Phương thức xác định trạng thái tour dựa trên ngày hiện tại
    private String determineStatus() {
        LocalDate today = LocalDate.now();
        if (today.isBefore(startDate)) {
            return "Chưa khởi hành";
        } else if (today.isAfter(endDate)) {
            return "Đã kết thúc";
        } else {
            return "Đang diễn ra";
        }
    }

    // Getters và Setters
    public String getTourID() {
        return tourID;
    }

    public String getDescription() {
        return description;
    }

    public String getDeparturePoint() {
        return departurePoint;
    }

    public double getPricePerPerson() {
        return pricePerPerson;
    }

    public int getMaxPeople() {
        return maxPeople;
    }

    public String getStartDate() {
        return startDate.toString();
    }

    public String getEndDate() {
        return endDate.toString();
    }

    public String getStatus() {
        return status;
    }
    public int getBookedPeople() {
        return bookedPeople;
    }
    // setters
    public void setTourID(String tourID) {
        this.tourID = tourID;
    }
    public void setPricePerPerson(double pricePerPerson) {
        this.pricePerPerson = pricePerPerson;
    }
    public void setMaxPeople(int maxPeople) {
        this.maxPeople = maxPeople;
    }
    public void setStartDate(String startDateString) {
        this.startDate = LocalDate.parse(startDateString);
    }
    public void setEndDate(String endDateString) {
        this.endDate = LocalDate.parse(endDateString);
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setDeparturePoint(String departurePoint) {
        this.departurePoint = departurePoint;
    }
    public void setBookedPeople(int bookedPeople) {
        this.bookedPeople = bookedPeople;
    }
    // Cập nhật số người đã đặt
    public void updateBookedPeople(int newBookedPeople) {
        this.bookedPeople += newBookedPeople;
    }
}