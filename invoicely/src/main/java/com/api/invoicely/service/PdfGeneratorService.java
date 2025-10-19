package com.api.invoicely.service;

import com.api.invoicely.entity.Budget;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import lombok.RequiredArgsConstructor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PdfGeneratorService {

    private final FileStorageService fileStorageService;

    public String generateBudgetPdf(Budget budget) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4, 40, 40, 50, 100);
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();

            // --- Fonts ---
            Font companyFont = new Font(Font.TIMES_ROMAN, 28, Font.BOLD, new Color(51, 51, 51));
            Font labelFont = FontFactory.getFont(FontFactory.TIMES_BOLD, 10, new Color(51, 51, 51));
            Font valueFont = FontFactory.getFont(FontFactory.TIMES, 10, new Color(51, 51, 51));
            Font tableHeaderFont = FontFactory.getFont(FontFactory.TIMES_BOLD, 11, Color.WHITE);

            // --- Header (nome empresa à esquerda / data à direita) ---
            PdfPTable headerLine = new PdfPTable(2);
            headerLine.setWidthPercentage(100);
            headerLine.setWidths(new float[]{2f, 1f});
            headerLine.getDefaultCell().setBorder(Rectangle.NO_BORDER);

            // Nome empresa
            Paragraph companyName = new Paragraph(budget.getCompany().getName(), companyFont);
            PdfPCell nameCell = new PdfPCell(companyName);
            nameCell.setBorder(Rectangle.NO_BORDER);
            nameCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            nameCell.setPaddingBottom(20f);
            headerLine.addCell(nameCell);

            // Data
            String dateStr = java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            Paragraph datePara = new Paragraph(dateStr);
            datePara.setFont(labelFont);
            PdfPCell dateCell = new PdfPCell(datePara);
            dateCell.setBorder(Rectangle.NO_BORDER);
            dateCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            dateCell.setPaddingBottom(20f);
            headerLine.addCell(dateCell);

            document.add(headerLine);
            document.add(Chunk.NEWLINE);

            // --- Info Empresa e Cliente ---
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setWidths(new float[]{2f, 1f});
            infoTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

            // Coluna esquerda: Empresa
            Paragraph companyInfo = new Paragraph();
            companyInfo.setFont(valueFont);
            companyInfo.setSpacingBefore(5f);
            companyInfo.add(new Chunk("NIF: ", labelFont));
            companyInfo.add(new Chunk(budget.getCompany().getNif() + "\n", valueFont));
            companyInfo.add(new Chunk("Telemóvel: ", labelFont));
            companyInfo.add(new Chunk(budget.getCompany().getPhone() + "\n", valueFont));
            companyInfo.add(new Chunk("Email: ", labelFont));
            companyInfo.add(new Chunk(budget.getCompany().getEmail() + "\n", valueFont));
            companyInfo.add(new Chunk("Endereço: ", labelFont));
            companyInfo.add(new Chunk(budget.getCompany().getAddress(), valueFont));

            PdfPCell companyInfoCell = new PdfPCell(companyInfo);
            companyInfoCell.setBorder(Rectangle.NO_BORDER);
            companyInfoCell.setPaddingRight(50f);
            companyInfoCell.setPaddingBottom(50f);
            infoTable.addCell(companyInfoCell);

            // Coluna direita: Cliente
            Paragraph clientInfo = new Paragraph();
            clientInfo.setFont(valueFont);
            clientInfo.setSpacingBefore(5f);
            clientInfo.add(new Chunk("Nome: ", labelFont));
            clientInfo.add(new Chunk(budget.getEntity().getName() + "\n", valueFont));
            clientInfo.add(new Chunk("NIF: ", labelFont));
            clientInfo.add(new Chunk(budget.getEntity().getNif() + "\n", valueFont));
            clientInfo.add(new Chunk("Telemóvel: ", labelFont));
            clientInfo.add(new Chunk(budget.getEntity().getPhone() + "\n", valueFont));
            clientInfo.add(new Chunk("Email: ", labelFont));
            clientInfo.add(new Chunk(budget.getEntity().getEmail() + "\n", valueFont));
            clientInfo.add(new Chunk("Endereço: ", labelFont));
            clientInfo.add(new Chunk(budget.getEntity().getAddress(), valueFont));

            PdfPCell clientInfoCell = new PdfPCell(clientInfo);
            clientInfoCell.setBorder(Rectangle.NO_BORDER);
            clientInfoCell.setPaddingBottom(50f);
            infoTable.addCell(clientInfoCell);

            document.add(infoTable);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);

            // --- Tabela Produtos/Serviços ---
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{3f, 1f, 1f, 1f, 1f});
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);
            Color headerColor = new Color(47, 49, 49);
            addTableHeader(table, "Nome", tableHeaderFont, headerColor);
            addTableHeader(table, "Preço", tableHeaderFont, headerColor);
            addTableHeader(table, "IVA", tableHeaderFont, headerColor);
            addTableHeader(table, "Quantidade", tableHeaderFont, headerColor);
            addTableHeader(table, "Total", tableHeaderFont, headerColor);

            for (var i : budget.getItens()) {
                if (i.getRemovedAt() != null) continue;
                String name = i.getItem() != null ? i.getItem().getName() : "—";
                String price = String.format("%.2f €", i.getUnitPrice());
                String iva = String.format("%.2f%%", i.getIva());
                String qty = String.format("%.2f", i.getQuantity());
                double total = i.getUnitPrice() * i.getQuantity() * (1 + i.getIva() / 100.0);
                String totalStr = String.format("%.2f €", total);

                addTableCell(table, name, valueFont);
                addTableCell(table, price, valueFont);
                addTableCell(table, iva, valueFont);
                addTableCell(table, qty, valueFont);
                addTableCell(table, totalStr, valueFont);
            }

            document.add(table);

            // --- Totais ---
            double subtotal = budget.getItens().stream()
                    .filter(it -> it.getRemovedAt() == null)
                    .mapToDouble(it -> it.getUnitPrice() * it.getQuantity() * (1 + it.getIva() / 100.0))
                    .sum();
            double discountPct = budget.getDiscount() != null ? budget.getDiscount() : 0.0;
            double totalFinal = subtotal - subtotal * discountPct / 100.0;

            PdfPTable totalsTable = new PdfPTable(2);
            totalsTable.setWidthPercentage(50);
            totalsTable.setHorizontalAlignment(Element.ALIGN_RIGHT);

            addTotalRow(totalsTable, "Subtotal", String.format("%.2f €", subtotal), valueFont);
            if (discountPct > 0) addTotalRow(totalsTable, "Desconto", String.format("%.2f %%", discountPct), valueFont);
            addTotalRow(totalsTable, "Total", String.format("%.2f €", totalFinal), valueFont);

            document.add(totalsTable);

            PdfContentByte canvas = writer.getDirectContent();
            float footerY = 75f; // altura da linha da assinatura

            // --- Stamp acima da assinatura, alinhado à esquerda mas deslocado à direita ---
            if (budget.getCompany().getStamp() != null) {
                Image stampImg = Image.getInstance(budget.getCompany().getStamp());
                stampImg.scaleToFit(125, 65);

                float stampX = 100 + (stampImg.getScaledWidth() / 2);
                float stampY = footerY + 5;
                stampImg.setAbsolutePosition(stampX - (stampImg.getScaledWidth() / 2), stampY);
                canvas.addImage(stampImg);
            }

            ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
                    new Phrase("Assinatura: ____________________", labelFont),
                    40, footerY, 0);

            if (budget.getCompany().getLogo() != null) {
                Image logoImg = Image.getInstance(budget.getCompany().getLogo());
                logoImg.scaleToFit(125, 65);
                float logoX = PageSize.A4.getWidth() - 40 - logoImg.getScaledWidth();
                float logoY = footerY;
                logoImg.setAbsolutePosition(logoX, logoY);
                canvas.addImage(logoImg);
            }
            document.close();

            // --- Upload PDF ---
            String sanitizedCompany = sanitizeForFileName(budget.getCompany().getName());
            String readableFilename = "budget_" + sanitizedCompany + "_" + budget.getDate() + ".pdf";
            String uploadFileName = UUID.randomUUID() + "_" + readableFilename;

            MockMultipartFile multipart = new MockMultipartFile(
                    uploadFileName,
                    readableFilename,
                    "application/pdf",
                    new ByteArrayInputStream(baos.toByteArray())
            );

            return fileStorageService.upload(multipart, "budgets/" + sanitizedCompany);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF", e);
        }
    }

    private void addTableHeader(PdfPTable table, String text, Font font, Color bgColor) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBackgroundColor(bgColor);
        cell.setPadding(5);
        table.addCell(cell);
    }

    private void addTableCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(5);
        table.addCell(cell);
    }

    private void addTotalRow(PdfPTable table, String label, String value, Font font) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, font));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, font));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(valueCell);
    }

    private String sanitizeForFileName(String input) {
        if (input == null) return "company";
        String normalized = java.text.Normalizer.normalize(input, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return normalized.replaceAll("[^a-zA-Z0-9_-]", "_").toLowerCase();
    }
}
