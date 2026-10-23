package com.bittercode.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class DatabaseConfigTest {

    @Test
    void loadsValuesFromTestApplicationProperties() {
        assertEquals("org.postgresql.Driver", DatabaseConfig.DRIVER_NAME);
        assertEquals("testdb", DatabaseConfig.DB_NAME);
        assertEquals("test_user", DatabaseConfig.DB_USER_NAME);
        assertEquals("test_password", DatabaseConfig.DB_PASSWORD);
        assertEquals("jdbc:postgresql://127.0.0.1:59999/testdb", DatabaseConfig.CONNECTION_STRING);
    }
}
