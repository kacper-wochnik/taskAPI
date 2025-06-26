package com.bookstore.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Author model representing the Author entity from FakeRestAPI
 * 
 * @author API Test Framework
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Author {
    
    @JsonProperty("id")
    private Integer id;
    
    @JsonProperty("idBook")
    private Integer idBook;
    
    @JsonProperty("firstName")
    private String firstName;
    
    @JsonProperty("lastName")
    private String lastName;
    
    /**
     * Creates a sample author for testing purposes
     * 
     * @return Author instance with test data
     */
    public static Author createSampleAuthor() {
        return Author.builder()
                .id(1)
                .idBook(1)
                .firstName("John")
                .lastName("Doe")
                .build();
    }
    
    /**
     * Creates an author with custom names
     * 
     * @param firstName Author's first name
     * @param lastName Author's last name
     * @return Author instance
     */
    public static Author createWithNames(String firstName, String lastName) {
        return Author.builder()
                .firstName(firstName)
                .lastName(lastName)
                .idBook(1)
                .build();
    }
    
    /**
     * Creates an author associated with a specific book
     * 
     * @param firstName Author's first name
     * @param lastName Author's last name
     * @param bookId Associated book ID
     * @return Author instance
     */
    public static Author createWithBookId(String firstName, String lastName, Integer bookId) {
        return Author.builder()
                .firstName(firstName)
                .lastName(lastName)
                .idBook(bookId)
                .build();
    }
      /**
     * Gets full name of the author
     * 
     * @return Full name as "firstName lastName"
     */
    @JsonIgnore
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
