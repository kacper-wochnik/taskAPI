package com.bookstore.tests.authors;

import com.bookstore.base.BaseTest;
import com.bookstore.models.Author;
import com.bookstore.utils.ResponseValidator;
import com.bookstore.utils.ExtentManager;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for Authors API GET operations
 * Covers happy path and edge cases for retrieving authors
 * 
 * @author API Test Framework
 * @version 1.0
 */
public class AuthorsGetTests extends BaseTest {
      @Test(description = "Verify getting all authors returns successful response")
    public void testGetAllAuthors_Success() {
        // Given
        ExtentManager.assignCategory("Authors API");
        ExtentManager.logInfo("Starting test to retrieve all authors");
        addTestStep("Send GET request", "Retrieving all authors from the API");
        
        // When
        Response response = authorsApiClient.getAllAuthors();
        
        // Then
        addTestStep("Validate response", "Checking response status and structure");
        ResponseValidator.validateStatusCode(response, 200);
        ResponseValidator.validateJsonContentType(response);
        ResponseValidator.validateJsonArray(response);
        ResponseValidator.validateResponseTime(response, 5000);
        
        List<Author> authors = response.jsonPath().getList("", Author.class);
        ExtentManager.logInfo("Authors Count: " + authors.size());
        
        if (!authors.isEmpty()) {
            Author firstAuthor = authors.get(0);
            assertThat(firstAuthor.getId()).isNotNull();
            assertThat(firstAuthor.getFirstName()).isNotNull();
            assertThat(firstAuthor.getLastName()).isNotNull();
            logTestInfo("Successfully retrieved " + authors.size() + " authors");
            ExtentManager.logPass("All authors retrieved successfully");
        }
    }
    
    @Test(description = "Verify getting a specific author by valid ID returns correct author")
    public void testGetAuthorById_ValidId_Success() {
        // Given
        int authorId = 1;
        
        // When
        Response response = authorsApiClient.getAuthorById(authorId);
        
        // Then
        ResponseValidator.validateAuthorResponse(response);
        ResponseValidator.validateResponseTime(response, 3000);
        
        Author author = response.as(Author.class);
        assertThat(author.getId()).isEqualTo(authorId);
        assertThat(author.getFirstName()).isNotNull().isNotEmpty();
        assertThat(author.getLastName()).isNotNull().isNotEmpty();
        assertThat(author.getIdBook()).isNotNull();
        
        logTestInfo("Successfully retrieved author: " + author.getFullName());
    }
    
    @Test(description = "Verify getting author with non-existent ID returns 404")
    public void testGetAuthorById_NonExistentId_Returns404() {
        // Given
        int nonExistentId = 999999;
        
        // When
        Response response = authorsApiClient.getAuthorById(nonExistentId);
        
        // Then
        ResponseValidator.validateStatusCode(response, 404);
        ResponseValidator.validateResponseTime(response, 3000);
        
        logTestInfo("Correctly received 404 for non-existent author ID: " + nonExistentId);
    }
    
    @Test(description = "Verify getting authors by book ID returns associated authors")
    public void testGetAuthorsByBookId_ValidBookId_Success() {
        // Given
        int bookId = 1;
        
        // When
        Response response = authorsApiClient.getAuthorsByBookId(bookId);
        
        // Then
        ResponseValidator.validateStatusCode(response, 200);
        ResponseValidator.validateJsonContentType(response);
        ResponseValidator.validateJsonArray(response);
        ResponseValidator.validateResponseTime(response, 3000);
        
        List<Author> authors = response.jsonPath().getList("", Author.class);
        
        // Verify that all returned authors are associated with the requested book
        for (Author author : authors) {
            assertThat(author.getIdBook())
                    .as("Author should be associated with requested book ID")
                    .isEqualTo(bookId);
        }
        
        logTestInfo("Successfully retrieved " + authors.size() + " authors for book ID: " + bookId);
    }
    
    @Test(description = "Verify getting authors by non-existent book ID returns empty or 404")
    public void testGetAuthorsByBookId_NonExistentBookId_AppropriateBehavior() {
        // Given
        int nonExistentBookId = 999999;
        
        // When
        Response response = authorsApiClient.getAuthorsByBookId(nonExistentBookId);
        
        // Then
        // API might return 200 with empty array, or 404
        assertThat(response.getStatusCode())
                .as("Should return appropriate status for non-existent book")
                .isIn(200, 404);
        
        ResponseValidator.validateResponseTime(response, 3000);
        
        if (response.getStatusCode() == 200) {
            // If 200, should be empty array
            List<Author> authors = response.jsonPath().getList("", Author.class);
        }
        
        logTestInfo("Received status " + response.getStatusCode() + " for non-existent book ID");
    }
    
    @Test(description = "Verify getting author with invalid ID format returns appropriate error")
    public void testGetAuthorById_InvalidIdFormat_ReturnsError() {
        // Given
        String invalidId = "abc";
        
        // When
        Response response = authorsApiClient.getRequestSpec()
                .pathParam("id", invalidId)
                .when()
                .get("/api/v1/Authors/{id}");
        
        // Then
        ResponseValidator.validateBadRequest(response);
        ResponseValidator.validateResponseTime(response, 3000);
        
        logTestInfo("Correctly received error for invalid author ID format: " + invalidId);
    }
    
    @Test(description = "Verify response structure and data types for all authors")
    public void testGetAllAuthors_ValidateResponseStructure() {
        // Given
        
        // When
        Response response = authorsApiClient.getAllAuthors();
        
        // Then
        ResponseValidator.validateStatusCode(response, 200);
        ResponseValidator.validateJsonArray(response);
        
        List<Author> authors = response.jsonPath().getList("", Author.class);
        
        if (!authors.isEmpty()) {
            Author sampleAuthor = authors.get(0);
            
            // Validate data types and constraints
            assertThat(sampleAuthor.getId())
                    .as("Author ID should be a positive integer")
                    .isNotNull()
                    .isGreaterThan(0);
            
            assertThat(sampleAuthor.getIdBook())
                    .as("Book ID should be a positive integer")
                    .isNotNull()
                    .isGreaterThan(0);
            
            assertThat(sampleAuthor.getFirstName())
                    .as("First name should not be null")
                    .isNotNull();
            
            assertThat(sampleAuthor.getLastName())
                    .as("Last name should not be null")
                    .isNotNull();
            
            logTestInfo("Response structure validation passed for " + authors.size() + " authors");        }
    }
    
    @Test(description = "Verify getting authors by negative book ID")
    public void testGetAuthorsByBookId_NegativeBookId_AppropriateBehavior() {
        // Given
        int negativeBookId = -1;
        
        // When
        Response response = authorsApiClient.getAuthorsByBookId(negativeBookId);
        
        // Then
        // API might return 400, 404, or even 200 with empty results
        assertThat(response.getStatusCode())
                .as("Should return appropriate status for negative book ID")
                .isIn(200, 400, 404);
        
        ResponseValidator.validateResponseTime(response, 3000);
        
        logTestInfo("Received status " + response.getStatusCode() + " for negative book ID");
    }
}
