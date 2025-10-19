package com.api.invoicely.service;

import com.api.invoicely.dto.budget.BudgetCreateDTO;
import com.api.invoicely.dto.budget.BudgetResponseDTO;
import com.api.invoicely.entity.*;
import com.api.invoicely.exceptions.ApiException;
import com.api.invoicely.mapper.BudgetMapper;
import com.api.invoicely.repository.BudgetRepository;
import com.api.invoicely.repository.CommercialItemRepository;
import com.api.invoicely.repository.EntitiesRepository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final EntitiesRepository entitiesRepository;
    private final CommercialItemRepository commercialItemRepository;
    private final PdfGeneratorService pdfGeneratorService;
    private final EmailService emailService;
    private final FileStorageService fileStorageService;

    @Transactional
    public BudgetResponseDTO createBudget(User owner, BudgetCreateDTO dto) {
        Company company = owner.getCompany();
        Entities entity = entitiesRepository.findById(dto.getEntityId())
                .orElseThrow(() -> new ApiException("Entidade não encontrada", HttpStatus.NOT_FOUND));

        Budget budget = Budget.builder()
                .company(company)
                .entity(entity)
                .date(dto.getDate())
                .discount(dto.getDiscount() != null ? dto.getDiscount() : 0.0)
                .total(dto.getTotal())
                .state(dto.getState())
                .build();

        List<ItemBudget> items = dto.getItems().stream().map(itemDto -> {
            CommercialItem item = commercialItemRepository.findById(itemDto.getItemId())
                    .orElseThrow(() -> new ApiException("Item comercial não encontrado", HttpStatus.NOT_FOUND));
            return ItemBudget.builder()
                    .company(company)
                    .budget(budget)
                    .item(item)
                    .quantity(itemDto.getQuantity())
                    .unitPrice(itemDto.getUnitPrice())
                    .iva(itemDto.getIva())
                    .build();
        }).collect(Collectors.toList());

        budget.setItens(items);

        // Calcula o total dos itens com IVA
        double totalItems = items.stream()
                .mapToDouble(i -> i.getUnitPrice() * i.getQuantity() * (1 + i.getIva() / 100))
                .sum();

        // Calcula valor do desconto em percentagem
        double discountPercentage = budget.getDiscount();
        double discountValue = totalItems * (discountPercentage / 100.0);

        // Total final = totalItems - desconto
        double totalFinal = totalItems - discountValue;
        budget.setTotal(totalFinal);

        budgetRepository.save(budget);
        return BudgetMapper.toBudgetDTO(budget);
    }

    public BudgetResponseDTO getBudgetById(User owner, UUID id) {
        Budget budget = budgetRepository.findActiveByIdAndCompanyId(id, owner.getCompany().getId())
                .orElseThrow(() -> new ApiException("Orçamento não encontrado", HttpStatus.NOT_FOUND));
        return BudgetMapper.toBudgetDTO(budget);
    }

    public List<BudgetResponseDTO> getAllBudgets(User owner, String entityName, String state, LocalDate startDate,
                                                 LocalDate endDate, LocalDate dateAfter, LocalDate dateBefore) {
        List<Budget> budgets = budgetRepository.findActiveByCompany(owner.getCompany());

        return budgets.stream()
                .filter(b -> entityName == null || b.getEntity().getName().toLowerCase().contains(entityName.toLowerCase()))
                .filter(b -> {
                    if (state == null) return true;
                    try {
                        Budget.BudgetStatus s = Budget.BudgetStatus.valueOf(state.trim().toUpperCase());
                        return b.getState() == s;
                    } catch (IllegalArgumentException e) {
                        throw new ApiException("Tipo de estado inválido: " + state, HttpStatus.BAD_REQUEST);
                    }
                })
                .filter(b -> startDate == null || (b.getDate() != null && !b.getDate().isBefore(startDate)))
                .filter(b -> endDate == null || (b.getDate() != null && !b.getDate().isAfter(endDate)))
                .filter(b -> dateAfter == null || (b.getDate() != null && b.getDate().isAfter(dateAfter)))
                .filter(b -> dateBefore == null || (b.getDate() != null && b.getDate().isBefore(dateBefore)))
                .map(BudgetMapper::toBudgetDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteBudget(User owner, UUID id) {
        Budget budget = budgetRepository.findActiveByIdAndCompanyId(id, owner.getCompany().getId())
                .orElseThrow(() -> new ApiException("Orçamento não encontrado", HttpStatus.NOT_FOUND));

        budget.setRemovedAt(LocalDateTime.now());
        budgetRepository.save(budget);
    }

    @Transactional
    public BudgetResponseDTO generateBudgetPdf(User owner, UUID id) {
        Budget budget = budgetRepository.findActiveByIdAndCompanyId(id, owner.getCompany().getId())
                .orElseThrow(() -> new ApiException("Orçamento não encontrado", HttpStatus.NOT_FOUND));

        String pdfUrl = pdfGeneratorService.generateBudgetPdf(budget);
        budget.setPdfUrl(pdfUrl);
        budget.setPdfGeneratedAt(LocalDateTime.now());
        budgetRepository.save(budget);

        return BudgetMapper.toBudgetDTO(budget);
    }

    @Transactional
    public void sendBudgetToClient(User owner, UUID id) {
        Budget budget = budgetRepository.findActiveByIdAndCompanyId(id, owner.getCompany().getId())
                .orElseThrow(() -> new ApiException("Orçamento não encontrado", HttpStatus.NOT_FOUND));

        if (budget.getPdfUrl() == null)
            throw new ApiException("O PDF ainda não foi gerado. Gere primeiro antes de enviar.", HttpStatus.BAD_REQUEST);

        String clientEmail = budget.getEntity().getEmail();
        if (clientEmail == null || clientEmail.isBlank())
            throw new ApiException("A entidade não possui email definido.", HttpStatus.BAD_REQUEST);

        String subject = "Orçamento " + budget.getEntity().getName() + " - " + budget.getDate();



        String logoHtml = "";
        if (budget.getCompany().getLogo() != null && budget.getCompany().getLogo().length > 0) {
            logoHtml = "<img src='cid:companyLogo' width='100'/>";
        }

        String body = String.format("""
        <p>Olá <b>%s</b>,</p>
        <p>Segue em anexo o orçamento solicitado para a sua consulta.</p>
        <p>Com os melhores cumprimentos,</p>
        <table style="font-family:Arial,sans-serif; font-size:12px; margin-top:10px; border-collapse:collapse;">
            <tr>
                <td rowspan="4" style="padding-right:10px; vertical-align:top;">%s</td>
                <td style="font-size:20px;"><b>%s</b></td>
            </tr>
            <tr><td style="padding:0; margin:0;">📞 %s</td></tr>
            <tr><td style="padding:0; margin:0;">✉️ %s</td></tr>
            <tr><td style="padding:0; margin:0;">📍 %s</td></tr>
        </table>
        """,
                budget.getEntity().getName(),
                logoHtml,
                budget.getCompany().getName(),
                budget.getCompany().getPhone() != null ? budget.getCompany().getPhone() : "",
                budget.getCompany().getEmail() != null ? budget.getCompany().getEmail() : "",
                budget.getCompany().getAddress() != null ? budget.getCompany().getAddress() : ""
        );

        try {
            // Extrai bucket e key do PDF URL
            String urlWithoutQuery = budget.getPdfUrl().split("\\?")[0];
            String bucket = fileStorageService.getR2Properties().getBucket();
            String key = urlWithoutQuery.substring(urlWithoutQuery.indexOf(bucket) + bucket.length() + 1);

            // Baixa PDF como byte[] diretamente do R2
            byte[] pdfBytes = fileStorageService.getS3Client().getObjectAsBytes(
                    software.amazon.awssdk.services.s3.model.GetObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .build()
            ).asByteArray();

            String filename = ("budget_" + budget.getEntity().getName() + "_" + budget.getDate())
                    .replaceAll("[^a-zA-Z0-9._-]", "_") + ".pdf";
            emailService.sendEmailWithAttachmentInlineLogo(clientEmail, subject, body, pdfBytes,
                    filename, budget.getCompany().getLogo());

        } catch (MessagingException e) {
            throw new ApiException("Falha ao enviar email para o cliente.", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }


    @Transactional
    public void sendBudgetByEmail(User owner, UUID id, String recipientEmail) {
        Budget budget = budgetRepository.findActiveByIdAndCompanyId(id, owner.getCompany().getId())
                .orElseThrow(() -> new ApiException("Orçamento não encontrado", HttpStatus.NOT_FOUND));

        if (budget.getPdfUrl() == null)
            throw new ApiException("O PDF ainda não foi gerado. Gere primeiro antes de enviar.", HttpStatus.BAD_REQUEST);

        if (recipientEmail == null || recipientEmail.isBlank())
            throw new ApiException("Nenhum email inserido.", HttpStatus.BAD_REQUEST);

        String subject = "Orçamento " + budget.getEntity().getName() + " - " + budget.getDate();


        Company company = owner.getCompany();
        String logoHtml = "";
        if (company.getLogo() != null && company.getLogo().length > 0) {
            logoHtml = "<img src='cid:companyLogo' width='100'/>";
        }

        String body = String.format("""
        <p>Olá,</p>
        <p>Segue em anexo o orçamento emitido pela empresa <b>%s</b> referente ao cliente <b>%s</b>.</p>
        <p>Com os melhores cumprimentos,</p>
        <table style="font-family:Arial,sans-serif; font-size:12px; margin-top:10px; border-collapse:collapse;">
            <tr>
                <td rowspan="4" style="padding-right:25px; vertical-align:top;">%s</td>
                <td style="font-size:20px;"><b>%s</b></td>
            </tr>
            <tr><td style="padding:0; margin:0;">📞 %s</td></tr>
            <tr><td style="padding:0; margin:0;">✉️ %s</td></tr>
            <tr><td style="padding:0; margin:0;">📍 %s</td></tr>
        </table>
        """,
                company.getName(),
                budget.getEntity().getName(),
                logoHtml,
                company.getName(),
                company.getPhone() != null ? company.getPhone() : "-",
                company.getEmail() != null ? company.getEmail() : "-",
                company.getAddress() != null ? company.getAddress() : "-"
        );

        try {
            String urlWithoutQuery = budget.getPdfUrl().split("\\?")[0];
            String bucket = fileStorageService.getR2Properties().getBucket();
            String key = urlWithoutQuery.substring(urlWithoutQuery.indexOf(bucket) + bucket.length() + 1);

            byte[] pdfBytes = fileStorageService.getS3Client().getObjectAsBytes(
                    software.amazon.awssdk.services.s3.model.GetObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .build()
            ).asByteArray();

            String filename = ("budget_" + budget.getEntity().getName() + "_" + budget.getDate())
                    .replaceAll("[^a-zA-Z0-9._-]", "_") + ".pdf";

            emailService.sendEmailWithAttachmentInlineLogo(recipientEmail, subject, body, pdfBytes, filename, company.getLogo());

            budgetRepository.save(budget);

        } catch (MessagingException e) {
            throw new ApiException("Falha ao enviar email.", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            throw new ApiException("Erro ao descarregar o PDF do orçamento.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
