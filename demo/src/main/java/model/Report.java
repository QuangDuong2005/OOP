package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Report {

    // Định dạng: yyyy-MM-dd (Ví dụ: 2026-01-12)
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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

    public static Report createRevenueReport(String ID, LocalDate from, LocalDate to, String byStaffId) {
        Report r = new Report();
        r.reportId = ID;
        r.type = "REVENUE";
        r.createdAt = LocalDate.now();
        r.createdByStaffId = byStaffId;
        r.fromDate = from;
        r.toDate = to;
        r.exportedFile = null;
        return r;
    }

    // ================== GETTERS & SETTERS (STRING yyyy-MM-dd) ==================

    // --- CreatedAt ---
    public String getCreatedAt() {
        // Chuyển LocalDate -> String
        return (this.createdAt != null) ? this.createdAt.format(FORMATTER) : null;
    }

    public void setCreatedAt(String dateStr) {
        // Chuyển String -> LocalDate (Lỗi nếu sai định dạng)
        this.createdAt = (dateStr != null) ? LocalDate.parse(dateStr, FORMATTER) : null;
    }

    // --- FromDate ---
    public String getFromDate() {
        return (this.fromDate != null) ? this.fromDate.format(FORMATTER) : null;
    }

    public void setFromDate(String dateStr) {
        this.fromDate = (dateStr != null) ? LocalDate.parse(dateStr, FORMATTER) : null;
    }

    // --- ToDate ---
    public String getToDate() {
        return (this.toDate != null) ? this.toDate.format(FORMATTER) : null;
    }

    public void setToDate(String dateStr) {
        this.toDate = (dateStr != null) ? LocalDate.parse(dateStr, FORMATTER) : null;
    }

    // ================== CÁC GETTER/SETTER KHÁC ==================

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreatedByStaffId() {
        return createdByStaffId;
    }

    public void setCreatedByStaffId(String createdByStaffId) {
        this.createdByStaffId = createdByStaffId;
    }

    public int getTotalInvoices() {
        return totalInvoices;
    }

    public void setTotalInvoices(int totalInvoices) {
        this.totalInvoices = totalInvoices;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public int getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(int totalBookings) {
        this.totalBookings = totalBookings;
    }

    public String getExportedFile() {
        return exportedFile;
    }

    public void setExportedFile(String exportedFile) {
        this.exportedFile = exportedFile;
    }
}