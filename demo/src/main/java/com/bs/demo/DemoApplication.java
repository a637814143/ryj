package com.bs.demo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApplication {

    private static final Logger log = LoggerFactory.getLogger(DemoApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    public CommandLineRunner databaseConnectionVerifier(DataSource dataSource) {
        return args -> {
            try (Connection connection = dataSource.getConnection();
                    PreparedStatement statement = connection.prepareStatement("SELECT 1");
                    ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    log.info("Successfully connected to the MySQL database. Validation result: {}", resultSet.getInt(1));
                }
            } catch (SQLException ex) {
                log.error("Unable to validate the MySQL connection. Please check the datasource configuration.", ex);
            }
        };
    }
}
