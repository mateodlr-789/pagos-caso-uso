/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 *
 * @author Mateo
 */
@Configuration
public class ApplicationProperties {

    @Autowired
    private Dotenv dotenv;

    @Bean
    @Primary
    public DataSource dataSource() {
        return DataSourceBuilder.create()
                .url(dotenv.get("DB_URL"))
                .username(dotenv.get("DB_USER"))
                .password(dotenv.get("DB_PASSWORD"))
                .build();
    }
}
