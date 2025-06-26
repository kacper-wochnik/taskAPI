package com.bookstore.base;

import com.bookstore.clients.AuthorsApiClient;
import com.bookstore.clients.BooksApiClient;
import com.bookstore.config.ConfigManager;
import com.bookstore.utils.ExtentManager;
import com.aventstack.extentreports.ExtentReports;
import lombok.extern.slf4j.Slf4j;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.lang.reflect.Method;

/**
 * Base test class providing common setup and teardown for all test classes
 * Contains shared test infrastructure and utility methods
 * 
 * @author API Test Framework
 * @version 1.0
 */
@Slf4j
public abstract class BaseTest {
    
    protected ConfigManager config;
    protected BooksApiClient booksApiClient;
    protected AuthorsApiClient authorsApiClient;
    protected ExtentReports extent;
    
    @BeforeSuite(description = "Suite setup - Initialize configuration and clients")
    public void suiteSetup() {
        log.info("=== Starting API Test Suite ===");
        
        // Initialize ExtentReports
        extent = ExtentManager.createInstance();
        
        // Initialize configuration
        config = ConfigManager.getInstance();
        log.info("Configuration loaded for environment: {}", config.getEnvironment());
        
        // Initialize API clients
        booksApiClient = new BooksApiClient();
        authorsApiClient = new AuthorsApiClient();
        
        // Verify API connectivity
        verifyApiConnectivity();
        
        log.info("=== Suite Setup Complete ===");
    }
    
    @AfterSuite(description = "Suite cleanup - Generate final report")
    public void suiteTeardown() {
        log.info("=== Finalizing Test Suite ===");
        ExtentManager.flush();
        log.info("ExtentReports generated at: {}", ExtentManager.getReportPath());
        log.info("=== Test Suite Complete ===");
    }
      
    @BeforeMethod(description = "Test setup - Log test start and add metadata")
    public void testSetup(Method method) {
        // Ensure configuration is initialized (thread-safe fallback)
        if (config == null) {
            synchronized (BaseTest.class) {
                if (config == null) {
                    log.warn("Config was null, initializing in testSetup method");
                    config = ConfigManager.getInstance();
                    booksApiClient = new BooksApiClient();
                    authorsApiClient = new AuthorsApiClient();
                }
            }
        }
        
        String testName = method.getName();
        String className = method.getDeclaringClass().getSimpleName();
        
        log.info("Starting test: {}.{}", className, testName);
          // Create ExtentTest
        String testDescription = getTestDescription(method);
        ExtentManager.createTest(testName, testDescription);
        
        // Add metadata
        ExtentManager.assignCategory(className);
        ExtentManager.assignAuthor("API Test Framework");
        
        // Log environment information
        ExtentManager.logInfo("Environment: " + config.getEnvironment());
        ExtentManager.logInfo("API Base URL: " + config.getApiBaseUrl());
        ExtentManager.logInfo("Test Started at: " + java.time.LocalDateTime.now());
    }
    
    @AfterMethod(description = "Test cleanup - Log test completion")
    public void testTeardown(Method method, ITestResult result) {
        String testName = method.getName();
        String className = method.getDeclaringClass().getSimpleName();
        
        // Note: Test status is handled by ExtentTestListener to avoid duplication
        // Only log completion message here
        
        log.info("Completed test: {}.{}", className, testName);
    }
    
    /**
     * Verifies that the API is reachable before running tests
     */
    private void verifyApiConnectivity() {
        log.info("Verifying API connectivity...");
        
        try {
            boolean booksApiReachable = booksApiClient.isApiReachable();
            boolean authorsApiReachable = authorsApiClient.isApiReachable();
            
            if (!booksApiReachable || !authorsApiReachable) {
                String message = "API connectivity check failed. " +
                        "Books API: " + (booksApiReachable ? "OK" : "FAILED") + ", " +
                        "Authors API: " + (authorsApiReachable ? "OK" : "FAILED");
                log.error(message);
                throw new RuntimeException(message);
            }
            
            log.info("API connectivity verified successfully");
        } catch (Exception e) {
            log.warn("API connectivity check failed, but continuing with tests: {}", e.getMessage());
            // Note: We don't fail here as the API might be temporarily unavailable
            // but we still want to run the tests to see the actual failures
        }
    }
    /**
     * Get test description from method annotation or generate default
     */
    private String getTestDescription(Method method) {
        // You can extend this to read from annotations like @Test(description="...")
        return "API Test: " + method.getName().replaceAll("([A-Z])", " $1").trim();
    }
        
    /**
     * Adds a step to ExtentReports
     * 
     * @param stepName Step name
     * @param stepDescription Step description
     */
    protected void addTestStep(String stepName, String stepDescription) {
        ExtentManager.logInfo(stepName + " - " + stepDescription);
        log.info("Step: {} - {}", stepName, stepDescription);
    }
    
    /**
     * Adds an info log to ExtentReports
     * 
     * @param name Log name
     * @param content Log content
     */
    protected void addTestInfo(String name, String content) {
        ExtentManager.logInfo(name + ": " + content);
    }
    
    /**
     * Logs and adds test information to ExtentReports
     * 
     * @param message Information message
     */
    protected void logTestInfo(String message) {
        log.info(message);
        ExtentManager.logInfo(message);
    }
    
    /**
     * Logs and adds warning to ExtentReports
     * 
     * @param message Warning message
     */
    protected void logTestWarning(String message) {
        log.warn(message);
        ExtentManager.logWarning(message);
    }
    
    /**
     * Logs error and adds to ExtentReports
     * 
     * @param message Error message
     * @param throwable Exception details
     */
    protected void logTestError(String message, Throwable throwable) {
        log.error(message, throwable);
        ExtentManager.logFail(message + "\n" + throwable.getMessage());
    }
    
    /**
     * Gets current timestamp for unique test data
     * 
     * @return Current timestamp as string
     */
    protected String getCurrentTimestamp() {
        return String.valueOf(System.currentTimeMillis());
    }
    
    /**
     * Waits for specified amount of time
     * Useful for tests that need to wait for eventual consistency
     * 
     * @param milliseconds Time to wait in milliseconds
     */
    protected void waitFor(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Wait interrupted: {}", e.getMessage());
        }
    }
}
