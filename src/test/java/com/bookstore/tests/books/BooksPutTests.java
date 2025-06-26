package com.bookstore.tests.books;

import com.bookstore.base.BaseTest;
import com.bookstore.models.Book;
import com.bookstore.utils.ResponseValidator;
import com.bookstore.utils.TestDataGenerator;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.time.OffsetDateTime;

/**
 * Test class for Books API PUT operations
 * Covers happy path and edge cases for updating books
 * 
 * @author API Test Framework
 * @version 1.0
 */
public class BooksPutTests extends BaseTest {
    
    @Test(description = "Verify updating an existing book with valid data succeeds")
    public void testUpdateBook_ValidData_Success() {
        // Given
        int bookId = 1;
        Book updatedBook = Book.builder()
                .id(bookId)                .title("Updated Book Title " + getCurrentTimestamp())
                .description("This is an updated description for the book")
                .pageCount(450)
                .excerpt("This is an updated excerpt from the book...")
                .publishDate(OffsetDateTime.now())
                .build();
        
        
        // When
        Response response = booksApiClient.updateBook(bookId, updatedBook);
        
        // Then
        ResponseValidator.validateStatusCode(response, 200);
        ResponseValidator.validateResponseTime(response, 5000);
        
        logTestInfo("Successfully updated book ID: " + bookId);
    }
    
    @Test(description = "Verify updating book with all fields modified")
    public void testUpdateBook_AllFieldsModified_Success() {
        // Given
        int bookId = 2;
        Book completelyUpdatedBook = Book.builder()
                .id(bookId)
                .title("Completely Updated Book " + getCurrentTimestamp())
                .description("Every field in this book has been updated for comprehensive testing")
                .pageCount(675)
                .excerpt("This excerpt has been completely rewritten to test full update capability...")
                .publishDate(OffsetDateTime.now().minusDays(30)) // Set to 30 days ago
                .build();
        
        
        // When
        Response response = booksApiClient.updateBook(bookId, completelyUpdatedBook);
        
        // Then
        ResponseValidator.validateStatusCode(response, 200);
        ResponseValidator.validateResponseTime(response, 5000);
        
        logTestInfo("Successfully updated all fields for book ID: " + bookId);
    }
    
    @Test(description = "Verify updating book with partial data")
    public void testUpdateBook_PartialData_Success() {
        // Given
        int bookId = 3;
        Book partialUpdateBook = Book.builder()                .id(bookId)
                .title("Partially Updated Title " + getCurrentTimestamp())
                .description("Only title and description updated")
                .pageCount(100) // Minimal value
                .excerpt("Minimal excerpt")
                .publishDate(OffsetDateTime.now())
                .build();
        
        
        // When
        Response response = booksApiClient.updateBook(bookId, partialUpdateBook);
        
        // Then
        ResponseValidator.validateStatusCode(response, 200);
        ResponseValidator.validateResponseTime(response, 5000);
        
        logTestInfo("Successfully performed partial update for book ID: " + bookId);
    }
    
    @Test(description = "Verify updating non-existent book returns appropriate error")
    public void testUpdateBook_NonExistentId_ReturnsError() {
        // Given
        int nonExistentId = 999999;
        Book updateData = TestDataGenerator.generateRandomBook();
        updateData.setId(nonExistentId);
        
        
        // When
        Response response = booksApiClient.updateBook(nonExistentId, updateData);
        
        // Then
        ResponseValidator.validateStatusCode(response, 404);
        ResponseValidator.validateResponseTime(response, 5000);
    }
    
    @Test(description = "Verify updating book with mismatched ID in path and body")
    public void testUpdateBook_MismatchedIds_AppropriateBehavior() {
        // Given
        int pathId = 5;
        int bodyId = 10;
        Book bookWithDifferentId = Book.builder()
                .id(bodyId) // Different from path ID                .title("Book with Mismatched ID " + getCurrentTimestamp())
                .description("Testing ID mismatch behavior")
                .pageCount(300)
                .excerpt("Test excerpt for ID mismatch")
                .publishDate(OffsetDateTime.now())
                .build();
        
        
        // When
        Response response = booksApiClient.updateBook(pathId, bookWithDifferentId);
        
        // Then
        ResponseValidator.validateResponseTime(response, 5000);
        
        // Document the actual behavior
        logTestInfo("API returned status " + response.getStatusCode() + " for mismatched IDs");
    }
    
    @Test(description = "Verify updating book with null values")
    public void testUpdateBook_NullValues_AppropriateBehavior() {
        // Given
        int bookId = 6;
        Book bookWithNulls = Book.builder()
                .id(bookId)
                .title(null) // Null title
                .description(null) // Null description
                .pageCount(null) // Null page count
                .excerpt(null) // Null excerpt
                .publishDate(null) // Null publish date
                .build();
        
        
        // When
        Response response = booksApiClient.updateBook(bookId, bookWithNulls);
        
        // Then
        ResponseValidator.validateResponseTime(response, 5000);
        
        // Document the behavior - API might accept nulls or reject them
        logTestInfo("API returned status " + response.getStatusCode() + " for null values");
    }
    
    @Test(description = "Verify updating book with invalid data types")
    public void testUpdateBook_InvalidDataTypes_ReturnsError() {
        // Given
        int bookId = 7;
          // Create JSON with invalid data types manually
        String invalidBookJson = String.format(
            "{\n" +
            "    \"id\": \"%d\",\n" +
            "    \"title\": \"Valid Title\",\n" +
            "    \"description\": \"Valid Description\",\n" +
            "    \"pageCount\": \"not_a_number\",\n" +
            "    \"excerpt\": \"Valid excerpt\",\n" +
            "    \"publishDate\": \"invalid_date_format\"\n" +
            "}", bookId);
        
        
        // When
        Response response = booksApiClient.getRequestSpec()
                .pathParam("id", bookId)
                .body(invalidBookJson)
                .when()
                .put("/api/v1/Books/{id}");
        
        // Then
        ResponseValidator.validateBadRequest(response);
        ResponseValidator.validateResponseTime(response, 5000);
        
        logTestInfo("Correctly received client error for invalid data types");
    }
    
    @Test(description = "Verify updating book with extremely large values")
    public void testUpdateBook_ExtremelyLargeValues_AppropriateBehavior() {
        // Given
        int bookId = 8;
        String hugeTitle = "Extremely Long Title ".repeat(1000);
        String hugeDescription = "Very detailed description ".repeat(5000);
        
        Book bookWithLargeValues = Book.builder()
                .id(bookId)
                .title(hugeTitle)
                .description(hugeDescription)                .pageCount(Integer.MAX_VALUE)
                .excerpt("Standard excerpt")
                .publishDate(OffsetDateTime.now())
                .build();
        
        
        // When
        Response response = booksApiClient.updateBook(bookId, bookWithLargeValues);
        
        // Then
        ResponseValidator.validateResponseTime(response, 15000); // Allow more time
        
        // Document the behavior
        logTestInfo("API returned status " + response.getStatusCode() + " for extremely large values");
    }
}
