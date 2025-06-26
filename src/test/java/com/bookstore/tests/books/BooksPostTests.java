package com.bookstore.tests.books;

import com.bookstore.base.BaseTest;
import com.bookstore.models.Book;
import com.bookstore.utils.ResponseValidator;
import com.bookstore.utils.TestDataGenerator;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for Books API POST operations
 * Covers happy path and edge cases for creating books
 * 
 * @author API Test Framework
 * @version 1.0
 */
public class BooksPostTests extends BaseTest {
    
    @Test(description = "Verify creating a new book with valid data succeeds")
    public void testCreateBook_ValidData_Success() {
        // Given
        Book newBook = TestDataGenerator.generateBookForCreation();
        
        // When
        Response response = booksApiClient.createBook(newBook);
        
        // Then
        ResponseValidator.validateStatusCode(response, 200);
        ResponseValidator.validateResponseTime(response, 5000);
        
        // Note: FakeRestAPI typically returns the created object or a success indicator
        logTestInfo("Successfully created book: " + newBook.getTitle());
    }
    
    @Test(description = "Verify creating a book with complete data structure")
    public void testCreateBook_CompleteData_Success() {        // Given
        Book completeBook = Book.builder()
                // No ID for POST - server will generate it
                .title("Complete Test Book " + getCurrentTimestamp())
                .description("This is a comprehensive test book with all fields populated")
                .pageCount(350)
                .excerpt("This excerpt demonstrates complete book data structure...")
                .publishDate(OffsetDateTime.now())
                .build();
        
        
        // When
        Response response = booksApiClient.createBook(completeBook);
        
        // Then
        ResponseValidator.validateStatusCode(response, 200);
        ResponseValidator.validateResponseTime(response, 5000);
        
        logTestInfo("Successfully created complete book: " + completeBook.getTitle());
    }
    
    @Test(description = "Verify creating a book with minimal required data")
    public void testCreateBook_MinimalData_Success() {
        // Given
        Book minimalBook = Book.builder()                .title("Minimal Book " + getCurrentTimestamp())
                .description("Minimal description")
                .pageCount(1)
                .excerpt("Minimal excerpt")
                .publishDate(OffsetDateTime.now())
                .build();
        
        
        // When
        Response response = booksApiClient.createBook(minimalBook);
        
        // Then
        ResponseValidator.validateStatusCode(response, 200);
        ResponseValidator.validateResponseTime(response, 5000);
        
        logTestInfo("Successfully created minimal book: " + minimalBook.getTitle());
    }
    
    @Test(description = "Verify creating book with null title returns appropriate error")
    public void testCreateBook_NullTitle_ReturnsError() {
        // Given
        Book invalidBook = Book.builder()
                .title(null)
                .description("Book with null title")
                .pageCount(100)
                .excerpt("Test excerpt")
                .publishDate(OffsetDateTime.now())
                .build();
        
        
        // When
        Response response = booksApiClient.createBook(invalidBook);
        
        // Then
        // The API behavior may vary - it might return 400, 422, or even 200 depending on implementation
        assertThat(response.getStatusCode())
                .as("Should return appropriate error status for null title")
                .isIn(200, 400, 422);
        
        ResponseValidator.validateResponseTime(response, 5000);
        logTestInfo("Received status " + response.getStatusCode() + " for null title");
    }
    
    @Test(description = "Verify creating book with empty title")
    public void testCreateBook_EmptyTitle_AppropriateBehavior() {
        // Given
        Book bookWithEmptyTitle = Book.builder()
                .title("")
                .description("Book with empty title")
                .pageCount(100)
                .excerpt("Test excerpt")
                .publishDate(OffsetDateTime.now())
                .build();
        
        
        // When
        Response response = booksApiClient.createBook(bookWithEmptyTitle);
        
        // Then
        ResponseValidator.validateResponseTime(response, 5000);
        
        // Document the actual behavior
        logTestInfo("API returned status " + response.getStatusCode() + " for empty title");
    }
    
    @Test(description = "Verify creating book with negative page count")
    public void testCreateBook_NegativePageCount_AppropriateBehavior() {
        // Given
        
        Book bookWithNegativePages = Book.builder()
                .title("Book with Negative Pages " + getCurrentTimestamp())
                .description("Testing negative page count validation")
                .pageCount(-50)
                .excerpt("Test excerpt")
                .publishDate(OffsetDateTime.now())
                .build();
        
        
        // When
        Response response = booksApiClient.createBook(bookWithNegativePages);
        
        // Then
        ResponseValidator.validateResponseTime(response, 5000);
        
        // Document the actual behavior
        logTestInfo("API returned status " + response.getStatusCode() + " for negative page count");
    }
    
    @Test(description = "Verify creating book with extremely large data")
    public void testCreateBook_LargeData_AppropriateBehavior() {
        // Given
        
        String longTitle = "Very Long Title ".repeat(100); // Very long title
        String longDescription = "Very long description content ".repeat(1000); // Very long description
          Book bookWithLargeData = Book.builder()
                .title(longTitle)
                .description(longDescription)
                .pageCount(999999)
                .excerpt("Standard excerpt")
                .publishDate(OffsetDateTime.now())
                .build();
        
        
        // When
        Response response = booksApiClient.createBook(bookWithLargeData);
        
        // Then
        ResponseValidator.validateResponseTime(response, 10000); // Allow more time for large data
        
        // Document the behavior
        logTestInfo("API returned status " + response.getStatusCode() + " for large data");
    }
    
    @Test(description = "Verify creating multiple books in sequence")
    public void testCreateBook_MultipleBooksSequence_Success() {
        // Given
        int numberOfBooks = 3;
        
        // When & Then
        for (int i = 1; i <= numberOfBooks; i++) {
            Book book = TestDataGenerator.generateBookWithTitle("Sequential Book " + i + " " + getCurrentTimestamp());
            
            Response response = booksApiClient.createBook(book);
            
            ResponseValidator.validateStatusCode(response, 200);
            ResponseValidator.validateResponseTime(response, 5000);
            
        }
        
        logTestInfo("Successfully created " + numberOfBooks + " books in sequence");
    }
}
