package com.AM.demo.Controller;

import com.lowagie.text.Document;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.awt.Color;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/export")
public class ExportController {

    /* ══════════════════════════════════════════
       POST /export/excel
    ══════════════════════════════════════════ */
    @PostMapping("/excel")
    public void exportExcel(
            @RequestBody Map<String, Object> request,
            HttpServletResponse response) throws IOException {

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> data =
                (List<Map<String, Object>>) request.get("data");

        String subject     = nullable((String) request.get("subject"));
        String date        = nullable((String) request.get("date"));
        String time        = nullable((String) request.get("time"));
        String teacherName = nullable((String) request.get("teacherName")); // ← NEW

        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Attendance");

        // ── styles ──────────────────────────────
        CellStyle titleStyle = workbook.createCellStyle();
        org.apache.poi.ss.usermodel.Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 14);
        titleStyle.setFont(titleFont);

        CellStyle headerStyle = workbook.createCellStyle();
        org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.DARK_GREEN.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);

        CellStyle metaStyle = workbook.createCellStyle();
        org.apache.poi.ss.usermodel.Font metaFont = workbook.createFont();
        metaFont.setItalic(true);
        metaStyle.setFont(metaFont);

        int rowNum = 0;

        // ── title row ───────────────────────────
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Attendance Report");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 6)); // extended to col 6

        // ── meta rows ───────────────────────────
        rowNum = addMetaRow(sheet, rowNum, "Subject",      subject,     metaStyle);
        rowNum = addMetaRow(sheet, rowNum, "Teacher Name", teacherName, metaStyle); // ← NEW
        rowNum = addMetaRow(sheet, rowNum, "Date",         date,        metaStyle);
        rowNum = addMetaRow(sheet, rowNum, "Time",         time,        metaStyle);
        rowNum++; // blank row

        // ── column headers ──────────────────────
        String[] headers = {"Roll No", "Name", "Contact", "Subject", "Teacher Name", "Date", "Time", "Status"}; // ← NEW col
        Row headerRow = sheet.createRow(rowNum++);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // ── data rows ───────────────────────────
        CellStyle presentStyle = workbook.createCellStyle();
        org.apache.poi.ss.usermodel.Font presentFont = workbook.createFont();
        presentFont.setColor(IndexedColors.DARK_GREEN.getIndex());
        presentStyle.setFont(presentFont);

        CellStyle absentStyle = workbook.createCellStyle();
        org.apache.poi.ss.usermodel.Font absentFont = workbook.createFont();
        absentFont.setColor(IndexedColors.RED.getIndex());
        absentStyle.setFont(absentFont);

        if (data != null) {
            for (Map<String, Object> record : data) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(nullable(String.valueOf(record.get("rollno"))));
                row.createCell(1).setCellValue(nullable(String.valueOf(record.get("name"))));
                row.createCell(2).setCellValue(nullable(String.valueOf(record.get("contact"))));
                row.createCell(3).setCellValue(nullable(String.valueOf(record.get("subject"))));
                row.createCell(4).setCellValue(nullable(String.valueOf(record.get("teacherName")))); // ← NEW
                row.createCell(5).setCellValue(nullable(String.valueOf(record.get("date"))));
                row.createCell(6).setCellValue(nullable(String.valueOf(record.get("time"))));

                Cell statusCell = row.createCell(7); // ← shifted from 6 to 7
                String status = nullable(String.valueOf(record.get("status")));
                statusCell.setCellValue(status);
                if ("Present".equalsIgnoreCase(status)) statusCell.setCellStyle(presentStyle);
                else if ("Absent".equalsIgnoreCase(status)) statusCell.setCellStyle(absentStyle);
            }
        }

        // ── auto-size columns ───────────────────
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // ── write response ──────────────────────
        response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader(
                "Content-Disposition", "attachment; filename=Attendance_Report.xlsx");

        workbook.write(response.getOutputStream());
        workbook.close();
    }


    /* ══════════════════════════════════════════
       POST /export/pdf
    ══════════════════════════════════════════ */
    @PostMapping("/pdf")
    public void exportPdf(
            @RequestBody Map<String, Object> request,
            HttpServletResponse response) throws IOException {

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> data =
                (List<Map<String, Object>>) request.get("data");

        String subject     = nullable((String) request.get("subject"));
        String date        = nullable((String) request.get("date"));
        String time        = nullable((String) request.get("time"));
        String teacherName = nullable((String) request.get("teacherName")); // ← NEW

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "attachment; filename=Attendance_Report.pdf");

        Document document = new Document();

        try {
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            // ── title ────────────────────────────
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16,
                    new Color(7, 74, 48));
            document.add(new Paragraph("Attendance Report", titleFont));
            document.add(new Paragraph(" "));

            // ── meta info ────────────────────────
            Font metaFont = FontFactory.getFont(FontFactory.HELVETICA, 10,
                    new Color(90, 120, 100));
            document.add(new Paragraph("Subject      : " + subject,     metaFont));
            document.add(new Paragraph("Teacher Name : " + teacherName, metaFont)); // ← NEW
            document.add(new Paragraph("Date         : " + date,        metaFont));
            document.add(new Paragraph("Time         : " + time,        metaFont));
            document.add(new Paragraph(" "));

            // ── table (8 columns now) ─────────────
            PdfPTable table = new PdfPTable(8); // ← was 7, now 8
            table.setWidthPercentage(100);
            table.setSpacingBefore(6f);

            // column widths
            table.setWidths(new float[]{1.2f, 2.2f, 1.8f, 2f, 2f, 1.6f, 1.8f, 1.4f}); // ← added Teacher col width

            // header cells
            String[] headers = {"Roll No", "Name", "Contact", "Subject", "Teacher Name", "Date", "Time", "Status"}; // ← NEW
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9,
                    new Color(232, 250, 244));

            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Paragraph(h, headerFont));
                cell.setBackgroundColor(new Color(7, 74, 48));
                cell.setPadding(6f);
                cell.setBorderColor(new Color(15, 110, 86));
                table.addCell(cell);
            }

            // data cells
            Font dataFont    = FontFactory.getFont(FontFactory.HELVETICA, 9,
                    new Color(58, 106, 80));
            Font presentFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9,
                    new Color(15, 110, 86));
            Font absentFont  = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9,
                    new Color(163, 45, 45));

            Color rowEven = new Color(244, 253, 248);
            Color rowOdd  = new Color(255, 255, 255);

            if (data != null) {
                int rowIdx = 0;
                for (Map<String, Object> record : data) {
                    Color bg = (rowIdx % 2 == 0) ? rowEven : rowOdd;
                    rowIdx++;

                    String status = nullable(String.valueOf(record.get("status")));

                    String[] values = {
                            nullable(String.valueOf(record.get("rollno"))),
                            nullable(String.valueOf(record.get("name"))),
                            nullable(String.valueOf(record.get("contact"))),
                            nullable(String.valueOf(record.get("subject"))),
                            nullable(String.valueOf(record.get("teacherName"))), // ← NEW
                            nullable(String.valueOf(record.get("date"))),
                            nullable(String.valueOf(record.get("time"))),
                            status
                    };

                    for (int i = 0; i < values.length; i++) {
                        Font cellFont = dataFont;
                        if (i == 7) { // ← status is now index 7 (was 6)
                            if ("Present".equalsIgnoreCase(status))     cellFont = presentFont;
                            else if ("Absent".equalsIgnoreCase(status)) cellFont = absentFont;
                        }
                        PdfPCell cell = new PdfPCell(new Paragraph(values[i], cellFont));
                        cell.setBackgroundColor(bg);
                        cell.setPadding(5f);
                        cell.setBorderColor(new Color(200, 234, 216));
                        cell.setBorderWidth(0.5f);
                        table.addCell(cell);
                    }
                }
            }

            document.add(table);
            document.close();
            response.getOutputStream().flush();

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }


    /* ── helpers ─────────────────────────────── */

    private int addMetaRow(Sheet sheet, int rowNum, String label,
                           String value, CellStyle style) {
        Row row = sheet.createRow(rowNum);
        Cell labelCell = row.createCell(0);
        labelCell.setCellValue(label + " : " + value);
        labelCell.setCellStyle(style);
        return rowNum + 1;
    }

    private String nullable(String s) {
        return (s == null || s.equals("null")) ? "" : s;
    }
}