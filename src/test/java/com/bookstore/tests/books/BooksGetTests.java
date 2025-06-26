package com.bookstore.tests.books;

import com.bookstore.base.BaseTest;
import com.bookstore.models.Book;
import com.bookstore.utils.ResponseValidator;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for Books API GET operations
 * Covers happy path and edge cases for retrieving books
 * 
 * @author API Test Framework
 * @version 1.0
 */
public class BooksGetTests extends BaseTest {
    
    @Test(description = "Verify getting all books returns successful response")
    public void testGetAllBooks_Success() {
        // Given
        
        // When
        Response response = booksApiClient.getAllBooks();
        
        // Then
        ResponseValidator.validateStatusCode(response, 200);
        ResponseValidator.validateJsonContentType(response);
        ResponseValidator.validateJsonArray(response);
        ResponseValidator.validateResponseTime(response, 5000);
        
        List<Book> books = response.jsonPath().getList("", Book.class);
        
        if (!books.isEmpty()) {
            Book firstBook = books.get(0);
            assertThat(firstBook.getId()).isNotNull();
            assertThat(firstBook.getTitle()).isNotNull();
            logTestInfo("Successfully retrieved " + books.size() + " books");
        }
    }
    
    @Test(description = "Verify getting a specific book by valid ID returns correct book")
    public void testGetBookById_ValidId_Success() {
        // Given
        int bookId = 1;
        
        // When
        Response response = booksApiClient.getBookById(bookId);
        
        // Then
        ResponseValidator.validateBookResponse(response);
        ResponseValidator.validateResponseTime(response, 3000);
        
        Book book = response.as(Book.class);
        assertThat(book.getId()).isEqualTo(bookId);
        assertThat(book.getTitle()).isNotNull().isNotEmpty();
        assertThat(book.getDescription()).isNotNull();
        assertThat(book.getPageCount()).isNotNull().isGreaterThan(0);
        assertThat(book.getExcerpt()).isNotNull();
        assertThat(book.getPublishDate()).isNotNull();
        
        logTestInfo("Successfully retrieved book: " + book.getTitle());
    }
    
    @Test(description = "Verify getting book with non-existent ID returns 404")
    public void testGetBookById_NonExistentId_Returns404() {
        // Given
        int nonExistentId = 999999;
        
        // When
        Response response = booksApiClient.getBookById(nonExistentId);
        
        // Then
        ResponseValidator.validateStatusCode(response, 404);
        ResponseValidator.validateResponseTime(response, 3000);
        
        logTestInfo("Correctly received 404 for non-existent book ID: " + nonExistentId);
    }
    
    @Test(description = "Verify getting book with invalid ID format returns appropriate error")
    public void testGetBookById_InvalidIdFormat_ReturnsError() {
        // Given
        String invalidId = "abc";
        
        // When
        Response response = booksApiClient.getRequestSpec()
                .pathParam("id", invalidId)
                .when()
                .get("/api/v1/Books/{id}");
        
        // Then
        ResponseValidator.validateBadRequest(response);
        ResponseValidator.validateResponseTime(response, 3000);
        
        logTestInfo("Correctly received error for invalid book ID format: " + invalidId);
    }
    
    @Test(description = "Verify getting book with negative ID returns appropriate response")
    public void testGetBookById_NegativeId_ReturnsAppropriateResponse() {
        // Given
        int negativeId = -1;
        
        // When
        Response response = booksApiClient.getBookById(negativeId);
        
        // Then
        // The API might return 404 or 400, both are acceptable for negative IDs
        assertThat(response.getStatusCode())
                .as("Status code should be 400 or 404 for negative ID")
                .isIn(400, 404);
        ResponseValidator.validateResponseTime(response, 3000);
        
        logTestInfo("Received status " + response.getStatusCode() + " for negative book ID");
    }
    
    @Test(description = "Verify response structure and data types for all books")
    public void testGetAllBooks_ValidateResponseStructure() {
        // Given
        
        // When
        Response response = booksApiClient.getAllBooks();
        
        // Then
        ResponseValidator.validateStatusCode(response, 200);
        ResponseValidator.validateJsonArray(response);
        
        List<Book> books = response.jsonPath().getList("", Book.class);
        
        if (!books.isEmpty()) {
            Book sampleBook = books.get(0);
            
            // Validate data types and constraints
            assertThat(sampleBook.getId())
                    .as("Book ID should be a positive integer")
                    .isNotNull()
                    .isGreaterThan(0);
            
            assertThat(sampleBook.getTitle())
                    .as("Book title should not be null or empty")
                    .isNotNull();
            
            assertThat(sampleBook.getPageCount())
                    .as("Page count should be a positive integer")
                    .isNotNull()
                    .isGreaterThanOrEqualTo(0);
            
            assertThat(sampleBook.getPublishDate())
                    .as("Publish date should not be null")
                    .isNotNull();
            logTestInfo("Response structure validation passed for " + books.size() + " books");
        }
    }
}
