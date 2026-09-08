package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * TestConfig class handles reading configuration from both:
 * 1. Environment variables (for sensitive data like credentials)
 * 2. config.properties file (for non-sensitive configuration)
 * 
 * Priority: Environment variables override config.properties values
 */
public class TestConfig {
    
    private Properties properties;
    private static final String CONFIG_FILE_PATH = "src/test/resources/config.properties";
    
    public TestConfig() {
        properties = new Properties();
        loadProperties();
    }
    
    /**
     * Load properties from config file
     */
    private void loadProperties() {
        try {
            FileInputStream fis = new FileInputStream(CONFIG_FILE_PATH);
            properties.load(fis);
            fis.close();
        } catch (IOException e) {
            System.err.println("Error loading config.properties file: " + e.getMessage());
            System.err.println("Please ensure config.properties exists at: " + CONFIG_FILE_PATH);
        }
    }
    
    /**
     * Get property value by key
     * First checks environment variables, then falls back to properties file
     * 
     * @param key Property key
     * @return Property value
     */
    public String getProperty(String key) {
        // First try to get from environment variable
        String envValue = getEnvironmentVariable(key);
        if (envValue != null && !envValue.isEmpty()) {
            return envValue;
        }
        
        // Fall back to properties file
        return properties.getProperty(key);
    }
    
    /**
     * Get property value with default fallback
     * 
     * @param key Property key
     * @param defaultValue Default value if key not found
     * @return Property value or default value
     */
    public String getProperty(String key, String defaultValue) {
        // First try to get from environment variable
        String envValue = getEnvironmentVariable(key);
        if (envValue != null && !envValue.isEmpty()) {
            return envValue;
        }
        
        // Fall back to properties file or default
        return properties.getProperty(key, defaultValue);
    }
    
    /**
     * Get environment variable by key
     * Converts property key format to environment variable format
     * Example: "trello.email" -> "TRELLO_EMAIL"
     * 
     * @param key Property key
     * @return Environment variable value or null
     */
    private String getEnvironmentVariable(String key) {
        // Convert property key to environment variable format
        // trello.email -> TRELLO_EMAIL
        String envKey = key.replace(".", "_").toUpperCase();
        return System.getenv(envKey);
    }
    
    /**
     * Check if credentials are configured
     * 
     * @return true if both email and password are available
     */
    public boolean hasCredentials() {
        String email = getProperty("trello.email");
        String password = getProperty("trello.password");
        
        return email != null && !email.isEmpty() && 
               !email.contains("your_") && // Check if placeholder
               password != null && !password.isEmpty() &&
               !password.contains("your_"); // Check if placeholder
    }
}
