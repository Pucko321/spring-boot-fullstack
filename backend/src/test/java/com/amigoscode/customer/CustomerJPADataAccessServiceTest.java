package com.amigoscode.customer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

class CustomerJPADataAccessServiceTest {

    private CustomerJPADataAccessService underTest;
    private AutoCloseable autoCloseable;
    @Mock
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new CustomerJPADataAccessService(customerRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void selectAllCustomers() {
        underTest.selectAllCustomers();

//        Mockito.verify(customerRepository).findAll();
        verify(customerRepository)
                .findAll();
    }

    @Test
    void selectCustomerById() {
        int id = 1;

        underTest.selectCustomerById(id);

        verify(customerRepository)
                .findById(id);
    }

    @Test
    void insertCustomer() {
        Customer customer = new Customer(
                "Test name",
                "test@gmail.com",
                1
        );

        underTest.insertCustomer(customer);

        verify(customerRepository)
                .save(customer);
    }

    @Test
    void existsPersonWithEmail() {
        String email = "test@gmail.com";

        underTest.existsPersonWithEmail(email);

        verify(customerRepository)
                .existsCustomerByEmail(email);
    }

    @Test
    void deleteCustomerById() {
        int id = 1;

        underTest.deleteCustomerById(id);

        verify(customerRepository)
                .deleteById(id);
    }

    @Test
    void existsPersonWithId() {
        int id = 1;

        underTest.existsPersonWithId(id);

        verify(customerRepository)
                .existsById(id);
    }

    @Test
    void updateCustomer() {
        Customer customer = new Customer(
                "Test name",
                "test@gmail.com",
                1
        );

        underTest.updateCustomer(customer);

        verify(customerRepository)
                .save(customer);
    }
}