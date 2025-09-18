package org.example.service.impl;

import org.example.model.Order;
import org.example.model.Payment;
import org.example.model.enums.PaymentMed;
import org.example.model.enums.PaymentStatus;
import org.example.repository.PaymentRepository;
import org.example.service.PaymentService;

public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public Payment pay(Order order, PaymentMed method) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }
        if (order.getUser() == null) {
            throw new IllegalArgumentException("User must login");
        }

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setMethod(method);
        payment.setAmount(order.getBook().getPrice() * order.getQuantity());
        payment.setStatus(PaymentStatus.DONE);

        return paymentRepository.save(payment);
    }

}
