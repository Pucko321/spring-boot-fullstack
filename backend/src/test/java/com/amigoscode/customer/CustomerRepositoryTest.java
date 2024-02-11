package com.amigoscode.customer;

import com.amigoscode.AbstractTestcontainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContext;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerRepositoryTest extends AbstractTestcontainers {

    @Autowired
    private CustomerRepository underTest;

    @Autowired
    private ApplicationContext applicationContext;

    @BeforeEach
    void setUp() {
        Customer testCustomer = new Customer(
                "Test name",
                "test-" + UUID.randomUUID() + "@gmail.com",
                1
        );
        underTest.save(testCustomer);
        System.out.println("Beans " + applicationContext.getBeanDefinitionCount());
    }

    @Test
    void customerExistsByEmail_existingEmail_true() {
        Customer testCustomer = new Customer(
                "Test name",
                "test-" + UUID.randomUUID() + "@gmail.com",
                1
        );
        underTest.save(testCustomer);

        boolean customerExistWithEmail = underTest.existsCustomerByEmail(testCustomer.getEmail());

        assertThat(customerExistWithEmail)
                .isTrue();
    }

    @Test
    void customerExistsByEmail_nonExistingEmail_false() {
        String emailThatDoesNotExist = "@gmail.com";
        boolean customerExistWithEmail = underTest.existsCustomerByEmail(emailThatDoesNotExist);

        assertThat(customerExistWithEmail)
                .isFalse();
    }
}