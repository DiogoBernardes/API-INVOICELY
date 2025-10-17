package com.api.invoicely.service;

import com.api.invoicely.entity.Budget;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class PdfGeneratorService {

    private final FileStorageService fileStorageService;

    public String generateBudgetPdf(Budget budget) {
        try {
            // Calcula total e desconto
            double totalItems = budget.getItens().stream()
                    .mapToDouble(i -> i.getUnitPrice() * i.getQuantity() * (1 + i.getIva() / 100))
                    .sum();

            double discountPercentage = budget.getDiscount() != null ? budget.getDiscount() : 0.0;
            double discountValue = totalItems * (discountPercentage / 100.0);

            // Criar PDF em memória
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // Cabeçalho
            document.add(new Paragraph("Orçamento #" + budget.getId()));
            document.add(new Paragraph("Data: " + budget.getDate().format(DateTimeFormatter.ISO_DATE)));
            document.add(new Paragraph("Cliente: " + budget.getEntity().getName()));
            document.add(new Paragraph("Empresa: " + budget.getCompany().getName()));
            document.add(new Paragraph(" "));

            // Tabela de itens
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.addCell("Item");
            table.addCell("Quantidade");
            table.addCell("Preço Unitário");
            table.addCell("IVA (%)");
            table.addCell("Total c/ IVA");

            budget.getItens().forEach(item -> {
                table.addCell(item.getItem().getName());
                table.addCell(item.getQuantity().toString());
                table.addCell(item.getUnitPrice().toString());
                table.addCell(item.getIva().toString());
                double total = (item.getUnitPrice() * item.getQuantity()) * (1 + item.getIva() / 100);
                table.addCell(String.format("%.2f", total));
            });

            document.add(table);
            document.add(new Paragraph(" "));
            document.add(new Paragraph(String.format("Total dos Itens: %.2f €", totalItems)));
            document.add(new Paragraph(String.format("Desconto (%.2f%%): %.2f €", discountPercentage, discountValue)));
            document.add(new Paragraph(String.format("Total Final: %.2f €", budget.getTotal())));

            document.close();

            // Converter para MultipartFile (para enviar via FileStorageService)
            byte[] pdfBytes = outputStream.toByteArray();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(pdfBytes);

            String fileName = "budget_" + budget.getEntity().getName() + "_" + budget.getDate() + ".pdf";
            MockMultipartFile multipartFile = new MockMultipartFile(
                    fileName,
                    fileName,
                    "application/pdf",
                    inputStream
            );

            // Enviar para o Cloudflare R2
            return fileStorageService.upload(multipartFile,
                    "budgets/" + budget.getCompany().getName().replaceAll("\\s+", "_").toLowerCase());

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar e enviar PDF do orçamento", e);
        }
    }
}
