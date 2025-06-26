package com.bookstore.clients;

import com.bookstore.models.Author;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Authors API client providing methods to interact with Authors endpoints
 * Implements all CRUD operations for Authors
 * 
 * @author API Test Framework
 * @version 1.0
 */
@Slf4j
public class AuthorsApiClient extends BaseApiClient {
    
    private static final String AUTHORS_ENDPOINT = "/api/v1/Authors";
    private static final String AUTHOR_BY_ID_ENDPOINT = "/api/v1/Authors/{id}";
    private static final String AUTHORS_BY_BOOK_ENDPOINT = "/api/v1/Authors/authors/books/{idBook}";
    
    /**
     * Retrieves all authors from the API
     * 
     * @return Response containing list of authors
     */
    public Response getAllAuthors() {
        log.info("Fetching all authors");
        
        return getRequestSpec()
                .when()
                .get(AUTHORS_ENDPOINT)
                .then()
                .spec(getResponseSpec())
                .extract()
                .response();
    }
    
    /**
     * Retrieves a specific author by ID
     * 
     * @param authorId Author ID to retrieve
     * @return Response containing author details
     */
    public Response getAuthorById(int authorId) {
        log.info("Fetching author with ID: {}", authorId);
        
        return getRequestSpec()
                .pathParam("id", authorId)
                .when()
                .get(AUTHOR_BY_ID_ENDPOINT)
                .then()
                .spec(getResponseSpec())
                .extract()
                .response();
    }
    
    /**
     * Retrieves authors by book ID
     * 
     * @param bookId Book ID to get authors for
     * @return Response containing authors for the specified book
     */
    public Response getAuthorsByBookId(int bookId) {
        log.info("Fetching authors for book ID: {}", bookId);
        
        return getRequestSpec()
                .pathParam("idBook", bookId)
                .when()
                .get(AUTHORS_BY_BOOK_ENDPOINT)
                .then()
                .spec(getResponseSpec())
                .extract()
                .response();
    }
    
    /**
     * Creates a new author
     * 
     * @param author Author object to create
     * @return Response from the creation request
     */
    public Response createAuthor(Author author) {
        log.info("Creating new author: {} {}", author.getFirstName(), author.getLastName());
        
        return getRequestSpec()
                .body(author)
                .when()
                .post(AUTHORS_ENDPOINT)
                .then()
                .spec(getResponseSpec())
                .extract()
                .response();
    }
    
    /**
     * Updates an existing author
     * 
     * @param authorId Author ID to update
     * @param author Updated author data
     * @return Response from the update request
     */
    public Response updateAuthor(int authorId, Author author) {
        log.info("Updating author with ID: {} to name: {} {}", 
                authorId, author.getFirstName(), author.getLastName());
        
        return getRequestSpec()
                .pathParam("id", authorId)
                .body(author)
                .when()
                .put(AUTHOR_BY_ID_ENDPOINT)
                .then()
                .spec(getResponseSpec())
                .extract()
                .response();
    }
    
    /**
     * Deletes an author by ID
     * 
     * @param authorId Author ID to delete
     * @return Response from the deletion request
     */
    public Response deleteAuthor(int authorId) {
        log.info("Deleting author with ID: {}", authorId);
        
        return getRequestSpec()
                .pathParam("id", authorId)
                .when()
                .delete(AUTHOR_BY_ID_ENDPOINT)
                .then()
                .spec(getResponseSpec())
                .extract()
                .response();
    }
    
    /**
     * Convenience method to get all authors as a list of Author objects
     * 
     * @return List of Author objects
     */
    public List<Author> getAllAuthorsAsObjects() {
        Response response = getAllAuthors();
        return response.jsonPath().getList("", Author.class);
    }
    
    /**
     * Convenience method to get an author by ID as an Author object
     * 
     * @param authorId Author ID to retrieve
     * @return Author object or null if not found
     */
    public Author getAuthorByIdAsObject(int authorId) {
        Response response = getAuthorById(authorId);
        if (response.getStatusCode() == 200) {
            return response.as(Author.class);
        }
        return null;
    }
    
    /**
     * Convenience method to get authors by book ID as a list of Author objects
     * 
     * @param bookId Book ID to get authors for
     * @return List of Author objects
     */
    public List<Author> getAuthorsByBookIdAsObjects(int bookId) {
        Response response = getAuthorsByBookId(bookId);
        return response.jsonPath().getList("", Author.class);
    }
    
    /**
     * Checks if an author exists by ID
     * 
     * @param authorId Author ID to check
     * @return true if author exists, false otherwise
     */
    public boolean authorExists(int authorId) {
        Response response = getAuthorById(authorId);
        return response.getStatusCode() == 200;
    }
}
