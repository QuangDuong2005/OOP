package ctrl;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.List;

import model.Booking;
import model.Invoice;
import model.Report;
import repository.ReportRepository;

public class ReportCtrl {
    public static Report createRevenueReport(LocalDate from, LocalDate to, String byStaffId,
                                            List<Invoice> invoices, List<Booking> bookings, List<Report> reportCache) {
        if (from.isAfter(to)) throw new IllegalArgumentException("fromDate > toDate");

        Report r = Report.createRevenueReport(from, to, byStaffId);

        int invCount = 0;
        double revenue = 0;
        if (invoices != null) {
            for (Invoice i : invoices) {
                if (i == null || i.createdAt == null) continue;
                if ((i.createdAt.isEqual(from) || i.createdAt.isAfter(from)) && (i.createdAt.isEqual(to) || i.createdAt.isBefore(to))) {
                    invCount++;
                    revenue += i.totalAmount;
                }
            }
        }
        r.totalInvoices = invCount;
        r.totalRevenue = revenue;
        r.totalBookings = bookings == null ? 0 : bookings.size();

        reportCache.add(r);
        ReportRepository.saveToFile(reportCache);
        return r;
    }

    public static File exportReportToCsv(Report report, List<Invoice> invoices) {
        try {
            File dir = new File(System.getProperty("user.dir"), "exports");
            dir.mkdirs();

            String fileName = report.reportId + "_" + report.type + ".csv";
            File f = new File(dir, fileName);

            try (FileWriter w = new FileWriter(f)) {
                w.write("reportId,type,from,to,totalInvoices,totalRevenue\n");
                w.write(report.reportId + "," + report.type + "," + report.fromDate + "," + report.toDate + "," + report.totalInvoices + "," + report.totalRevenue + "\n");
                w.write("\n");
                w.write("invoiceId,bookingId,createdAt,amount\n");
                if (invoices != null) {
                    for (Invoice i : invoices) {
                        if (i == null || i.createdAt == null) continue;
                        if ((i.createdAt.isEqual(report.fromDate) || i.createdAt.isAfter(report.fromDate)) && (i.createdAt.isEqual(report.toDate) || i.createdAt.isBefore(report.toDate))) {
                            w.write(i.invoiceID + "," + i.bookingID + "," + i.createdAt + "," + i.totalAmount + "\n");
                        }
                    }
                }
            }

            report.exportedFile = f.getAbsolutePath();
            return f;
        } catch (Exception e) {
            throw new RuntimeException("Export failed: " + e.getMessage(), e);
        }
    }
}
