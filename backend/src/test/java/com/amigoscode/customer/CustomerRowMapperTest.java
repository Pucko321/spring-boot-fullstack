package com.amigoscode.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomerRowMapperTest {

    private CustomerRowMapper underTest;

    @BeforeEach
    void setUp() {
        underTest = new CustomerRowMapper();
    }

    @Test
    void mapRow() throws SQLException {
        Customer expectedCustomer = new Customer(
                1L, "Test name", "test@gmail.com", 19,
                Gender.FEMALE);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getLong("id")).thenReturn(expectedCustomer.getId());
        when(resultSet.getInt("age")).thenReturn(expectedCustomer.getAge());
        when(resultSet.getString("name")).thenReturn(expectedCustomer.getName());
        when(resultSet.getString("email")).thenReturn(expectedCustomer.getEmail());
        when(resultSet.getString("gender")).thenReturn(expectedCustomer.getGender().toString());

        Customer actualCustomer = underTest.mapRow(resultSet, 1);

        assertThat(actualCustomer).isNotNull();
        assertThat(actualCustomer.getId()).isEqualTo(expectedCustomer.getId());
        assertThat(actualCustomer.getName()).isEqualTo(expectedCustomer.getName());
        assertThat(actualCustomer.getEmail()).isEqualTo(expectedCustomer.getEmail());
        assertThat(actualCustomer.getAge()).isEqualTo(expectedCustomer.getAge());
        assertThat(actualCustomer.getGender()).isEqualTo(expectedCustomer.getGender());
    }
}