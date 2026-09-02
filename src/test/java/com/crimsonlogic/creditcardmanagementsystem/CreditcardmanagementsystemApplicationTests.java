package com.crimsonlogic.creditcardmanagementsystem;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

import static org.mockito.Mockito.mock;

@SpringBootTest(properties = {
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.jpa.show-sql=false"
})
class CreditcardmanagementsystemApplicationTests {

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public DataSource dataSource() {
            return mock(DataSource.class);
        }
    }

    @Test
    void contextLoads() {
    }

}
