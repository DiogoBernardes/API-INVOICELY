package com.api.invoicely.controller;

import com.api.invoicely.entity.PaymentMethod;
import com.api.invoicely.repository.PaymentMethodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/payment-methods")
@RequiredArgsConstructor
public class PaymentMethodController {

    private final PaymentMethodRepository paymentMethodRepository;

    @GetMapping("/payment-methods")
    public ResponseEntity<List<PaymentMethod>> getAllActiveMethods() {
        return ResponseEntity.ok(paymentMethodRepository.findAll());
    }
}
