package com.erkan.reading.is.good.service;

import com.erkan.reading.is.good.controller.model.request.CustomerRequest;
import com.erkan.reading.is.good.controller.model.response.CustomerResponse;
import com.erkan.reading.is.good.controller.model.response.OrderResponse;
import com.erkan.reading.is.good.entity.Book;
import com.erkan.reading.is.good.entity.Customer;
import com.erkan.reading.is.good.entity.OrderHeader;
import com.erkan.reading.is.good.entity.OrderLine;
import com.erkan.reading.is.good.exception.CustomerNotFoundException;
import com.erkan.reading.is.good.repository.CustomerRepository;
import com.erkan.reading.is.good.repository.OrderHeaderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @InjectMocks
    @Spy
    private CustomerService customerService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private OrderHeaderRepository orderHeaderRepository;
    private static CustomerRequest customerRequest;

    @BeforeEach
    public void init() {
        customerRequest = CustomerRequest.builder()
                .name("Erkan")
                .build();
    }

    @Test()
    public void createCustomerTest_whenCustomerRequestComesIn_thenReturnSuccessCustomerResponse() {
        // GIVEN
        Customer customer =  Customer.builder()
                .name("Erkan")
                .id(1L)
                .build();
        when(customerRepository.save(any(Customer.class)))
                .thenReturn(customer);
        // WHEN
        CustomerResponse customerResponse = customerService.createCustomer(customerRequest);
        // THEN
        assertThat(customerResponse.getId()).isEqualTo(1L);
        assertThat(customerResponse.getName()).isEqualTo("Erkan");
    }

    @Test()
    public void getOrdersFromCustomerTest_whenCustomerNotFound_thenThrowCustomerNotFoundException() {
        // GIVEN
        when(customerRepository.findById(1L))
                .thenThrow(new CustomerNotFoundException());
        // WHEN THEN
        assertThrows(CustomerNotFoundException.class, () -> customerService.getOrdersFromCustomer(1L));
    }

    @Test()
    public void getOrdersFromCustomerTest_whenOrdersFetched_thenReturnOrderResponseList() {
        // GIVEN
        Customer c1 = Customer.builder()
                .id(1L)
                .name("Customer1").build();
        OrderLine orderLine = OrderLine.builder()
                .amount(new BigDecimal("12"))
                .book(Book.builder()
                        .id(1L)
                        .name("Book1").build())
                .id(1L).build();
        OrderHeader oh = OrderHeader.builder()
                .customer(c1)
                .id(1L)
                .totalAmount(new BigDecimal("100"))
                .lines(Collections.singletonList(orderLine)).build();
        Page<OrderHeader> page = new PageImpl<>(Collections.singletonList(oh));
        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(c1));
        when(orderHeaderRepository.findAllByCustomer(eq(c1), any(Pageable.class)))
                .thenReturn(page);

        // WHEN
        List<OrderResponse> list = customerService.getOrdersFromCustomer(1L);
        // THEN
        assertThat(list.get(0).getLines().get(0).getAmount()).isEqualTo(new BigDecimal("12"));
        assertThat(list.get(0).getLines().get(0).getId()).isEqualTo(1);

    }
}
