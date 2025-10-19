package com.api.invoicely.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String body) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true);
        helper.setFrom("invoicely.supp@gmail.com", "Invoicely Suporte");

        mailSender.send(message);
    }

    public void sendEmailWithAttachmentInlineLogo(String to, String subject, String body,
                                                  byte[] attachment, String attachmentName, byte[] logoBytes) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true);
        helper.setFrom("invoicely.supp@gmail.com", "Invoicely Suporte");

        // Anexo PDF
        helper.addAttachment(attachmentName, new ByteArrayResource(attachment));

        // Logo inline
        if (logoBytes != null && logoBytes.length > 0) {
            helper.addInline("companyLogo", new ByteArrayResource(logoBytes), "image/png");
        }

        mailSender.send(message);
    }

}
