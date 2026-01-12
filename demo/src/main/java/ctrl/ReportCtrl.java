package ctrl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import model.Booking;
import model.Invoice;
import model.Report;
import repository.ReportRepository;

public class ReportCtrl {

    public static Report createRevenueReport(LocalDate from, LocalDate to, String byStaffId,
                                            List<Invoice> invoices, List<Booking> bookings, List<Report> reportCache) {
        // 1. Kiểm tra ngày hợp lệ
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("Ngày bắt đầu phải trước hoặc bằng ngày kết thúc (fromDate > toDate)");
        }

        // 2. Tạo ID tự động (Ví dụ: RP + Thời gian hiện tại)
        String reportId = "RP" + System.currentTimeMillis();

        // 3. Gọi hàm tạo Report (Thêm reportId vào tham số đầu tiên)
        Report r = Report.createRevenueReport(reportId, from, to, byStaffId);

        // 4. Tính toán doanh thu và số hóa đơn trong khoảng thời gian
        int invCount = 0;
        double revenue = 0;
        if (invoices != null) {
            for (Invoice i : invoices) {
                // Kiểm tra null và ngày tạo hóa đơn
                if (i != null && i.getCreatedAt() != null) {
                    // Chuyển String ngày về LocalDate nếu Invoice lưu String, hoặc dùng trực tiếp nếu là LocalDate
                    // Giả sử i.createdAt là LocalDate. Nếu là String thì cần parse.
                    LocalDate iDate = LocalDate.parse(i.getCreatedAt()); 
                    
                    if (!iDate.isBefore(from) && !iDate.isAfter(to)) { // Logic: from <= date <= to
                        invCount++;
                        revenue += i.getTotalAmount();
                    }
                }
            }
        }

        // 5. Tính toán số Booking trong khoảng thời gian (Logic cũ của bạn là đếm tất cả, mình sửa lại cho đúng báo cáo)
        int bookingCount = 0;
        if (bookings != null) {
            for (Booking b : bookings) {
                if (b != null && b.getBookingDate() != null) {
                    LocalDate bDate = LocalDate.parse(b.getBookingDate());
                    if (!bDate.isBefore(from) && !bDate.isAfter(to)) {
                        bookingCount++;
                    }
                }
            }
        }

        r.setTotalInvoices(invCount);
        r.setTotalRevenue(revenue);
        r.setTotalBookings(bookingCount);

        // 6. Lưu vào Cache và File
        reportCache.add(r);
        ReportRepository.saveToFile(reportCache);

        return r;
    }

    public static File exportReportToCsv(Report report, List<Invoice> invoices) {
        try {
            // Tạo thư mục exports nếu chưa có
            File dir = new File(System.getProperty("user.dir"), "exports");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // Tên file: RP123456_REVENUE.csv
            String fileName = report.getReportId() + "_" + report.getType() + ".csv";
            File f = new File(dir, fileName);

            try (FileWriter w = new FileWriter(f)) {
                // Header báo cáo chung
                w.write("Report ID,Type,From Date,To Date,Total Invoices,Total Revenue,Total Bookings\n");
                w.write(report.getReportId() + "," + 
                        report.getType() + "," + 
                        report.getFromDate() + "," + 
                        report.getToDate() + "," + 
                        report.getTotalInvoices() + "," + 
                        report.getTotalRevenue() + "," +
                        report.getTotalBookings() + "\n");
                
                w.write("\n"); // Dòng trống ngăn cách

                // Danh sách chi tiết hóa đơn
                w.write("Invoice ID,Booking ID,Created At,Amount,Staff ID\n");
                
                if (invoices != null) {
                    // Cần parse ngày từ String của Report để so sánh lại (vì Report lưu String yyyy-MM-dd)
                    LocalDate from = LocalDate.parse(report.getFromDate());
                    LocalDate to = LocalDate.parse(report.getToDate());

                    for (Invoice i : invoices) {
                        if (i != null && i.getCreatedAt() != null) {
                            LocalDate iDate = LocalDate.parse(i.getCreatedAt());
                            if (!iDate.isBefore(from) && !iDate.isAfter(to)) {
                                w.write(i.getInvoiceID() + "," + 
                                        i.getBookingID() + "," + 
                                        i.getCreatedAt() + "," + 
                                        i.getTotalAmount() + "," +
                                        (i.getCreatedByStaffId() != null ? i.getCreatedByStaffId() : "N/A") + "\n");
                            }
                        }
                    }
                }
            }

            // Cập nhật đường dẫn file vào object Report
            report.setExportedFile(f.getAbsolutePath());
            return f;

        } catch (IOException e) {
            throw new RuntimeException("Lỗi xuất file báo cáo: " + e.getMessage(), e);
        }
    }
}