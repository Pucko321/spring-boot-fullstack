package com.amigoscode.customer;

import com.amigoscode.AbstractTestcontainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CustomerJDBCDataAccessServiceTest extends AbstractTestcontainers {

    private CustomerJDBCDataAccessService underTest;
    private final CustomerRowMapper customerRowMapper = new CustomerRowMapper();

    @BeforeEach
    void setUp() {
        underTest = new CustomerJDBCDataAccessService(
                getJdbcTemplate(),
                customerRowMapper
        );
    }

    @Test
    void selectAllCustomers() {
        Customer customer = new Customer(
                "Test name",
                "test-" + UUID.randomUUID() + "@gmail.com",
                1
        );
        underTest.insertCustomer(customer);

        List<Customer> customers = underTest.selectAllCustomers();

        assertThat(customers).isNotEmpty();
    }

    @Test
    void selectCustomerById() {
        String email = "test-" + UUID.randomUUID() + "@gmail.com";
        Customer testCustomer = new Customer(
                "Test name",
                email,
                1
        );
        underTest.insertCustomer(testCustomer);

        int id = underTest.selectAllCustomers().stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(customer -> Math.toIntExact(customer.getId()))
                .findFirst()
                .orElseThrow();

        Optional<Customer> actualCustomer = underTest.selectCustomerById(id);

        assertThat(actualCustomer)
                .isPresent()
                .hasValueSatisfying(customer -> {
                    assertThat(customer.getId()).isEqualTo(id);
                    assertThat(customer.getName()).isEqualTo(testCustomer.getName());
                    assertThat(customer.getEmail()).isEqualTo(testCustomer.getEmail());
                    assertThat(customer.getAge()).isEqualTo(testCustomer.getAge());
                });
    }

    // Amigoscode naming*
    @Test
    void willReturnEmptyWhenSelectCustomerById() {
        int idThatDoesNotExist = -1;

        Optional<Customer> actualCustomer = underTest.selectCustomerById(idThatDoesNotExist);

        assertThat(actualCustomer).isEmpty();
    }

    @Test
    void insertCustomer() {
        Customer testCustomer = new Customer(
                "Test name",
                "test-" + UUID.randomUUID() + "@gmail.com",
                1
        );

        Optional<Customer> customerResultBeforeInsert = underTest.selectAllCustomers().stream()
                .filter(customer -> customer.getEmail().equals(testCustomer.getEmail()))
                .findFirst();
        underTest.insertCustomer(testCustomer);
        Optional<Customer> customerResultAfterInsert = underTest.selectAllCustomers().stream()
                .filter(customer -> customer.getEmail().equals(testCustomer.getEmail()))
                .findFirst();

        assertThat(customerResultBeforeInsert)
                .isEmpty();
        assertThat(customerResultAfterInsert)
                .isPresent()
                .hasValueSatisfying(customer -> {
                    assertThat(customer.getName()).isEqualTo(testCustomer.getName());
                    assertThat(customer.getEmail()).isEqualTo(testCustomer.getEmail());
                    assertThat(customer.getAge()).isEqualTo(testCustomer.getAge());
                });
    }

    @Test
    void existsPersonWithEmail() {
        Customer testCustomer = new Customer(
                "Test name",
                "test-" + UUID.randomUUID() + "@gmail.com",
                1
        );

        underTest.insertCustomer(testCustomer);
        boolean testCustomerExist = underTest.existsPersonWithEmail(testCustomer.getEmail());

        assertThat(testCustomerExist)
                .isTrue();
    }

    @Test
    void personExistsWithEmail_emailDoesNotExist_false() {
        String emailThatDoesNotExist = "@nothing.com";

        boolean emailExists = underTest.existsPersonWithEmail(emailThatDoesNotExist);

        assertThat(emailExists).isFalse();
    }

    @Test
    void deleteCustomerById() {
        String email = "test-" + UUID.randomUUID() + "@gmail.com";
        Customer testCustomer = new Customer(
                "Test name",
                email,
                1
        );
        underTest.insertCustomer(testCustomer);
        int id = underTest.selectAllCustomers().stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(customer -> Math.toIntExact(customer.getId()))
                .findFirst()
                .orElseThrow();

        underTest.deleteCustomerById(id);

        boolean testCustomerExist = underTest.existsPersonWithId(id);
        assertThat(testCustomerExist)
                .isFalse();
    }

    // A possible test naming convention:
    // featureBeingTested_stateUnderTest_expectedBehavior
    @Test
    void personExistsWithId_idDoesNotExist_false() {
        int idThatDoesNotExist = -1;

        boolean personExistsWithId = underTest.existsPersonWithId(idThatDoesNotExist);

        assertThat(personExistsWithId)
                .isFalse();
    }

    @Test
    void personExistsWithId_idExist_true() {
        String email = "test-" + UUID.randomUUID() + "@gmail.com";
        Customer testCustomer = new Customer(
                "Test name",
                email,
                1
        );

        underTest.insertCustomer(testCustomer);
        int id = underTest.selectAllCustomers().stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(customer -> Math.toIntExact(customer.getId()))
                .findFirst()
                .orElseThrow();
        boolean personExistsWithId = underTest.existsPersonWithId(id);

        assertThat(personExistsWithId)
                .isTrue();
    }

    @Test
    void updateCustomer_allFieldsAreNew_allFieldsUpdated() {
        String oldEmail = "test-" + UUID.randomUUID() + "@gmail.com";
        Customer oldCustomer = new Customer(
                "Old Test Name",
                oldEmail,
                1
        );
        Customer newCustomer = new Customer(
                "New Test Name",
                "new" + oldEmail,
                2
        );

        underTest.insertCustomer(oldCustomer);
        Long id = underTest.selectAllCustomers().stream()
                .filter(customer -> customer.getEmail().equals(oldEmail))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();
        newCustomer.setId(id);
        underTest.updateCustomer(newCustomer);
        Optional<Customer> updatedCustomer = underTest.selectCustomerById(Math.toIntExact(id));

        assertThat(updatedCustomer)
                .isPresent()
                .hasValueSatisfying(customer -> {
                    assertThat(customer.getName()).isEqualTo(newCustomer.getName());
                    assertThat(customer.getEmail()).isEqualTo(newCustomer.getEmail());
                    assertThat(customer.getAge()).isEqualTo(newCustomer.getAge());
                });
    }
}