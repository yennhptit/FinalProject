package org.example.service;

import org.example.model.Order;
import org.example.model.Payment;
import org.example.model.enums.PaymentMed;

public interface PaymentService {
    Payment pay(Order order, PaymentMed method);
}
