package com.amigoscode;

import com.amigoscode.customer.Customer;
import com.amigoscode.customer.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Random;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(CustomerRepository customerRepository) {
        return args -> {
            String firstName = "Ying";
            String surname = "Yang";
            Random random = new Random();
            Customer customer = new Customer(
                    firstName + " " + surname,
                    firstName.toLowerCase() + "." + surname.toLowerCase() + "@gmail.com",
                    random.nextInt(16, 99)
            );
//            customerRepository.save(customer);
        };
    }
}
