package com.bookstore.utils;

import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * TestNG Listener for ExtentReports integration
 * Automatically marks tests as pass/fail/skip in ExtentReports based on TestNG results
 */
public class ExtentTestListener implements ITestListener {

    @Override
    public void onTestStart(ITestResult result) {
        // Create test if it doesn't exist (fallback mechanism)
        if (ExtentManager.getTest() == null) {
            String testName = result.getMethod().getMethodName();
            String testDescription = "API Test: " + testName.replaceAll("([A-Z])", " $1").trim();
            ExtentManager.createTest(testName, testDescription);
            
            // Add basic metadata
            ExtentManager.assignCategory(result.getTestClass().getName().replaceAll(".*\\.", ""));
            ExtentManager.assignAuthor("API Test Framework");
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        if (ExtentManager.getTest() != null) {
            ExtentManager.getTest().pass("Test passed successfully");
        }
        // Clear test from ThreadLocal after completion
        ExtentManager.clearTest();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        if (ExtentManager.getTest() != null) {
            String errorMessage = result.getThrowable() != null ? 
                result.getThrowable().getMessage() : "Test failed with unknown error";
            
            ExtentManager.getTest().fail("Test failed: " + errorMessage);
            
            // Log stack trace if available
            if (result.getThrowable() != null) {
                ExtentManager.getTest().fail(
                    "<details><summary>Stack Trace</summary><pre>" + 
                    getStackTrace(result.getThrowable()) + "</pre></details>");
            }
        }
        // Clear test from ThreadLocal after completion
        ExtentManager.clearTest();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        if (ExtentManager.getTest() != null) {
            String skipReason = result.getThrowable() != null ? 
                result.getThrowable().getMessage() : "Test was skipped";
            
            ExtentManager.getTest().skip("Test skipped: " + skipReason);
        }
        // Clear test from ThreadLocal after completion
        ExtentManager.clearTest();
    }

    /**
     * Convert throwable stack trace to string
     */
    private String getStackTrace(Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        sb.append(throwable.toString()).append("\n");
        
        for (StackTraceElement element : throwable.getStackTrace()) {
            sb.append("\tat ").append(element.toString()).append("\n");
        }
        
        return sb.toString();
    }
}
