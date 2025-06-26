package com.bookstore.utils;

import com.bookstore.models.Author;
import com.bookstore.models.Book;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Test data generator utility class
 * Provides methods to generate test data for Books and Authors
 * 
 * @author API Test Framework
 * @version 1.0
 */
@UtilityClass
public class TestDataGenerator {
    
    private static final AtomicInteger BOOK_COUNTER = new AtomicInteger(1000);
    private static final AtomicInteger AUTHOR_COUNTER = new AtomicInteger(2000);
    private static final Random RANDOM = new Random();
    
    // Sample data arrays
    private static final String[] BOOK_TITLES = {
            "The Great Adventure", "Mystery of the Lost City", "Future Horizons",
            "Tales of Wonder", "Journey Through Time", "Secrets of the Universe",
            "The Last Guardian", "Digital Dreams", "Whispers in the Wind",
            "Chronicles of Tomorrow", "The Hidden Truth", "Beyond the Stars"
    };
    
    private static final String[] BOOK_DESCRIPTIONS = {
            "A captivating story that will keep you on the edge of your seat",
            "An epic adventure through unknown realms and mysterious lands",
            "A thought-provoking tale about the future of humanity",
            "A heartwarming story of friendship and courage",
            "An thrilling journey through time and space"
    };
    
    private static final String[] BOOK_EXCERPTS = {
            "In the beginning, there was darkness. Then came the light...",
            "The old man looked at the horizon, knowing his time had come...",
            "She opened the book and immediately felt transported to another world...",
            "The sound of footsteps echoed through the empty corridor...",
            "It was a dark and stormy night when everything changed..."
    };
    
    private static final String[] FIRST_NAMES = {
            "John", "Jane", "Michael", "Sarah", "David", "Emily", "Robert", "Lisa",
            "William", "Jessica", "James", "Ashley", "Daniel", "Amanda", "Christopher",
            "Stephanie", "Matthew", "Melissa", "Anthony", "Nicole"
    };
    
    private static final String[] LAST_NAMES = {
            "Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller",
            "Davis", "Rodriguez", "Martinez", "Hernandez", "Lopez", "Gonzalez",
            "Wilson", "Anderson", "Thomas", "Taylor", "Moore", "Jackson", "Martin"
    };
    
    /**
     * Generates a random book with unique ID
     * 
     * @return Random Book instance
     */
    public static Book generateRandomBook() {        return Book.builder()
                .id(BOOK_COUNTER.getAndIncrement())                .title(getRandomElement(BOOK_TITLES))
                .description(getRandomElement(BOOK_DESCRIPTIONS))
                .pageCount(RANDOM.nextInt(500) + 50) // 50-550 pages
                .excerpt(getRandomElement(BOOK_EXCERPTS))
                .publishDate(generateRandomDate())
                .build();
    }
    
    /**
     * Generates a book with specific title
     * 
     * @param title Book title
     * @return Book instance with specified title
     */
    public static Book generateBookWithTitle(String title) {        return Book.builder()                // No ID for POST requests - server will generate it
                .title(title)
                .description("Auto-generated description for: " + title)
                .pageCount(RANDOM.nextInt(400) + 100)
                .excerpt("This is an excerpt from " + title)
                .publishDate(generateRandomDate())
                .build();
    }
    
    /**
     * Generates a book without ID (for creation tests)
     * 
     * @return Book instance without ID
     */
    public static Book generateBookForCreation() {        return Book.builder()
                .title(getRandomElement(BOOK_TITLES) + " " + System.currentTimeMillis())
                .description(getRandomElement(BOOK_DESCRIPTIONS))
                .pageCount(RANDOM.nextInt(400) + 100)
                .excerpt(getRandomElement(BOOK_EXCERPTS))
                .publishDate(OffsetDateTime.now())
                .build();
    }
    
    /**
     * Generates a book with invalid data (for negative testing)
     * 
     * @return Book instance with invalid data
     */
    public static Book generateInvalidBook() {
        return Book.builder()
                .id(-1)
                .title("") // Empty title
                .description(null)
                .pageCount(-10) // Negative page count
                .excerpt("")
                .publishDate(null)
                .build();
    }
    
    /**
     * Generates a random author with unique ID
     * 
     * @return Random Author instance
     */
    public static Author generateRandomAuthor() {
        return Author.builder()
                .id(AUTHOR_COUNTER.getAndIncrement())
                .idBook(RANDOM.nextInt(100) + 1)
                .firstName(getRandomElement(FIRST_NAMES))
                .lastName(getRandomElement(LAST_NAMES))
                .build();
    }
    
    /**
     * Generates an author for a specific book
     * 
     * @param bookId Book ID to associate with
     * @return Author instance for the specified book
     */
    public static Author generateAuthorForBook(int bookId) {
        return Author.builder()
                .id(AUTHOR_COUNTER.getAndIncrement())
                .idBook(bookId)
                .firstName(getRandomElement(FIRST_NAMES))
                .lastName(getRandomElement(LAST_NAMES))
                .build();
    }
    
    /**
     * Generates an author without ID (for creation tests)
     * 
     * @return Author instance without ID
     */
    public static Author generateAuthorForCreation() {
        return Author.builder()
                .idBook(RANDOM.nextInt(100) + 1)
                .firstName(getRandomElement(FIRST_NAMES))
                .lastName(getRandomElement(LAST_NAMES))
                .build();
    }
    
    /**
     * Generates an author with specific names
     * 
     * @param firstName First name
     * @param lastName Last name
     * @return Author instance with specified names
     */
    public static Author generateAuthorWithNames(String firstName, String lastName) {
        return Author.builder()
                .id(AUTHOR_COUNTER.getAndIncrement())
                .idBook(RANDOM.nextInt(100) + 1)
                .firstName(firstName)
                .lastName(lastName)
                .build();
    }
    
    /**
     * Generates an author with invalid data (for negative testing)
     * 
     * @return Author instance with invalid data
     */
    public static Author generateInvalidAuthor() {
        return Author.builder()
                .id(-1)
                .idBook(-1)
                .firstName("") // Empty first name
                .lastName(null) // Null last name
                .build();
    }    /**
     * Generates a random date within the last 10 years
     * 
     * @return Random OffsetDateTime
     */
    private static OffsetDateTime generateRandomDate() {
        long minDay = OffsetDateTime.now().minusYears(10).toLocalDate().toEpochDay();
        long maxDay = OffsetDateTime.now().toLocalDate().toEpochDay();
        long randomDay = minDay + RANDOM.nextInt((int) (maxDay - minDay));
        
        return LocalDate.ofEpochDay(randomDay).atStartOfDay().atOffset(ZoneOffset.UTC);
    }
    
    /**
     * Gets a random element from an array
     * 
     * @param array Array to select from
     * @return Random element
     */
    private static String getRandomElement(String[] array) {
        return array[RANDOM.nextInt(array.length)];
    }
    
    /**
     * Generates a unique string for testing
     * 
     * @param prefix Prefix for the string
     * @return Unique string with timestamp
     */
    public static String generateUniqueString(String prefix) {
        return prefix + "_" + System.currentTimeMillis();
    }
    
    /**
     * Generates a random integer within a range
     * 
     * @param min Minimum value (inclusive)
     * @param max Maximum value (exclusive)
     * @return Random integer
     */
    public static int generateRandomInt(int min, int max) {
        return RANDOM.nextInt(max - min) + min;
    }
}
