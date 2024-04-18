package com.function;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseConfig {
    private static final String CONFIG_FILE = "config.properties";

    private Properties properties;

    public DatabaseConfig() {
        properties = new Properties();
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String getJdbcUrl() {
        return properties.getProperty("DB_jdbcUrl");
    }

    public String getUsername() {
        return properties.getProperty("DB_USERNAME");
    }

    public String getPassword() {
        return properties.getProperty("DB_PASSWORD");
    }
}
