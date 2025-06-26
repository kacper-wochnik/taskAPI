package com.bookstore.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * Book model representing the Book entity from FakeRestAPI
 * 
 * @author API Test Framework
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Book {
    
    @JsonProperty("id")
    private Integer id;
    
    @JsonProperty("title")
    private String title;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("pageCount")
    private Integer pageCount;    @JsonProperty("excerpt")
    private String excerpt;    @JsonProperty("publishDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private OffsetDateTime publishDate;
    
    /**
     * Creates a sample book for testing purposes
     * 
     * @return Book instance with test data
     */    public static Book createSampleBook() {
        return Book.builder()
                .id(1)
                .title("Test Book Title")
                .description("This is a test book description")
                .pageCount(250)
                .excerpt("This is a test excerpt from the book...")
                .publishDate(OffsetDateTime.now())
                .build();
    }
    
    /**
     * Creates a book with minimal required fields
     * 
     * @param title Book title
     * @return Book instance
     */    public static Book createWithTitle(String title) {
        return Book.builder()
                .title(title)
                .description("Auto-generated description for " + title)
                .pageCount(100)
                .excerpt("Auto-generated excerpt")
                .publishDate(OffsetDateTime.now())
                .build();
    }
}
