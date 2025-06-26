package com.bookstore.clients;

import com.bookstore.config.ConfigManager;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import lombok.extern.slf4j.Slf4j;

/**
 * Base API client providing common configuration and specifications
 * for all API clients in the framework
 * 
 * @author API Test Framework
 * @version 1.0
 */
@Slf4j
public abstract class BaseApiClient {
    
    protected final ConfigManager config;
    protected final RequestSpecification requestSpec;
    protected final ResponseSpecification responseSpec;
    
    protected BaseApiClient() {
        this.config = ConfigManager.getInstance();
        this.requestSpec = createRequestSpecification();
        this.responseSpec = createResponseSpecification();
        
        log.info("Initialized {} API client", this.getClass().getSimpleName());
    }
    
    /**
     * Creates the default request specification
     * 
     * @return Configured RequestSpecification
     */    private RequestSpecification createRequestSpecification() {
        RequestSpecBuilder builder = new RequestSpecBuilder()
                .setBaseUri(config.getBaseUrl())
                .setContentType(ContentType.JSON)
                .addHeader("Accept", "application/json")
                .addHeader("User-Agent", "Bookstore-API-Tests/1.0");
        
        if (config.isEnableLogging()) {
            builder.log(LogDetail.ALL);
        }
        
        return builder.build();
    }
    
    /**
     * Creates the default response specification
     * 
     * @return Configured ResponseSpecification
     */
    private ResponseSpecification createResponseSpecification() {
        ResponseSpecBuilder builder = new ResponseSpecBuilder();
        
        if (config.isEnableLogging()) {
            builder.log(LogDetail.ALL);
        }
        
        return builder.build();
    }
    
    /**
     * Gets the request specification
     * 
     * @return RequestSpecification
     */
    public RequestSpecification getRequestSpec() {
        return RestAssured.given().spec(requestSpec);
    }
    
    /**
     * Gets the response specification
     * 
     * @return ResponseSpecification
     */
    public ResponseSpecification getResponseSpec() {
        return responseSpec;
    }
    
    /**
     * Creates a request specification with authentication
     * This can be extended for APIs that require authentication
     * 
     * @param token Authentication token
     * @return Authenticated RequestSpecification
     */
    protected RequestSpecification getAuthenticatedRequestSpec(String token) {
        return getRequestSpec().header("Authorization", "Bearer " + token);
    }
      /**
     * Validates that the API is reachable
     * 
     * @return true if API is reachable
     */
    public boolean isApiReachable() {
        try {
            getRequestSpec()
                    .when()
                    .get(config.getApiBaseUrl() + "/Books")
                    .then()
                    .statusCode(200);
            return true;
        } catch (Exception e) {
            log.error("API is not reachable: {}", e.getMessage());
            return false;
        }
    }
}
