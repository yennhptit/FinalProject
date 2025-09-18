package org.example.model;

import lombok.*;
import org.example.model.enums.PaymentMed;
import org.example.model.enums.PaymentStatus;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    private Long id;
    private Order order;
    private PaymentMed method;
    private double amount;
    private PaymentStatus status;
}

