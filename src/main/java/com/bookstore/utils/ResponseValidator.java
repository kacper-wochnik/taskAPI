package com.bookstore.utils;

import io.restassured.response.Response;
import lombok.experimental.UtilityClass;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Response validation utility class
 * Provides common validation methods for API responses
 * 
 * @author API Test Framework
 * @version 1.0
 */
@UtilityClass
public class ResponseValidator {
    
    /**
     * Validates that response has expected status code
     * 
     * @param response API response
     * @param expectedStatusCode Expected HTTP status code
     */
    public static void validateStatusCode(Response response, int expectedStatusCode) {
        assertThat(response.getStatusCode())
                .as("Response status code")
                .isEqualTo(expectedStatusCode);
    }
    
    /**
     * Validates that response time is within acceptable limits
     * 
     * @param response API response
     * @param maxResponseTimeMs Maximum acceptable response time in milliseconds
     */
    public static void validateResponseTime(Response response, long maxResponseTimeMs) {
        assertThat(response.getTime())
                .as("Response time should be within acceptable limits")
                .isLessThanOrEqualTo(maxResponseTimeMs);
    }
    
    /**
     * Validates that response contains specific header
     * 
     * @param response API response
     * @param headerName Header name to check
     * @param expectedValue Expected header value
     */
    public static void validateHeader(Response response, String headerName, String expectedValue) {
        assertThat(response.getHeader(headerName))
                .as("Header '%s' value", headerName)
                .isEqualTo(expectedValue);
    }
    
    /**
     * Validates that response has Content-Type header with JSON
     * 
     * @param response API response
     */
    public static void validateJsonContentType(Response response) {
        String contentType = response.getHeader("Content-Type");
        assertThat(contentType)
                .as("Content-Type header should indicate JSON")
                .containsIgnoringCase("application/json");
    }
    
    /**
     * Validates that response body is not empty
     * 
     * @param response API response
     */
    public static void validateResponseBodyNotEmpty(Response response) {
        assertThat(response.getBody().asString())
                .as("Response body should not be empty")
                .isNotEmpty();
    }
    
    /**
     * Validates that response body is empty
     * 
     * @param response API response
     */
    public static void validateResponseBodyEmpty(Response response) {
        assertThat(response.getBody().asString())
                .as("Response body should be empty")
                .isEmpty();
    }
      /**
     * Validates that response contains specific JSON field
     * 
     * @param response API response
     * @param jsonPath JSON path to the field
     */
    public static void validateJsonFieldExists(Response response, String jsonPath) {
        Object fieldValue = response.jsonPath().get(jsonPath);
        assertThat(fieldValue)
                .as("JSON field '%s' should exist", jsonPath)
                .isNotNull();
    }
      /**
     * Validates that response JSON field has expected value
     * 
     * @param response API response
     * @param jsonPath JSON path to the field
     * @param expectedValue Expected field value
     */
    public static void validateJsonFieldValue(Response response, String jsonPath, Object expectedValue) {
        Object fieldValue = response.jsonPath().get(jsonPath);
        assertThat(fieldValue)
                .as("JSON field '%s' value", jsonPath)
                .isEqualTo(expectedValue);
    }
    
    /**
     * Validates that response is a JSON array
     * 
     * @param response API response
     */
    public static void validateJsonArray(Response response) {
        assertThat(response.getBody().asString())
                .as("Response should be a JSON array")
                .startsWith("[")
                .endsWith("]");
    }
    
    /**
     * Validates that JSON array has expected size
     * 
     * @param response API response
     * @param expectedSize Expected array size
     */
    public static void validateJsonArraySize(Response response, int expectedSize) {
        assertThat(response.jsonPath().getList("").size())
                .as("JSON array size")
                .isEqualTo(expectedSize);
    }
    
    /**
     * Validates that JSON array is not empty
     * 
     * @param response API response
     */
    public static void validateJsonArrayNotEmpty(Response response) {
        assertThat(response.jsonPath().getList("").size())
                .as("JSON array should not be empty")
                .isGreaterThan(0);
    }
    
    /**
     * Validates that response is a successful HTTP response (2xx)
     * 
     * @param response API response
     */
    public static void validateSuccessfulResponse(Response response) {
        assertThat(response.getStatusCode())
                .as("Response should be successful (2xx)")
                .isBetween(200, 299);
    }
    
    /**
     * Validates that response is a client error (4xx)
     * 
     * @param response API response
     */
    public static void validateClientError(Response response) {
        assertThat(response.getStatusCode())
                .as("Response should be a client error (4xx)")
                .isBetween(400, 499);
    }
    
    /**
     * Validates that response returns 400 Bad Request
     * 
     * @param response API response
     */
    public static void validateBadRequest(Response response) {
        assertThat(response.getStatusCode())
                .as("Response should be 400 Bad Request for invalid input data")
                .isEqualTo(400);
    }
    
    /**
     * Validates that response returns 404 Not Found
     * 
     * @param response API response
     */
    public static void validateNotFound(Response response) {
        assertThat(response.getStatusCode())
                .as("Response should be 404 Not Found for non-existent resource")
                .isEqualTo(404);
    }
    
    /**
     * Validates that response returns 422 Unprocessable Entity
     * 
     * @param response API response
     */
    public static void validateUnprocessableEntity(Response response) {
        assertThat(response.getStatusCode())
                .as("Response should be 422 Unprocessable Entity for logically invalid data")
                .isEqualTo(422);
    }
    
    /**
     * Validates basic response structure for Books API
     * 
     * @param response API response
     */
    public static void validateBookResponse(Response response) {
        validateSuccessfulResponse(response);
        validateJsonContentType(response);
        validateJsonFieldExists(response, "id");
        validateJsonFieldExists(response, "title");
        validateJsonFieldExists(response, "description");
        validateJsonFieldExists(response, "pageCount");
        validateJsonFieldExists(response, "excerpt");
        validateJsonFieldExists(response, "publishDate");
    }
    
    /**
     * Validates basic response structure for Authors API
     * 
     * @param response API response
     */
    public static void validateAuthorResponse(Response response) {
        validateSuccessfulResponse(response);
        validateJsonContentType(response);
        validateJsonFieldExists(response, "id");
        validateJsonFieldExists(response, "idBook");
        validateJsonFieldExists(response, "firstName");
        validateJsonFieldExists(response, "lastName");
    }
}
