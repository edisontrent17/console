package com.acme.data360agent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class Data360AgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(Data360AgentApplication.class, args);
    }
}
