package com.bookstore.clients;

import com.bookstore.models.Book;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Books API client providing methods to interact with Books endpoints
 * Implements all CRUD operations for Books
 * 
 * @author API Test Framework
 * @version 1.0
 */
@Slf4j
public class BooksApiClient extends BaseApiClient {
    
    private static final String BOOKS_ENDPOINT = "/api/v1/Books";
    private static final String BOOK_BY_ID_ENDPOINT = "/api/v1/Books/{id}";
    
    /**
     * Retrieves all books from the API
     * 
     * @return Response containing list of books
     */    public Response getAllBooks() {
        log.info("Fetching all books");
        
        return getRequestSpec()
                .when()
                .get(BOOKS_ENDPOINT)
                .then()
                .spec(getResponseSpec())
                .extract()
                .response();
    }
    
    /**
     * Retrieves a specific book by ID
     * 
     * @param bookId Book ID to retrieve
     * @return Response containing book details
     */
    public Response getBookById(int bookId) {
        log.info("Fetching book with ID: {}", bookId);
        
        return getRequestSpec()
                .pathParam("id", bookId)
                .when()
                .get(BOOK_BY_ID_ENDPOINT)
                .then()
                .spec(getResponseSpec())
                .extract()
                .response();
    }
    
    /**
     * Creates a new book
     * 
     * @param book Book object to create
     * @return Response from the creation request
     */
    public Response createBook(Book book) {
        log.info("Creating new book: {}", book.getTitle());
        
        return getRequestSpec()
                .body(book)
                .when()
                .post(BOOKS_ENDPOINT)
                .then()
                .spec(getResponseSpec())
                .extract()
                .response();
    }
    
    /**
     * Updates an existing book
     * 
     * @param bookId Book ID to update
     * @param book Updated book data
     * @return Response from the update request
     */
    public Response updateBook(int bookId, Book book) {
        log.info("Updating book with ID: {} to title: {}", bookId, book.getTitle());
        
        return getRequestSpec()
                .pathParam("id", bookId)
                .body(book)
                .when()
                .put(BOOK_BY_ID_ENDPOINT)
                .then()
                .spec(getResponseSpec())
                .extract()
                .response();
    }
    
    /**
     * Deletes a book by ID
     * 
     * @param bookId Book ID to delete
     * @return Response from the deletion request
     */
    public Response deleteBook(int bookId) {
        log.info("Deleting book with ID: {}", bookId);
        
        return getRequestSpec()
                .pathParam("id", bookId)
                .when()
                .delete(BOOK_BY_ID_ENDPOINT)
                .then()
                .spec(getResponseSpec())
                .extract()
                .response();
    }
    
    /**
     * Convenience method to get all books as a list of Book objects
     * 
     * @return List of Book objects
     */
    public List<Book> getAllBooksAsObjects() {
        Response response = getAllBooks();
        return response.jsonPath().getList("", Book.class);
    }
    
    /**
     * Convenience method to get a book by ID as a Book object
     * 
     * @param bookId Book ID to retrieve
     * @return Book object or null if not found
     */
    public Book getBookByIdAsObject(int bookId) {
        Response response = getBookById(bookId);
        if (response.getStatusCode() == 200) {
            return response.as(Book.class);
        }
        return null;
    }
    
    /**
     * Checks if a book exists by ID
     * 
     * @param bookId Book ID to check
     * @return true if book exists, false otherwise
     */
    public boolean bookExists(int bookId) {
        Response response = getBookById(bookId);
        return response.getStatusCode() == 200;
    }
}
