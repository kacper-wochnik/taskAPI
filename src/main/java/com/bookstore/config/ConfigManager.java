package com.bookstore.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration manager for loading and managing application properties
 * Implements Singleton pattern for global access to configuration
 * 
 * @author API Test Framework
 * @version 1.0
 */
@Slf4j
@Getter
public class ConfigManager {
    
    private static ConfigManager instance;
    private final Properties properties;
    
    // API Configuration
    private final String baseUrl;
    private final String apiVersion;
    private final int requestTimeout;
    private final int connectionTimeout;
    
    // Test Configuration
    private final String environment;
    private final boolean enableLogging;
    private final String reportPath;
    
    private ConfigManager() {
        properties = new Properties();
        loadProperties();
        
        // Initialize configuration values
        this.baseUrl = getProperty("api.base.url", "https://fakerestapi.azurewebsites.net");
        this.apiVersion = getProperty("api.version", "v1");
        this.requestTimeout = Integer.parseInt(getProperty("api.request.timeout", "30000"));
        this.connectionTimeout = Integer.parseInt(getProperty("api.connection.timeout", "10000"));
          this.environment = getProperty("test.environment", "dev");
        this.enableLogging = Boolean.parseBoolean(getProperty("test.logging.enabled", "true"));
        this.reportPath = getProperty("test.report.path", "test-output/ExtentReports");
        
        log.info("Configuration loaded successfully for environment: {}", environment);
    }
    
    /**
     * Gets the singleton instance of ConfigManager
     * 
     * @return ConfigManager instance
     */
    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }
    
    /**
     * Loads properties from configuration files
     */
    private void loadProperties() {
        // Load default properties
        loadPropertiesFromFile("config.properties");
        
        // Load environment-specific properties
        String env = System.getProperty("env", "dev");
        loadPropertiesFromFile("config-" + env + ".properties");
        
        // Override with system properties
        properties.putAll(System.getProperties());
    }
    
    /**
     * Loads properties from a specific file
     * 
     * @param fileName Properties file name
     */
    private void loadPropertiesFromFile(String fileName) {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (input != null) {
                properties.load(input);
                log.debug("Loaded properties from: {}", fileName);
            } else {
                log.warn("Properties file not found: {}", fileName);
            }
        } catch (IOException e) {
            log.error("Error loading properties from {}: {}", fileName, e.getMessage());
        }
    }
    
    /**
     * Gets a property value with a default fallback
     * 
     * @param key Property key
     * @param defaultValue Default value if property is not found
     * @return Property value or default
     */
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    /**
     * Gets a property value
     * 
     * @param key Property key
     * @return Property value or null if not found
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    /**
     * Gets the complete API base URL
     * 
     * @return Full base URL with version
     */
    public String getApiBaseUrl() {
        return baseUrl + "/api/" + apiVersion;
    }
    
    /**
     * Gets the Books API endpoint URL
     * 
     * @return Books endpoint URL
     */
    public String getBooksEndpoint() {
        return getApiBaseUrl() + "/Books";
    }
    
    /**
     * Gets the Authors API endpoint URL
     * 
     * @return Authors endpoint URL
     */
    public String getAuthorsEndpoint() {
        return getApiBaseUrl() + "/Authors";
    }
    
    /**
     * Checks if we're running in debug mode
     * 
     * @return true if debug mode is enabled
     */
    public boolean isDebugMode() {
        return Boolean.parseBoolean(getProperty("debug.mode", "false"));
    }
}
