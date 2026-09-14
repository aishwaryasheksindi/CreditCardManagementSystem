package com.crimsonlogic.creditcardmanagementsystem;

import com.crimsonlogic.creditcardmanagementsystem.client.GeminiApiClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

@SpringBootTest(properties = {
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.jpa.show-sql=false"
})
class CreditcardmanagementsystemApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private GeminiApiClient geminiApiClient;

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
        assertNotNull(applicationContext, "ApplicationContext must not be null");
        assertNotNull(geminiApiClient, "GeminiApiClient bean must be successfully instantiated and injected");
    }

}
