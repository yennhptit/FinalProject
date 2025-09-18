package org.example.service.impl;

import org.example.model.*;
import org.example.model.enums.PaymentMed;
import org.example.model.enums.PaymentStatus;
import org.example.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
class PaymentServiceImplTest {

    private PaymentRepository paymentRepository;
    private PaymentServiceImpl paymentService;

    private User user;
    private Book book;
    private Order order;

    @BeforeEach
    void setup() {
        paymentRepository = mock(PaymentRepository.class, withSettings().lenient());
        paymentService = new PaymentServiceImpl(paymentRepository);

        user = new User();
        user.setId(1L);
        user.setUsername("test");

        book = new Book();
        book.setId(10L);
        book.setTitle("Test Book");
        book.setPrice(100);

        order = new Order();
        order.setId(1L);
        order.setBook(book);
        order.setQuantity(3);
        order.setCanceled(false);
        order.setUser(user);
    }

    @Test
    void testPayCashSuccess() {
        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Payment payment = paymentService.pay(order, PaymentMed.CASH);

        assertThat(payment, is(notNullValue()));
        assertThat(payment.getOrder(), is(order));
        assertThat(payment.getMethod(), is(PaymentMed.CASH));
        assertThat(payment.getAmount(), is(300.0)); // 100 * 3
        assertThat(payment.getStatus(), is(PaymentStatus.DONE));

        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void testPayCreditSuccess() {
        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Payment payment = paymentService.pay(order, PaymentMed.CREDIT);

        assertThat(payment.getMethod(), is(PaymentMed.CREDIT));
        assertThat(payment.getAmount(), is(300.0));
        assertThat(payment.getStatus(), is(PaymentStatus.DONE));
    }

    @Test
    void testPayOrderNullThrowsException() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> paymentService.pay(null, PaymentMed.CASH)
        );

        assertThat(ex.getMessage(), is("Order cannot be null"));
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void testPayUserNotLoggedInThrowsException() {
        order.setUser(null); // user chưa login / session hết hạn

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> paymentService.pay(order, PaymentMed.CASH)
        );

        assertThat(ex.getMessage(), is("User must login"));
        verify(paymentRepository, never()).save(any(Payment.class));
    }
}
