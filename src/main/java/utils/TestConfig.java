package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * TestConfig reads configuration from two sources, in priority order:
 * 1. Environment variables (for sensitive data like credentials)
 * 2. config.properties file (for non-sensitive configuration)
 *
 * Environment variables override config.properties values.
 * Key mapping: "trello.email" -> environment variable "TRELLO_EMAIL".
 */
public class TestConfig {

    private static final String CONFIG_FILE_PATH = "src/test/resources/config.properties";

    private final Properties properties;

    public TestConfig() {
        properties = new Properties();
        loadProperties();
    }

    private void loadProperties() {
        File configFile = new File(CONFIG_FILE_PATH);
        if (!configFile.exists()) {
            // Not an error: the setup may rely on environment variables only.
            System.out.println("config.properties not found at " + CONFIG_FILE_PATH
                    + " - using environment variables only.");
            return;
        }
        try (InputStream in = new FileInputStream(configFile)) {
            properties.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read config file: " + CONFIG_FILE_PATH, e);
        }
    }

    /**
     * Get a required property. Checks environment variables first, then config.properties.
     *
     * @param key Property key
     * @return Property value
     * @throws IllegalStateException if the key is not configured anywhere (fail fast,
     *                               instead of a cryptic NullPointerException later)
     */
    public String getProperty(String key) {
        String value = lookup(key);
        if (value == null || value.isEmpty()) {
            throw new IllegalStateException(
                    "Missing configuration for '" + key + "'. Set the environment variable "
                            + toEnvKey(key) + " or add '" + key + "' to " + CONFIG_FILE_PATH
                            + " (copy config.properties.example as a starting point).");
        }
        return value;
    }

    /**
     * Get an optional property with a default fallback.
     *
     * @param key          Property key
     * @param defaultValue Value returned when the key is not configured anywhere
     * @return Property value or the default
     */
    public String getProperty(String key, String defaultValue) {
        String value = lookup(key);
        return (value == null || value.isEmpty()) ? defaultValue : value;
    }

    /**
     * Check whether usable test credentials are configured (env vars or config file,
     * ignoring placeholder values like "your_email@example.com").
     *
     * @return true if both email and password are available
     */
    public boolean hasCredentials() {
        return isProvided("trello.email") && isProvided("trello.password");
    }

    private String lookup(String key) {
        String envValue = System.getenv(toEnvKey(key));
        if (envValue != null && !envValue.isEmpty()) {
            return envValue;
        }
        return properties.getProperty(key);
    }

    private boolean isProvided(String key) {
        String value = lookup(key);
        return value != null && !value.isEmpty() && !value.contains("your_");
    }

    static String toEnvKey(String key) {
        return key.replace('.', '_').toUpperCase();
    }
}
