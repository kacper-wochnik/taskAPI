package com.bookstore.tests.authors;

import com.bookstore.base.BaseTest;
import com.bookstore.models.Author;
import com.bookstore.utils.ResponseValidator;
import com.bookstore.utils.TestDataGenerator;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for Authors API PUT operations
 * Covers happy path and edge cases for updating authors
 * 
 * @author API Test Framework
 * @version 1.0
 */
public class AuthorsPutTests extends BaseTest {
    
    @Test(description = "Verify updating an existing author with valid data succeeds")
    public void testUpdateAuthor_ValidData_Success() {
        // Given
        int authorId = 1;
        Author updatedAuthor = Author.builder()
                .id(authorId)
                .firstName("Updated" + getCurrentTimestamp())
                .lastName("Author")
                .idBook(1)
                .build();
        
        
        // When
        Response response = authorsApiClient.updateAuthor(authorId, updatedAuthor);
        
        // Then
        ResponseValidator.validateStatusCode(response, 200);
        ResponseValidator.validateJsonContentType(response);
        ResponseValidator.validateResponseTime(response, 5000);
        
        Author responseAuthor = response.as(Author.class);
        assertThat(responseAuthor.getId()).isEqualTo(authorId);
        assertThat(responseAuthor.getFirstName()).isEqualTo(updatedAuthor.getFirstName());
        assertThat(responseAuthor.getLastName()).isEqualTo(updatedAuthor.getLastName());
        assertThat(responseAuthor.getIdBook()).isEqualTo(updatedAuthor.getIdBook());
        
        logTestInfo("Successfully updated author: " + responseAuthor.getFullName());
    }
    
    @Test(description = "Verify updating author with partial data")
    public void testUpdateAuthor_PartialData_Success() {
        // Given
        int authorId = 2;
        Author partialUpdate = Author.builder()
                .id(authorId)
                .firstName("PartialUpdate" + getCurrentTimestamp())
                .lastName("TestAuthor")
                .idBook(1)
                .build();
        
        
        // When
        Response response = authorsApiClient.updateAuthor(authorId, partialUpdate);
        
        // Then
        ResponseValidator.validateStatusCode(response, 200);
        
        Author responseAuthor = response.as(Author.class);
        assertThat(responseAuthor.getId()).isEqualTo(authorId);
        assertThat(responseAuthor.getFirstName()).isEqualTo(partialUpdate.getFirstName());
        
        logTestInfo("Successfully performed partial update for author: " + responseAuthor.getFullName());
    }
    
    @Test(description = "Verify updating non-existent author returns 404")
    public void testUpdateAuthor_NonExistentId_Returns404() {
        // Given
        int nonExistentId = 999999;
        Author updateData = Author.builder()
                .id(nonExistentId)
                .firstName("NonExistent")
                .lastName("Author")
                .idBook(1)
                .build();
        
        
        // When
        Response response = authorsApiClient.updateAuthor(nonExistentId, updateData);
        
        // Then
        ResponseValidator.validateStatusCode(response, 404);
        ResponseValidator.validateResponseTime(response, 3000);
    }
    
    @Test(description = "Verify updating author with invalid ID format returns error")
    public void testUpdateAuthor_InvalidIdFormat_ReturnsError() {
        // Given
        String invalidId = "abc";
        Author updateData = TestDataGenerator.generateAuthorForCreation();
        
        
        // When
        Response response = authorsApiClient.getRequestSpec()
                .pathParam("id", invalidId)
                .body(updateData)
                .when()
                .put("/api/v1/Authors/{id}");
        
        // Then
        ResponseValidator.validateBadRequest(response); // Invalid ID format should return 400
        ResponseValidator.validateResponseTime(response, 3000);
        
        logTestInfo("Correctly received error for invalid author ID format: " + invalidId);
    }
    
    @Test(description = "Verify updating author with negative ID returns appropriate response")
    public void testUpdateAuthor_NegativeId_ReturnsAppropriateResponse() {
        // Given
        int negativeId = -1;
        Author updateData = TestDataGenerator.generateAuthorForCreation();
        updateData.setId(negativeId);
        
        
        // When
        Response response = authorsApiClient.updateAuthor(negativeId, updateData);
        
        // Then
        ResponseValidator.validateBadRequest(response); // Negative ID should return 400
        ResponseValidator.validateResponseTime(response, 3000);
    }
    
    @Test(description = "Verify updating author with empty first name returns error")
    public void testUpdateAuthor_EmptyFirstName_ReturnsError() {
        // Given
        int authorId = 1;
        Author invalidUpdate = Author.builder()
                .id(authorId)
                .firstName("")
                .lastName("ValidLastName")
                .idBook(1)
                .build();
        
        
        // When
        Response response = authorsApiClient.updateAuthor(authorId, invalidUpdate);
        
        // Then
        ResponseValidator.validateBadRequest(response); // Empty firstName should return 400
        ResponseValidator.validateResponseTime(response, 3000);
    }
    
    @Test(description = "Verify updating author with empty last name returns error")
    public void testUpdateAuthor_EmptyLastName_ReturnsError() {
        // Given
        int authorId = 1;
        Author invalidUpdate = Author.builder()
                .id(authorId)
                .firstName("ValidFirstName")
                .lastName("")
                .idBook(1)
                .build();
        
        
        // When
        Response response = authorsApiClient.updateAuthor(authorId, invalidUpdate);
        
        // Then
        ResponseValidator.validateBadRequest(response); // Empty lastName should return 400
        ResponseValidator.validateResponseTime(response, 3000);
    }
    
    @Test(description = "Verify updating author with null book ID returns error")
    public void testUpdateAuthor_NullBookId_ReturnsError() {
        // Given
        int authorId = 1;
        Author invalidUpdate = Author.builder()
                .id(authorId)
                .firstName("ValidFirstName")
                .lastName("ValidLastName")
                .idBook(null)
                .build();
        
        
        // When
        Response response = authorsApiClient.updateAuthor(authorId, invalidUpdate);
        
        // Then
        ResponseValidator.validateBadRequest(response); // Null bookId should return 400
        ResponseValidator.validateResponseTime(response, 3000);
    }
    
    @Test(description = "Verify updating author with non-existent book ID")
    public void testUpdateAuthor_NonExistentBookId_AppropriateBehavior() {
        // Given
        int authorId = 1;
        int nonExistentBookId = 999999;
        Author updateWithNonExistentBook = Author.builder()
                .id(authorId)
                .firstName("Test" + getCurrentTimestamp())
                .lastName("Author")
                .idBook(nonExistentBookId)
                .build();
        
        
        // When
        Response response = authorsApiClient.updateAuthor(authorId, updateWithNonExistentBook);
        
        // Then
        // API might accept it (200) or reject it (400/404), depending on validation
        assertThat(response.getStatusCode())
                .as("Should handle non-existent book ID appropriately")
                .isIn(200, 400, 404);
        
        ResponseValidator.validateResponseTime(response, 3000);
        
        logTestInfo("Received status " + response.getStatusCode() + " for non-existent book ID update");
    }
    
    @Test(description = "Verify updating author with very long names")
    public void testUpdateAuthor_VeryLongNames_AppropriateBehavior() {
        // Given
        int authorId = 1;
        String longName = "a".repeat(1000);
        Author updateWithLongNames = Author.builder()
                .id(authorId)
                .firstName(longName)
                .lastName(longName)
                .idBook(1)
                .build();
        
        
        // When
        Response response = authorsApiClient.updateAuthor(authorId, updateWithLongNames);
        
        // Then
        assertThat(response.getStatusCode())
                .as("Should handle long names appropriately")
                .isIn(200, 400, 413);
        
        ResponseValidator.validateResponseTime(response, 5000);
        
        logTestInfo("Received status " + response.getStatusCode() + " for very long names update");
    }
    
    @Test(description = "Verify ID mismatch between path and body")
    public void testUpdateAuthor_IdMismatch_AppropriateBehavior() {
        // Given
        int pathId = 1;
        int bodyId = 2;
        Author updateData = Author.builder()
                .id(bodyId)
                .firstName("Mismatch" + getCurrentTimestamp())
                .lastName("Test")
                .idBook(1)
                .build();
        
        
        // When
        Response response = authorsApiClient.updateAuthor(pathId, updateData);
        
        // Then
        // API behavior may vary: accept path ID, reject mismatch, or use body ID
        assertThat(response.getStatusCode())
                .as("Should handle ID mismatch appropriately")
                .isIn(200, 400, 409); // 409 = Conflict
        
        ResponseValidator.validateResponseTime(response, 3000);
        
        logTestInfo("Received status " + response.getStatusCode() + " for ID mismatch");
    }
}
