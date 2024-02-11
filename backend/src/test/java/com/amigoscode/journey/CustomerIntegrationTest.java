package com.amigoscode.journey;

import com.amigoscode.customer.Customer;
import com.amigoscode.customer.CustomerRegistrationRequest;
import com.amigoscode.customer.CustomerUpdateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

// Write for the client's real world journey
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class CustomerIntegrationTest {

    private static final String CUSTOMER_URI = "/api/v1/customer";

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void canRegisterACustomer() {
        String name = "Test name";
        String email = "test26@gmail.com";
        Integer age = 3;
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, email, age
        );

        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        List<Customer> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {})
                .returnResult()
                .getResponseBody();

        Customer expectedCustomer = new Customer(
                name, email, age
        );

        assertThat(allCustomers)
                .isNotNull() // added by me
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .contains(expectedCustomer);

        Long customerId = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        expectedCustomer.setId(customerId);

        Customer customer = webTestClient.get()
                .uri(CUSTOMER_URI + "/{customerId}", customerId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<Customer>() {})
                .returnResult()
                .getResponseBody();
//                .isEqualTo(expectedCustomer); not working, maybe because id is Long

        assertThat(customer).isNotNull();
        assertThat(customer.getId()).isEqualTo(expectedCustomer.getId());
        assertThat(customer.getName()).isEqualTo(expectedCustomer.getName());
        assertThat(customer.getEmail()).isEqualTo(expectedCustomer.getEmail());
        assertThat(customer.getAge()).isEqualTo(expectedCustomer.getAge());

        webTestClient.delete() // so I can run more than once
                .uri(CUSTOMER_URI + "/{customerId}", customerId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange();
    }

    @Test
    void canDeleteCustomer() {
        String name = "Test name";
        String email = "test27@gmail.com";
        Integer age = 3;
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, email, age
        );

        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        List<Customer> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {})
                .returnResult()
                .getResponseBody();

        assertThat(allCustomers)
                .isNotNull(); // added by me

        Long customerId = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        webTestClient.delete()
                .uri(CUSTOMER_URI + "/{customerId}", customerId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();

        webTestClient.get()
                .uri(CUSTOMER_URI + "/{customerId}", customerId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void canUpdateCustomer() {
        // create customer
        String oldName = "Test name";
        String oldEmail = "test28@gmail.com";
        Integer oldAge = 3;
        CustomerRegistrationRequest registrationRequest = new CustomerRegistrationRequest(
                oldName, oldEmail, oldAge
        );
        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(registrationRequest), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();
        // get id
        List<Customer> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {})
                .returnResult()
                .getResponseBody();

        assertThat(allCustomers)
                .isNotNull(); // added by me

        Long customerId = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(oldEmail))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // edit
        String newName = "New test name";
        String newEmail = "test28-changed@gmail.com";
        Integer newAge = 4;
        CustomerUpdateRequest updateRequestRequest = new CustomerUpdateRequest(
                newName, newEmail, newAge
        );
        webTestClient.put()
                .uri(CUSTOMER_URI + "/{customerId}", customerId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(updateRequestRequest), CustomerUpdateRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // check if changed
        Customer changedCustomer = webTestClient.get()
                .uri(CUSTOMER_URI + "/{customerId}", customerId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Customer.class)
                .returnResult()
                .getResponseBody();

        assertThat(changedCustomer).isNotNull();
        assertThat(changedCustomer.getName()).isEqualTo(newName);
        assertThat(changedCustomer.getEmail()).isEqualTo(newEmail);
        assertThat(changedCustomer.getAge()).isEqualTo(newAge);

        webTestClient.delete() // so I can run more than once
                .uri(CUSTOMER_URI + "/{customerId}", customerId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange();
    }
}
