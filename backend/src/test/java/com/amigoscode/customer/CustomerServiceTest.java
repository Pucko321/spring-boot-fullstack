package com.amigoscode.customer;

import com.amigoscode.exception.DuplicateResourceException;
import com.amigoscode.exception.RequestValidationException;
import com.amigoscode.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // instead of autoCloseable
class CustomerServiceTest {

    @Mock
    private CustomerDao customerDao;
    private CustomerService underTest;

    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDao);
    }

    @Test
    void getAllCustomers() {
        underTest.getAllCustomers();

        verify(customerDao).selectAllCustomers();
    }

    @Test
    void getCustomer_existingId_returnOptionalCustomer() {
        int id = 1;
        Customer customer = new Customer(
                (long) id,
                "Test name",
                "@gmail.com",
                20
        );
        when(customerDao.selectCustomerById(id))
                .thenReturn(Optional.of(customer));

        Customer returnedCustomer = underTest.getCustomer(id);

        assertThat(returnedCustomer).isEqualTo(customer);
    }

    @Test
    void getCustomer_nonExistingId_throwResourceNotFoundException() {
        int id = 1;
        when(customerDao.selectCustomerById(id))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.getCustomer(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id [%s] not found".formatted(id));
    }

    @Test
    void addCustomer_nonExistingEmail_customerInserted() {
        String email = "test@gmail.com";
        when(customerDao.existsPersonWithEmail(email)).thenReturn(false);
        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(
                "Test name", email, 20
        );

        underTest.addCustomer(customerRegistrationRequest);

        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(
                Customer.class
        );
        verify(customerDao).insertCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();
        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getName()).isEqualTo(customerRegistrationRequest.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customerRegistrationRequest.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(customerRegistrationRequest.age());
    }

    @Test
    void addCustomer_existingEmail_throwDuplicateResourceException() {
        String email = "test@gmail.com";
        when(customerDao.existsPersonWithEmail(email)).thenReturn(true);
        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(
                "Test name", email, 20
        );

        assertThatThrownBy(() -> underTest.addCustomer(customerRegistrationRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Email already taken");

        verify(customerDao, never()).insertCustomer(any());
    }

    @Test
    void deleteCustomerById_existingId_customerDeleted() {
        int id = 1;
        when(customerDao.existsPersonWithId(id)).thenReturn(true);

        underTest.deleteCustomerById(id);

        verify(customerDao).deleteCustomerById(id);
    }

    @Test
    void deleteCustomerById_noneExistingId_throwResourceNotFoundException() {
        int id = 1;
        when(customerDao.existsPersonWithId(id)).thenReturn(false);

        assertThatThrownBy(() -> underTest.deleteCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id [%s] not found".formatted(id));

        verify(customerDao, never()).deleteCustomerById(any());
    }

    @Test
    void updateCustomer_existingIdAndNonExistingEmailAndNewFields_customerUpdated() {
        int id = 1;
        Customer customer = new Customer(
                (long) id, "Test name", "test@gmail.com", 11
        );
        String newEmail = "test-updated@gmail.com";
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        CustomerUpdateRequest customerUpdateRequestInput = new CustomerUpdateRequest(
                "Test name updated", newEmail, 22
        );
        when(customerDao.existsPersonWithEmail(newEmail)).thenReturn(false);

        underTest.updateCustomer(id, customerUpdateRequestInput);

        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(
                Customer.class
        );
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();
        assertThat(capturedCustomer.getId()).isEqualTo(id);
        assertThat(capturedCustomer.getName()).isEqualTo(customerUpdateRequestInput.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customerUpdateRequestInput.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(customerUpdateRequestInput.age());
    }

    // Skip for email and age because it's basically the same
    @Test
    void updateCustomer_existingIdAndNonExistingEmailAndNewName_customerUpdated() {
        int id = 1;
        Customer customer = new Customer(
                (long) id, "Test name", "test@gmail.com", 11
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        CustomerUpdateRequest customerUpdateRequestInput = new CustomerUpdateRequest(
                "Test name updated", null, null
        );

        underTest.updateCustomer(id, customerUpdateRequestInput);

        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(
                Customer.class
        );
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();
        assertThat(capturedCustomer.getId()).isEqualTo(id);
        assertThat(capturedCustomer.getName()).isEqualTo(customerUpdateRequestInput.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
    }

    @Test
    void updateCustomer_existingIdAndExistingEmailAndNewEmail_throwDuplicateResourceException() {
        int id = 1;
        Customer customer = new Customer(
                (long) id, "Test name", "test@gmail.com", 11
        );
        String newEmail = "test-updated@gmail.com";
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        CustomerUpdateRequest customerUpdateRequestInput = new CustomerUpdateRequest(
                null, newEmail, null
        );
        when(customerDao.existsPersonWithEmail(newEmail)).thenReturn(true);

        assertThatThrownBy(() -> underTest.updateCustomer(id, customerUpdateRequestInput))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Email already taken");

        verify(customerDao, never()).updateCustomer(any());
    }

    @Test
    void updateCustomer_existingIdAndNonExistingEmailAndNoChanges_throwRequestValidationException() {
        int id = 1;
        Customer customer = new Customer(
                (long) id, "Test name", "test@gmail.com", 11
        );
        when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        CustomerUpdateRequest customerUpdateRequestInput = new CustomerUpdateRequest(
                customer.getName(), customer.getEmail(), customer.getAge()
        );

        assertThatThrownBy(() -> underTest.updateCustomer(id, customerUpdateRequestInput))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("No data changes found");

        verify(customerDao, never()).updateCustomer(any());
    }
}