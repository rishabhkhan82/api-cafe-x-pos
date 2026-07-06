package com.cafex.pos.service;

import com.cafex.pos.dto.RestaurantReportResponse;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.GrayColor;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class RestaurantReportPdfService {

    private static final float PAGE_MARGIN = 36f;
    private static final float PAGE_WIDTH = PageSize.A4.getWidth();
    private static final float PAGE_HEIGHT = PageSize.A4.getHeight();
    private static final GrayColor COLOR_DARK_GRAY = new GrayColor(0.25f);
    private static final GrayColor COLOR_LIGHT_GRAY = new GrayColor(0.75f);
    private static final GrayColor COLOR_BG_ALT = new GrayColor(0.96f);
    private static final com.lowagie.text.pdf.RGBColor COLOR_WHITE = new com.lowagie.text.pdf.RGBColor(255, 255, 255);

    public ByteArrayResource generatePdf(RestaurantReportResponse report) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, PAGE_MARGIN, PAGE_MARGIN, PAGE_MARGIN, PAGE_MARGIN);
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        writer.setPageEvent(new EndOfPageEvent(report));
        document.open();

        addRestaurantHeader(document, report.getReportMeta());
        addReportMeta(document, report.getReportMeta());
        addStatistics(document, report.getStatistics());
        addDataTable(document, report.getData());

        document.close();
        return new ByteArrayResource(baos.toByteArray());
    }

    private void addRestaurantHeader(Document doc, RestaurantReportResponse.ReportMeta meta) throws DocumentException {
        Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD, COLOR_DARK_GRAY);
        Font bodyFont = new Font(Font.HELVETICA, 10, Font.NORMAL, new GrayColor(0.4f));

        Paragraph title = new Paragraph(meta.getRestaurantName(), titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(4f);
        doc.add(title);

        if (meta.getRestaurantAddress() != null && !meta.getRestaurantAddress().isEmpty()) {
            Paragraph address = new Paragraph(meta.getRestaurantAddress(), bodyFont);
            address.setAlignment(Element.ALIGN_CENTER);
            address.setSpacingAfter(2f);
            doc.add(address);
        }

        StringBuilder contact = new StringBuilder();
        if (meta.getRestaurantPhone() != null) contact.append("Phone: ").append(meta.getRestaurantPhone());
        if (meta.getRestaurantEmail() != null) {
            if (contact.length() > 0) contact.append("  |  ");
            contact.append("Email: ").append(meta.getRestaurantEmail());
        }
        if (contact.length() > 0) {
            Paragraph p = new Paragraph(contact.toString(), bodyFont);
            p.setAlignment(Element.ALIGN_CENTER);
            p.setSpacingAfter(2f);
            doc.add(p);
        }

        if (meta.getGstNumber() != null && !meta.getGstNumber().isEmpty()) {
            Paragraph gst = new Paragraph("GST Number: " + meta.getGstNumber(), bodyFont);
            gst.setAlignment(Element.ALIGN_CENTER);
            gst.setSpacingAfter(8f);
            doc.add(gst);
        }

        com.lowagie.text.pdf.draw.LineSeparator line = new com.lowagie.text.pdf.draw.LineSeparator();
        line.setLineColor(COLOR_LIGHT_GRAY);
        line.setLineWidth(1f);
        doc.add(line);
        doc.add(Chunk.NEWLINE);
    }

    private void addReportMeta(Document doc, RestaurantReportResponse.ReportMeta meta) throws DocumentException {
        Font headingFont = new Font(Font.HELVETICA, 14, Font.BOLD, COLOR_DARK_GRAY);
        Font bodyFont = new Font(Font.HELVETICA, 10, Font.NORMAL, new GrayColor(0.4f));

        String typeLabel = meta.getReportTypeLabel() != null ? meta.getReportTypeLabel() : meta.getReportType();
        Paragraph title = new Paragraph(typeLabel, headingFont);
        title.setSpacingAfter(4f);
        doc.add(title);

        String period = meta.getPeriod();
        if (period != null && !period.isEmpty()) {
            Paragraph p = new Paragraph("Period: " + period, bodyFont);
            p.setSpacingAfter(2f);
            doc.add(p);
        }

        Paragraph generated = new Paragraph("Generated: " + meta.getGeneratedAt(), bodyFont);
        generated.setSpacingAfter(8f);
        doc.add(generated);

        com.lowagie.text.pdf.draw.LineSeparator line = new com.lowagie.text.pdf.draw.LineSeparator();
        line.setLineColor(COLOR_LIGHT_GRAY);
        line.setLineWidth(1f);
        doc.add(line);
        doc.add(Chunk.NEWLINE);
    }

    private void addStatistics(Document doc, Map<String, Object> statistics) throws DocumentException {
        if (statistics == null || statistics.isEmpty()) return;

        Font labelFont = new Font(Font.HELVETICA, 8, Font.NORMAL, new GrayColor(0.4f));
        Font valueFont = new Font(Font.HELVETICA, 10, Font.BOLD, COLOR_DARK_GRAY);

        int cols = statistics.size();
        PdfPTable table = new PdfPTable(cols);
        table.setWidthPercentage(100);
        table.setSpacingBefore(4f);
        table.setSpacingAfter(12f);

        for (Map.Entry<String, Object> entry : statistics.entrySet()) {
            PdfPCell cell = new PdfPCell();
            cell.setBorder(Rectangle.BOX);
            cell.setBorderColor(COLOR_LIGHT_GRAY);
            cell.setBorderWidth(0.5f);
            cell.setPadding(6f);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);

            String label = formatLabel(entry.getKey());
            Paragraph labelP = new Paragraph(label, labelFont);
            labelP.setAlignment(Element.ALIGN_CENTER);
            labelP.setSpacingAfter(2f);
            cell.addElement(labelP);

            String value = formatValue(entry.getKey(), entry.getValue());
            Paragraph valueP = new Paragraph(value, valueFont);
            valueP.setAlignment(Element.ALIGN_CENTER);
            cell.addElement(valueP);

            table.addCell(cell);
        }

        doc.add(table);
    }

    private void addDataTable(Document doc, List<Map<String, Object>> data) throws DocumentException {
        if (data == null || data.isEmpty()) {
            Paragraph p = new Paragraph("No data available for the selected period",
                    new Font(Font.HELVETICA, 10, Font.ITALIC, new GrayColor(0.5f)));
            p.setAlignment(Element.ALIGN_CENTER);
            p.setSpacingBefore(12f);
            doc.add(p);
            return;
        }

        List<String> columns = new ArrayList<>(data.get(0).keySet());
        int colCount = columns.size();
        float availableWidth = doc.getPageSize().getWidth() - PAGE_MARGIN * 2;
        float colWidth = availableWidth / colCount;

        PdfPTable table = new PdfPTable(colCount);
        table.setWidthPercentage(100);
        table.setSpacingBefore(4f);
        table.setSpacingAfter(12f);
        float[] widths = new float[colCount];
        Arrays.fill(widths, colWidth);
        table.setWidths(widths);
        table.setHeaderRows(1);

        Font headerFont = new Font(Font.HELVETICA, 8, Font.BOLD, COLOR_WHITE);
        Font cellFont = new Font(Font.HELVETICA, 8, Font.NORMAL, COLOR_DARK_GRAY);
        Font altCellFont = new Font(Font.HELVETICA, 8, Font.NORMAL, COLOR_DARK_GRAY);

        for (String col : columns) {
            PdfPCell cell = new PdfPCell();
            cell.setBackgroundColor(new com.lowagie.text.pdf.RGBColor(60, 60, 60));
            cell.setBorderColor(COLOR_LIGHT_GRAY);
            cell.setBorderWidth(0.5f);
            cell.setPadding(5f);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            Paragraph p = new Paragraph(formatLabel(col), headerFont);
            p.setAlignment(Element.ALIGN_CENTER);
            cell.addElement(p);
            table.addCell(cell);
        }

        for (int i = 0; i < data.size(); i++) {
            Map<String, Object> row = data.get(i);
            PdfPCell cellTemplate = new PdfPCell();
            cellTemplate.setBorderColor(COLOR_LIGHT_GRAY);
            cellTemplate.setBorderWidth(0.5f);
            cellTemplate.setPadding(4f);
            if (i % 2 == 0) {
                cellTemplate.setBackgroundColor(COLOR_BG_ALT);
            }

            for (String col : columns) {
                PdfPCell cell = new PdfPCell(cellTemplate);
                String cellText = formatCellValue(col, row.get(col));
                Paragraph p = new Paragraph(cellText, i % 2 == 0 ? cellFont : altCellFont);
                p.setAlignment(Element.ALIGN_LEFT);
                cell.addElement(p);
                table.addCell(cell);
            }
        }

        doc.add(table);
    }

    private String formatLabel(String key) {
        if (key == null) return "";
        String replaced = key.replace('_', ' ');
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;
        for (char c : replaced.toCharArray()) {
            if (c == ' ') {
                capitalizeNext = true;
                result.append(c);
            } else if (capitalizeNext) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    private String formatValue(String key, Object value) {
        if (value == null) return "-";
        double num = toDouble(value);
        if (!Double.isNaN(num)) {
            if (isCurrencyKey(key)) {
                NumberFormat nf = NumberFormat.getInstance(new Locale("en", "IN"));
                nf.setMinimumFractionDigits(2);
                nf.setMaximumFractionDigits(2);
                return "\u20B9" + nf.format(num);
            }
            NumberFormat nf = NumberFormat.getInstance(new Locale("en", "IN"));
            return nf.format(num);
        }
        return value.toString();
    }

    private String formatCellValue(String key, Object value) {
        if (value == null) return "-";
        if (value instanceof Number || (value instanceof String && isNumeric((String) value))) {
            return formatValue(key, value);
        }
        return value.toString();
    }

    private boolean isCurrencyKey(String key) {
        if (key == null) return false;
        String lower = key.toLowerCase();
        return lower.contains("amount") || lower.contains("revenue")
                || lower.contains("value") || lower.contains("tax")
                || lower.contains("discount");
    }

    private double toDouble(Object value) {
        if (value instanceof Number) return ((Number) value).doubleValue();
        if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                return Double.NaN;
            }
        }
        return Double.NaN;
    }

    private boolean isNumeric(String value) {
        if (value == null || value.isEmpty()) return false;
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static class EndOfPageEvent extends PdfPageEventHelper {
        private final RestaurantReportResponse report;

        EndOfPageEvent(RestaurantReportResponse report) {
            this.report = report;
        }

        @Override
        public void onOpenDocument(PdfWriter writer, Document document) {
            // reserved for future font setup if needed
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            try {
                PdfContentByte cb = writer.getDirectContent();
                cb.setRGBColorFill(128, 128, 128);

                float pageWidth = document.getPageSize().getWidth();
                float pageMargin = 36f;

                String pageText = "Page " + writer.getPageNumber();
                float x = pageWidth / 2;
                float y = pageMargin / 2;

                ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                        new Phrase(pageText, new Font(Font.HELVETICA, 8)), x, y, 0);

                String poweredBy = "Clean Report Powered by CafeX POS";
                ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
                        new Phrase(poweredBy, new Font(Font.HELVETICA, 8)), pageWidth - pageMargin, y, 0);
            } catch (Exception e) {
                // ignore footer errors
            }
        }
    }
}
