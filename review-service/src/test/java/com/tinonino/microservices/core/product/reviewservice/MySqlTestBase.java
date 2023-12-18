package com.tinonino.microservices.core.product.reviewservice;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MySQLContainer;

public abstract class MySqlTestBase {
    // Extend startup timeout since a MySQLContainer with MySQL 8 starts very slow on Win10/WSL2
    @ServiceConnection
    private static JdbcDatabaseContainer database = new MySQLContainer("mysql:8.0.32").withStartupTimeoutSeconds(600);

    static {
        database.start();
    }
}
