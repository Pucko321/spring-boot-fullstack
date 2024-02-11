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
        Customer experctedCustomer = new Customer(
                1L, "Test name", "test@gmail.com", 19
        );
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getLong("id")).thenReturn(experctedCustomer.getId());
        when(resultSet.getInt("age")).thenReturn(experctedCustomer.getAge());
        when(resultSet.getString("name")).thenReturn(experctedCustomer.getName());
        when(resultSet.getString("email")).thenReturn(experctedCustomer.getEmail());

        Customer actualCustomer = underTest.mapRow(resultSet, 1);

        assertThat(actualCustomer).isNotNull();
        assertThat(actualCustomer.getId()).isEqualTo(experctedCustomer.getId());
        assertThat(actualCustomer.getName()).isEqualTo(experctedCustomer.getName());
        assertThat(actualCustomer.getEmail()).isEqualTo(experctedCustomer.getEmail());
        assertThat(actualCustomer.getAge()).isEqualTo(experctedCustomer.getAge());
    }
}