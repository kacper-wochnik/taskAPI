package com.bookstore.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ExtentReports Manager for handling test reporting
 * Provides centralized reporting functionality for all test classes
 */
public class ExtentManager {
    
    private static ExtentReports extent;
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();
    
    private static final String REPORT_PATH = "test-output/ExtentReports/";
    private static final String SCREENSHOT_PATH = "test-output/screenshots/";
    
    /**
     * Initialize ExtentReports instance
     */
    public static synchronized ExtentReports createInstance() {
        if (extent == null) {
            // Create directories if they don't exist
            new File(REPORT_PATH).mkdirs();
            new File(SCREENSHOT_PATH).mkdirs();
            
            String fileName = REPORT_PATH + "BookstoreAPI_TestReport_" + 
                            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")) + ".html";
            
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(fileName);
            sparkReporter.config().setTheme(Theme.STANDARD);
            sparkReporter.config().setDocumentTitle("Bookstore API Test Report");
            sparkReporter.config().setReportName("Bookstore API Automation Test Results");
            sparkReporter.config().setTimeStampFormat("MMM dd, yyyy HH:mm:ss");
            
            extent = new ExtentReports();
            extent.attachReporter(sparkReporter);
            
            // System information
            extent.setSystemInfo("Framework", "RestAssured + TestNG + ExtentReports");
            extent.setSystemInfo("Author", "API Test Automation Team");
            extent.setSystemInfo("API Under Test", "FakeRestAPI Bookstore");
            extent.setSystemInfo("Environment", System.getProperty("env", "dev"));
            extent.setSystemInfo("Java Version", System.getProperty("java.version"));
            extent.setSystemInfo("OS", System.getProperty("os.name"));
        }
        return extent;
    }
    
    /**
     * Create a new test instance
     */
    public static synchronized ExtentTest createTest(String testName, String description) {
        ExtentTest extentTest = extent.createTest(testName, description);
        test.set(extentTest);
        return extentTest;
    }
    
    /**
     * Get current test instance
     */
    public static synchronized ExtentTest getTest() {
        return test.get();
    }
    
    /**
     * Log info message
     */
    public static void logInfo(String message) {
        if (getTest() != null) {
            getTest().log(Status.INFO, message);
        }
    }
    
    /**
     * Log pass message
     */
    public static void logPass(String message) {
        if (getTest() != null) {
            getTest().log(Status.PASS, message);
        }
    }
    
    /**
     * Log fail message
     */
    public static void logFail(String message) {
        if (getTest() != null) {
            getTest().log(Status.FAIL, message);
        }
    }
    
    /**
     * Log warning message
     */
    public static void logWarning(String message) {
        if (getTest() != null) {
            getTest().log(Status.WARNING, message);
        }
    }
    
    /**
     * Log skip message
     */
    public static void logSkip(String message) {
        if (getTest() != null) {
            getTest().log(Status.SKIP, message);
        }
    }
    
    /**
     * Add category to test
     */
    public static void assignCategory(String category) {
        if (getTest() != null) {
            getTest().assignCategory(category);
        }
    }
    
    /**
     * Add author to test
     */
    public static void assignAuthor(String author) {
        if (getTest() != null) {
            getTest().assignAuthor(author);
        }
    }
    
    /**
     * Flush the reports
     */
    public static synchronized void flush() {
        if (extent != null) {
            extent.flush();
        }
    }
    
    /**
     * Get report file path
     */
    public static String getReportPath() {
        return new File(REPORT_PATH).getAbsolutePath();
    }
    
    /**
     * Clear current test from ThreadLocal (cleanup)
     */
    public static void clearTest() {
        test.remove();
    }
}
