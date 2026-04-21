package com.bintang.service;

import com.bintang.entity.Attendance;
import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Phrase;
import com.lowagie.text.Element;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AttendanceReportService {

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public byte[] generateExcel(List<Attendance> attendanceList) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Attendance Report");

            // Header Style
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.BLUE_GREY.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            org.apache.poi.ss.usermodel.Font font = workbook.createFont();
            font.setColor(IndexedColors.WHITE.getIndex());
            font.setBold(true);
            headerStyle.setFont(font);

            // Create Header
            Row headerRow = sheet.createRow(0);
            String[] columns = {"Tanggal", "NIK", "Nama", "Job", "In", "Out", "Status", "Detail"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // Fill Data
            int rowIdx = 1;
            for (Attendance att : attendanceList) {
                Row row = sheet.createRow(rowIdx++);
                String dateStr = att.getCheckInTime() != null ? att.getCheckInTime().format(dateFormatter) : "-";
                String timeInStr = att.getCheckInTime() != null ? att.getCheckInTime().format(timeFormatter) : "-";
                
                row.createCell(0).setCellValue(dateStr);
                row.createCell(1).setCellValue(att.getEmployee() != null ? att.getEmployee().getNik() : "-");
                row.createCell(2).setCellValue(att.getEmployee() != null ? att.getEmployee().getFirstName() + " " + att.getEmployee().getLastName() : "-");
                row.createCell(3).setCellValue(att.getEmployee() != null ? att.getEmployee().getJobId() : "-");
                row.createCell(4).setCellValue(timeInStr);
                row.createCell(5).setCellValue(att.getCheckOutTime() != null ? att.getCheckOutTime().format(timeFormatter) : "-");
                row.createCell(6).setCellValue(att.getStatus());
                row.createCell(7).setCellValue(att.getCheckInDetail() != null ? att.getCheckInDetail() : "");
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        }
    }

    public byte[] generatePdf(List<Attendance> attendanceList) throws IOException {
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);

        document.open();
        
        // Title
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLACK);
        Paragraph title = new Paragraph("Laporan Kehadiran Karyawan", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Table
        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100f);
        table.setWidths(new float[]{2.5f, 3f, 4f, 3f, 2f, 2f, 4f, 5f});

        // Table Header
        Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
        String[] headers = {"Tanggal", "NIK", "Nama", "Job", "In", "Out", "Status", "Detail"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headFont));
            cell.setBackgroundColor(new Color(44, 62, 80));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            table.addCell(cell);
        }

        // Table Data
        Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 9);
        for (Attendance att : attendanceList) {
            String dateStr = att.getCheckInTime() != null ? att.getCheckInTime().format(dateFormatter) : "-";
            String timeInStr = att.getCheckInTime() != null ? att.getCheckInTime().format(timeFormatter) : "-";
            String timeOutStr = att.getCheckOutTime() != null ? att.getCheckOutTime().format(timeFormatter) : "-";

            table.addCell(new PdfPCell(new Phrase(dateStr, dataFont)));
            table.addCell(new PdfPCell(new Phrase(att.getEmployee() != null ? att.getEmployee().getNik() : "-", dataFont)));
            table.addCell(new PdfPCell(new Phrase(att.getEmployee() != null ? att.getEmployee().getFirstName() + " " + att.getEmployee().getLastName() : "-", dataFont)));
            table.addCell(new PdfPCell(new Phrase(att.getEmployee() != null ? att.getEmployee().getJobId() : "-", dataFont)));
            table.addCell(new PdfPCell(new Phrase(timeInStr, dataFont)));
            table.addCell(new PdfPCell(new Phrase(timeOutStr, dataFont)));
            table.addCell(new PdfPCell(new Phrase(att.getStatus() != null ? att.getStatus() : "-", dataFont)));
            table.addCell(new PdfPCell(new Phrase(att.getCheckInDetail() != null ? att.getCheckInDetail() : "", dataFont)));
        }

        document.add(table);
        document.close();
        return out.toByteArray();
    }
}
