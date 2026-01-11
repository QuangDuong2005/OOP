package model;

import java.time.LocalDate;

public class Report {
    public static int numberReports = 0;

    public String reportId;
    public String type; // REVENUE
    public LocalDate createdAt;
    public String createdByStaffId;

    public LocalDate fromDate;
    public LocalDate toDate;

    public int totalInvoices;
    public double totalRevenue;
    public int totalBookings;

    public String exportedFile; // path

    public Report() {}

    public static Report createRevenueReport(LocalDate from, LocalDate to, String byStaffId) {
        Report r = new Report();
        r.reportId = "RP" + String.format("%03d", ++numberReports);
        r.type = "REVENUE";
        r.createdAt = LocalDate.now();
        r.createdByStaffId = byStaffId;
        r.fromDate = from;
        r.toDate = to;
        r.exportedFile = null;
        return r;
    }
}
