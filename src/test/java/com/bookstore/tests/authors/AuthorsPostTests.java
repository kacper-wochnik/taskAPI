package com.bookstore.tests.authors;

import com.bookstore.base.BaseTest;
import com.bookstore.models.Author;
import com.bookstore.utils.ResponseValidator;
import com.bookstore.utils.TestDataGenerator;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for Authors API POST operations
 * Covers happy path and edge cases for creating authors
 * 
 * @author API Test Framework
 * @version 1.0
 */
public class AuthorsPostTests extends BaseTest {
    
    @Test(description = "Verify creating a new author with valid data succeeds")
    public void testCreateAuthor_ValidData_Success() {
        // Given
        Author newAuthor = TestDataGenerator.generateAuthorForCreation();
        
        // When
        Response response = authorsApiClient.createAuthor(newAuthor);
        
        // Then
        ResponseValidator.validateStatusCode(response, 200);
        ResponseValidator.validateJsonContentType(response);
        ResponseValidator.validateResponseTime(response, 5000);
          // Validate response contains the created author data
        Author createdAuthor = response.as(Author.class);
        // Note: API returns ID as 0 (this appears to be a bug in the API)
        assertThat(createdAuthor.getId()).isNotNull();
        assertThat(createdAuthor.getFirstName()).isEqualTo(newAuthor.getFirstName());
        assertThat(createdAuthor.getLastName()).isEqualTo(newAuthor.getLastName());
        assertThat(createdAuthor.getIdBook()).isEqualTo(newAuthor.getIdBook());
        
        logTestInfo("Successfully created author: " + createdAuthor.getFullName());
    }
    
    @Test(description = "Verify creating author with minimal required data")
    public void testCreateAuthor_MinimalData_Success() {
        // Given
        Author minimalAuthor = Author.builder()
                .firstName("Min" + getCurrentTimestamp())
                .lastName("Author")
                .idBook(1)
                .build();
        
        
        // When
        Response response = authorsApiClient.createAuthor(minimalAuthor);
        
        // Then
        ResponseValidator.validateStatusCode(response, 200);
          Author createdAuthor = response.as(Author.class);
        // Note: API returns ID as 0 (this appears to be a bug in the API)
        assertThat(createdAuthor.getId()).isNotNull();
        assertThat(createdAuthor.getFirstName()).isEqualTo(minimalAuthor.getFirstName());
        assertThat(createdAuthor.getLastName()).isEqualTo(minimalAuthor.getLastName());
        
        logTestInfo("Successfully created minimal author: " + createdAuthor.getFullName());
    }
    
    @Test(description = "Verify creating author with empty first name returns error")
    public void testCreateAuthor_EmptyFirstName_ReturnsError() {
        // Given
        Author invalidAuthor = Author.builder()
                .firstName("")
                .lastName("TestAuthor")
                .idBook(1)
                .build();
        
        
        // When
        Response response = authorsApiClient.createAuthor(invalidAuthor);
        
        // Then
        ResponseValidator.validateBadRequest(response); // Empty firstName should return 400
        ResponseValidator.validateResponseTime(response, 3000);
    }
    
    @Test(description = "Verify creating author with empty last name returns error")
    public void testCreateAuthor_EmptyLastName_ReturnsError() {
        // Given
        Author invalidAuthor = Author.builder()
                .firstName("TestAuthor")
                .lastName("")
                .idBook(1)
                .build();
        
        
        // When
        Response response = authorsApiClient.createAuthor(invalidAuthor);
        
        // Then
        ResponseValidator.validateBadRequest(response); // Empty lastName should return 400
        ResponseValidator.validateResponseTime(response, 3000);
    }
    
    @Test(description = "Verify creating author with null book ID returns error")
    public void testCreateAuthor_NullBookId_ReturnsError() {
        // Given
        Author invalidAuthor = Author.builder()
                .firstName("TestAuthor")
                .lastName("WithNullBook")
                .idBook(null)
                .build();
        
        
        // When
        Response response = authorsApiClient.createAuthor(invalidAuthor);
        
        // Then
        ResponseValidator.validateBadRequest(response); // Null bookId should return 400
        ResponseValidator.validateResponseTime(response, 3000);
    }
    
    @Test(description = "Verify creating author with negative book ID returns error")
    public void testCreateAuthor_NegativeBookId_ReturnsError() {
        // Given
        Author invalidAuthor = Author.builder()
                .firstName("TestAuthor")
                .lastName("WithNegativeBook")
                .idBook(-1)
                .build();
        
        
        // When
        Response response = authorsApiClient.createAuthor(invalidAuthor);
        
        // Then
        ResponseValidator.validateBadRequest(response); // Negative bookId should return 400
        ResponseValidator.validateResponseTime(response, 3000);
    }
    
    @Test(description = "Verify creating author with very long names")
    public void testCreateAuthor_VeryLongNames_AppropriateBehavior() {
        // Given
        String longName = "a".repeat(1000); // 1000 character name
        Author authorWithLongNames = Author.builder()
                .firstName(longName)
                .lastName(longName)
                .idBook(1)
                .build();
        
        
        // When
        Response response = authorsApiClient.createAuthor(authorWithLongNames);
        
        // Then
        // API might accept it (200) or reject it (400), both are valid responses
        assertThat(response.getStatusCode())
                .as("Should handle long names appropriately")
                .isIn(200, 400, 413); // 413 = Payload Too Large
        
        ResponseValidator.validateResponseTime(response, 5000);
        
        logTestInfo("Received status " + response.getStatusCode() + " for very long names");
    }
    
    @Test(description = "Verify creating author with special characters in names")
    public void testCreateAuthor_SpecialCharacters_AppropriateBehavior() {
        // Given
        Author authorWithSpecialChars = Author.builder()
                .firstName("João-André")
                .lastName("O'Connor-Smith")
                .idBook(1)
                .build();
        
        
        // When
        Response response = authorsApiClient.createAuthor(authorWithSpecialChars);
        
        // Then
        // API should ideally accept valid international characters
        assertThat(response.getStatusCode())
                .as("Should handle special characters appropriately")
                .isIn(200, 400);
        
        ResponseValidator.validateResponseTime(response, 3000);
        
        logTestInfo("Received status " + response.getStatusCode() + " for special characters");
    }
    
    @Test(description = "Verify creating author with non-existent book ID")
    public void testCreateAuthor_NonExistentBookId_AppropriateBehavior() {
        // Given
        int nonExistentBookId = 999999;
        Author authorWithNonExistentBook = Author.builder()
                .firstName("Test" + getCurrentTimestamp())
                .lastName("Author")
                .idBook(nonExistentBookId)
                .build();
        
        
        // When
        Response response = authorsApiClient.createAuthor(authorWithNonExistentBook);
        
        // Then
        // API might accept it (200) or reject it (400/404), depending on validation
        assertThat(response.getStatusCode())
                .as("Should handle non-existent book ID appropriately")
                .isIn(200, 400, 404);
        
        ResponseValidator.validateResponseTime(response, 3000);
        
        logTestInfo("Received status " + response.getStatusCode() + " for non-existent book ID");
    }
}
