package com.bookstore.tests.books;

import com.bookstore.base.BaseTest;
import com.bookstore.utils.ResponseValidator;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for Books API DELETE operations
 * Covers happy path and edge cases for deleting books
 * 
 * @author API Test Framework
 * @version 1.0
 */
public class BooksDeleteTests extends BaseTest {
    
    @Test(description = "Verify deleting an existing book succeeds")
    public void testDeleteBook_ExistingBook_Success() {
        // Given
        int bookId = 1;
        
        // When
        Response deleteResponse = booksApiClient.deleteBook(bookId);
        
        // Then
        ResponseValidator.validateStatusCode(deleteResponse, 200);
        ResponseValidator.validateResponseTime(deleteResponse, 5000);
        
        logTestInfo("Successfully deleted book ID: " + bookId);
    }
    
    @Test(description = "Verify deleting non-existent book returns appropriate response")
    public void testDeleteBook_NonExistentBook_AppropriateBehavior() {
        // Given
        int nonExistentId = 999999;
        
        // When
        Response response = booksApiClient.deleteBook(nonExistentId);
        
        // Then
        ResponseValidator.validateStatusCode(response, 404);
        ResponseValidator.validateResponseTime(response, 5000);
    }
    
    @Test(description = "Verify deleting book with negative ID returns appropriate error")
    public void testDeleteBook_NegativeId_AppropriateBehavior() {
        // Given
        int negativeId = -1;
        
        // When
        Response response = booksApiClient.deleteBook(negativeId);
        
        // Then
        ResponseValidator.validateBadRequest(response);
        ResponseValidator.validateResponseTime(response, 5000);
    }
    
    @Test(description = "Verify deleting book with invalid ID format returns error")
    public void testDeleteBook_InvalidIdFormat_ReturnsError() {
        // Given
        String invalidId = "invalid_id";
        
        // When
        Response response = booksApiClient.getRequestSpec()
                .pathParam("id", invalidId)
                .when()
                .delete("/api/v1/Books/{id}");
        
        // Then
        ResponseValidator.validateBadRequest(response);
        ResponseValidator.validateResponseTime(response, 5000);
        
        logTestInfo("Correctly received client error for invalid ID format");
    }
    
    @Test(description = "Verify deleting the same book multiple times (idempotency)")
    public void testDeleteBook_MultipleDeletes_IdempotentBehavior() {
        // Given
        int bookId = 15;
        
        // When & Then
        Response firstDeleteResponse = booksApiClient.deleteBook(bookId);
        ResponseValidator.validateStatusCode(firstDeleteResponse, 200);
        
        Response secondDeleteResponse = booksApiClient.deleteBook(bookId);
        
        Response thirdDeleteResponse = booksApiClient.deleteBook(bookId);
        
        // Verify all responses are handled appropriately
        assertThat(secondDeleteResponse.getStatusCode())
                .as("Second delete should be handled gracefully")
                .isIn(200, 404);
        
        assertThat(thirdDeleteResponse.getStatusCode())
                .as("Third delete should be handled gracefully")
                .isIn(200, 404);
        
        logTestInfo("Idempotency test completed - multiple deletes handled appropriately");
    }
    
    @Test(description = "Verify deleting book and then trying to retrieve it")
    public void testDeleteBook_ThenRetrieve_BookNotFound() {
        // Given
        int bookId = 20;
        
        // When
        Response deleteResponse = booksApiClient.deleteBook(bookId);
        ResponseValidator.validateStatusCode(deleteResponse, 200);
        
        // Wait a moment for potential eventual consistency
        waitFor(1000);
        Response getResponse = booksApiClient.getBookById(bookId);
        
        // Then
        ResponseValidator.validateStatusCode(getResponse, 404);
        ResponseValidator.validateResponseTime(getResponse, 5000);
    }
    
    @Test(description = "Verify deleting multiple books in sequence")
    public void testDeleteBook_MultipleBooks_Success() {
        // Given
        int[] bookIds = {25, 26, 27, 28, 29};
        
        // When & Then
        for (int i = 0; i < bookIds.length; i++) {
            int bookId = bookIds[i];
            
            Response response = booksApiClient.deleteBook(bookId);
            
            ResponseValidator.validateStatusCode(response, 200);
            ResponseValidator.validateResponseTime(response, 5000);
            
            
            // Small delay between deletions to avoid overwhelming the API
            if (i < bookIds.length - 1) {
                waitFor(500);
            }
        }
        
        logTestInfo("Successfully deleted " + bookIds.length + " books in sequence");    }
}
