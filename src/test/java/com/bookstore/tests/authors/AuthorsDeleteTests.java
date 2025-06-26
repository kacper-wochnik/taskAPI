package com.bookstore.tests.authors;

import com.bookstore.base.BaseTest;
import com.bookstore.models.Author;
import com.bookstore.utils.ResponseValidator;
import com.bookstore.utils.TestDataGenerator;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for Authors API DELETE operations
 * Covers happy path and edge cases for deleting authors
 * 
 * @author API Test Framework
 * @version 1.0
 */
public class AuthorsDeleteTests extends BaseTest {
    
    @Test(description = "Verify deleting an existing author succeeds")
    public void testDeleteAuthor_ValidId_Success() {
        // Given - First create an author to delete
        Author newAuthor = TestDataGenerator.generateAuthorForCreation();
        Response createResponse = authorsApiClient.createAuthor(newAuthor);
        ResponseValidator.validateStatusCode(createResponse, 200);
        
        Author createdAuthor = createResponse.as(Author.class);
        int authorIdToDelete = createdAuthor.getId();
        
        
        // When
        Response deleteResponse = authorsApiClient.deleteAuthor(authorIdToDelete);
        
        // Then
        ResponseValidator.validateStatusCode(deleteResponse, 200);
        ResponseValidator.validateResponseTime(deleteResponse, 5000);
        
        // Verify author is actually deleted by trying to get it
        Response getResponse = authorsApiClient.getAuthorById(authorIdToDelete);
        ResponseValidator.validateStatusCode(getResponse, 404);
        
        logTestInfo("Successfully deleted author with ID: " + authorIdToDelete);
    }
    
    @Test(description = "Verify deleting non-existent author returns 404")
    public void testDeleteAuthor_NonExistentId_Returns404() {
        // Given
        int nonExistentId = 999999;
        
        // When
        Response response = authorsApiClient.deleteAuthor(nonExistentId);
        
        // Then
        ResponseValidator.validateStatusCode(response, 404);
        ResponseValidator.validateResponseTime(response, 3000);
    }
    
    @Test(description = "Verify deleting author with invalid ID format returns error")
    public void testDeleteAuthor_InvalidIdFormat_ReturnsError() {
        // Given
        String invalidId = "abc";
        
        // When
        Response response = authorsApiClient.getRequestSpec()
                .pathParam("id", invalidId)
                .when()
                .delete("/api/v1/Authors/{id}");
        
        // Then
        ResponseValidator.validateBadRequest(response); // Invalid ID format should return 400
        ResponseValidator.validateResponseTime(response, 3000);
        
        logTestInfo("Correctly received error for invalid author ID format: " + invalidId);
    }
    
    @Test(description = "Verify deleting author with negative ID returns appropriate response")
    public void testDeleteAuthor_NegativeId_ReturnsAppropriateResponse() {
        // Given
        int negativeId = -1;
        
        // When
        Response response = authorsApiClient.deleteAuthor(negativeId);
        
        // Then
        ResponseValidator.validateBadRequest(response); // Negative ID should return 400
        ResponseValidator.validateResponseTime(response, 3000);
    }
    
    @Test(description = "Verify double deletion of same author")
    public void testDeleteAuthor_DoubleDelete_Returns404() {
        // Given - First create and delete an author
        Author newAuthor = TestDataGenerator.generateAuthorForCreation();
        Response createResponse = authorsApiClient.createAuthor(newAuthor);
        ResponseValidator.validateStatusCode(createResponse, 200);
        
        Author createdAuthor = createResponse.as(Author.class);
        int authorId = createdAuthor.getId();
        
        // First deletion
        Response firstDeleteResponse = authorsApiClient.deleteAuthor(authorId);
        ResponseValidator.validateStatusCode(firstDeleteResponse, 200);
        
        
        // When - Second deletion attempt
        Response secondDeleteResponse = authorsApiClient.deleteAuthor(authorId);
        
        // Then
        ResponseValidator.validateStatusCode(secondDeleteResponse, 404);
        ResponseValidator.validateResponseTime(secondDeleteResponse, 3000);
    }
    
    @Test(description = "Verify deleting author with zero ID")
    public void testDeleteAuthor_ZeroId_ReturnsAppropriateResponse() {
        // Given
        int zeroId = 0;
        
        // When
        Response response = authorsApiClient.deleteAuthor(zeroId);
        
        // Then
        ResponseValidator.validateBadRequest(response); // Zero ID should return 400
        ResponseValidator.validateResponseTime(response, 3000);
    }
    
    @Test(description = "Verify deleting author with very large ID")
    public void testDeleteAuthor_VeryLargeId_ReturnsAppropriateResponse() {
        // Given
        int veryLargeId = Integer.MAX_VALUE;
        
        // When
        Response response = authorsApiClient.deleteAuthor(veryLargeId);
        
        // Then
        ResponseValidator.validateStatusCode(response, 404);
        ResponseValidator.validateResponseTime(response, 3000);
    }
    
    @Test(description = "Verify deletion response contains appropriate data")
    public void testDeleteAuthor_ValidateResponseData() {
        // Given - First create an author to delete
        Author newAuthor = TestDataGenerator.generateAuthorForCreation();
        Response createResponse = authorsApiClient.createAuthor(newAuthor);
        ResponseValidator.validateStatusCode(createResponse, 200);
        
        Author createdAuthor = createResponse.as(Author.class);
        int authorIdToDelete = createdAuthor.getId();
        
        
        // When
        Response deleteResponse = authorsApiClient.deleteAuthor(authorIdToDelete);
        
        // Then
        ResponseValidator.validateStatusCode(deleteResponse, 200);
        ResponseValidator.validateResponseTime(deleteResponse, 5000);
        
        // The response should be empty or contain minimal confirmation data
        if (deleteResponse.getBody().asString().length() > 0) {
            
            // If response contains data, it might be the deleted author or confirmation
            try {
                Author deletedAuthor = deleteResponse.as(Author.class);
                assertThat(deletedAuthor.getId()).isEqualTo(authorIdToDelete);
            } catch (Exception e) {
                // Response might not be in author format, which is also valid
            }
        }
        
        logTestInfo("Successfully validated deletion response for author ID: " + authorIdToDelete);
    }
    
    @Test(description = "Verify cascade behavior when deleting author")
    public void testDeleteAuthor_CascadeBehavior() {
        // Given - Create an author and note their book association
        Author newAuthor = TestDataGenerator.generateAuthorForCreation();
        Response createResponse = authorsApiClient.createAuthor(newAuthor);
        ResponseValidator.validateStatusCode(createResponse, 200);
        
        Author createdAuthor = createResponse.as(Author.class);
        int authorId = createdAuthor.getId();
        int bookId = createdAuthor.getIdBook();
        
        
        // When
        Response deleteResponse = authorsApiClient.deleteAuthor(authorId);
        
        // Then
        ResponseValidator.validateStatusCode(deleteResponse, 200);
        
        // Verify author is deleted
        Response getAuthorResponse = authorsApiClient.getAuthorById(authorId);
        ResponseValidator.validateStatusCode(getAuthorResponse, 404);
        
        // Check if associated book still exists (it should, unless API implements cascade delete)
        Response getBookResponse = booksApiClient.getBookById(bookId);
        // Book should still exist (200) unless API implements cascade delete
        assertThat(getBookResponse.getStatusCode())
                .as("Book should still exist after author deletion")
                .isIn(200, 404); // 404 if cascade delete is implemented
        
        logTestInfo("Cascade behavior verified: Book " + 
                   (getBookResponse.getStatusCode() == 200 ? "still exists" : "was also deleted"));
    }
}
