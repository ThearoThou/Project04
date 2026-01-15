package com.example.project.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("BookRequest DTO Validation Tests")
class BookRequestTest {

    @Autowired
    private Validator validator;

    private BookRequest bookRequest;

    @BeforeEach
    void setUp() {
        bookRequest = new BookRequest();
    }

    // ================== Valid Tests ==================

    @Test
    @DisplayName("Should accept valid BookRequest")
    void testValidBookRequest() {
        bookRequest.setTitle("The Great Gatsby");
        bookRequest.setAuthor("F. Scott Fitzgerald");
        bookRequest.setIsbn("978-0-7432-7356-5");
        bookRequest.setGenre("Fiction");
        bookRequest.setQuantity(5);

        Set<ConstraintViolation<BookRequest>> violations = validator.validate(bookRequest);
        assertTrue(violations.isEmpty(), "Valid BookRequest should have no violations");
    }

    // ================== Title Tests ==================

    @Test
    @DisplayName("Should reject empty title")
    void testEmptyTitle() {
        bookRequest.setTitle("");
        bookRequest.setAuthor("F. Scott Fitzgerald");
        bookRequest.setIsbn("978-0-7432-7356-5");
        bookRequest.setGenre("Fiction");
        bookRequest.setQuantity(5);

        Set<ConstraintViolation<BookRequest>> violations = validator.validate(bookRequest);
        assertFalse(violations.isEmpty(), "Empty title should fail validation");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Title is required")));
    }

    @Test
    @DisplayName("Should reject null title")
    void testNullTitle() {
        bookRequest.setTitle(null);
        bookRequest.setAuthor("Author");
        bookRequest.setIsbn("978-0-7432-7356-5");
        bookRequest.setGenre("Fiction");
        bookRequest.setQuantity(5);

        Set<ConstraintViolation<BookRequest>> violations = validator.validate(bookRequest);
        assertFalse(violations.isEmpty(), "Null title should fail validation");
    }

    @Test
    @DisplayName("Should reject title exceeding max length (70)")
    void testTitleExceedsMaxLength() {
        bookRequest.setTitle("A".repeat(71));
        bookRequest.setAuthor("Author");
        bookRequest.setIsbn("978-0-7432-7356-5");
        bookRequest.setGenre("Fiction");
        bookRequest.setQuantity(5);

        Set<ConstraintViolation<BookRequest>> violations = validator.validate(bookRequest);
        assertFalse(violations.isEmpty(), "Title exceeding 70 chars should fail");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("between 1 and 70")));
    }

    @Test
    @DisplayName("Should reject title with invalid characters")
    void testTitleWithInvalidCharacters() {
        bookRequest.setTitle("Book@Title#123");
        bookRequest.setAuthor("Author");
        bookRequest.setIsbn("978-0-7432-7356-5");
        bookRequest.setGenre("Fiction");
        bookRequest.setQuantity(5);

        Set<ConstraintViolation<BookRequest>> violations = validator.validate(bookRequest);
        assertFalse(violations.isEmpty(), "Title with invalid characters should fail");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("invalid characters")));
    }

    // ================== Author Tests ==================

    @Test
    @DisplayName("Should reject empty author")
    void testEmptyAuthor() {
        bookRequest.setTitle("Valid Title");
        bookRequest.setAuthor("");
        bookRequest.setIsbn("978-0-7432-7356-5");
        bookRequest.setGenre("Fiction");
        bookRequest.setQuantity(5);

        Set<ConstraintViolation<BookRequest>> violations = validator.validate(bookRequest);
        assertFalse(violations.isEmpty(), "Empty author should fail validation");
    }

    @Test
    @DisplayName("Should reject null author")
    void testNullAuthor() {
        bookRequest.setTitle("Valid Title");
        bookRequest.setAuthor(null);
        bookRequest.setIsbn("978-0-7432-7356-5");
        bookRequest.setGenre("Fiction");
        bookRequest.setQuantity(5);

        Set<ConstraintViolation<BookRequest>> violations = validator.validate(bookRequest);
        assertFalse(violations.isEmpty(), "Null author should fail validation");
    }

    @Test
    @DisplayName("Should reject author with numbers")
    void testAuthorWithNumbers() {
        bookRequest.setTitle("Valid Title");
        bookRequest.setAuthor("John Doe123");
        bookRequest.setIsbn("978-0-7432-7356-5");
        bookRequest.setGenre("Fiction");
        bookRequest.setQuantity(5);

        Set<ConstraintViolation<BookRequest>> violations = validator.validate(bookRequest);
        assertFalse(violations.isEmpty(), "Author with numbers should fail");
    }

    // ================== ISBN Tests ==================

    @Test
    @DisplayName("Should reject ISBN less than 10 characters")
    void testISBNTooShort() {
        bookRequest.setTitle("Valid Title");
        bookRequest.setAuthor("Author");
        bookRequest.setIsbn("123456789");
        bookRequest.setGenre("Fiction");
        bookRequest.setQuantity(5);

        Set<ConstraintViolation<BookRequest>> violations = validator.validate(bookRequest);
        assertFalse(violations.isEmpty(), "ISBN < 10 chars should fail");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("between 10 and 17")));
    }

    @Test
    @DisplayName("Should reject ISBN exceeding 17 characters")
    void testISBNTooLong() {
        bookRequest.setTitle("Valid Title");
        bookRequest.setAuthor("Author");
        bookRequest.setIsbn("978-0-7432-7356-55555");
        bookRequest.setGenre("Fiction");
        bookRequest.setQuantity(5);

        Set<ConstraintViolation<BookRequest>> violations = validator.validate(bookRequest);
        assertFalse(violations.isEmpty(), "ISBN > 17 chars should fail");
    }

    @Test
    @DisplayName("Should reject ISBN with invalid characters")
    void testISBNWithInvalidCharacters() {
        bookRequest.setTitle("Valid Title");
        bookRequest.setAuthor("Author");
        bookRequest.setIsbn("978-0-ABC2-7356");
        bookRequest.setGenre("Fiction");
        bookRequest.setQuantity(5);

        Set<ConstraintViolation<BookRequest>> violations = validator.validate(bookRequest);
        assertFalse(violations.isEmpty(), "ISBN with letters should fail");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("numbers and hyphens")));
    }

    @Test
    @DisplayName("Should accept valid ISBN-10 format")
    void testValidISBN10() {
        bookRequest.setTitle("Valid Title");
        bookRequest.setAuthor("Author");
        bookRequest.setIsbn("0743273567");
        bookRequest.setGenre("Fiction");
        bookRequest.setQuantity(5);

        Set<ConstraintViolation<BookRequest>> violations = validator.validate(bookRequest);
        assertTrue(violations.isEmpty(), "Valid ISBN-10 should pass");
    }

    @Test
    @DisplayName("Should accept valid ISBN-13 format")
    void testValidISBN13() {
        bookRequest.setTitle("Valid Title");
        bookRequest.setAuthor("Author");
        bookRequest.setIsbn("978-0-7432-7356-5");
        bookRequest.setGenre("Fiction");
        bookRequest.setQuantity(5);

        Set<ConstraintViolation<BookRequest>> violations = validator.validate(bookRequest);
        assertTrue(violations.isEmpty(), "Valid ISBN-13 should pass");
    }

    // ================== Genre Tests ==================

    @Test
    @DisplayName("Should reject empty genre")
    void testEmptyGenre() {
        bookRequest.setTitle("Valid Title");
        bookRequest.setAuthor("Author");
        bookRequest.setIsbn("978-0-7432-7356-5");
        bookRequest.setGenre("");
        bookRequest.setQuantity(5);

        Set<ConstraintViolation<BookRequest>> violations = validator.validate(bookRequest);
        assertFalse(violations.isEmpty(), "Empty genre should fail validation");
    }

    @Test
    @DisplayName("Should reject null genre")
    void testNullGenre() {
        bookRequest.setTitle("Valid Title");
        bookRequest.setAuthor("Author");
        bookRequest.setIsbn("978-0-7432-7356-5");
        bookRequest.setGenre(null);
        bookRequest.setQuantity(5);

        Set<ConstraintViolation<BookRequest>> violations = validator.validate(bookRequest);
        assertFalse(violations.isEmpty(), "Null genre should fail validation");
    }

    @Test
    @DisplayName("Should reject genre exceeding max length (50)")
    void testGenreExceedsMaxLength() {
        bookRequest.setTitle("Valid Title");
        bookRequest.setAuthor("Author");
        bookRequest.setIsbn("978-0-7432-7356-5");
        bookRequest.setGenre("Science".repeat(10));
        bookRequest.setQuantity(5);

        Set<ConstraintViolation<BookRequest>> violations = validator.validate(bookRequest);
        assertFalse(violations.isEmpty(), "Genre exceeding 50 chars should fail");
    }

    // ================== Quantity Tests ==================

    @Test
    @DisplayName("Should reject null quantity")
    void testNullQuantity() {
        bookRequest.setTitle("Valid Title");
        bookRequest.setAuthor("Author");
        bookRequest.setIsbn("978-0-7432-7356-5");
        bookRequest.setGenre("Fiction");
        bookRequest.setQuantity(null);

        Set<ConstraintViolation<BookRequest>> violations = validator.validate(bookRequest);
        assertFalse(violations.isEmpty(), "Null quantity should fail validation");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Quantity is required")));
    }

    @Test
    @DisplayName("Should reject quantity less than 1")
    void testQuantityLessThanOne() {
        bookRequest.setTitle("Valid Title");
        bookRequest.setAuthor("Author");
        bookRequest.setIsbn("978-0-7432-7356-5");
        bookRequest.setGenre("Fiction");
        bookRequest.setQuantity(0);

        Set<ConstraintViolation<BookRequest>> violations = validator.validate(bookRequest);
        assertFalse(violations.isEmpty(), "Quantity < 1 should fail");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("at least 1")));
    }

    @Test
    @DisplayName("Should reject negative quantity")
    void testNegativeQuantity() {
        bookRequest.setTitle("Valid Title");
        bookRequest.setAuthor("Author");
        bookRequest.setIsbn("978-0-7432-7356-5");
        bookRequest.setGenre("Fiction");
        bookRequest.setQuantity(-5);

        Set<ConstraintViolation<BookRequest>> violations = validator.validate(bookRequest);
        assertFalse(violations.isEmpty(), "Negative quantity should fail");
    }

    @Test
    @DisplayName("Should reject quantity exceeding max (30)")
    void testQuantityExceedsMax() {
        bookRequest.setTitle("Valid Title");
        bookRequest.setAuthor("Author");
        bookRequest.setIsbn("978-0-7432-7356-5");
        bookRequest.setGenre("Fiction");
        bookRequest.setQuantity(31);

        Set<ConstraintViolation<BookRequest>> violations = validator.validate(bookRequest);
        assertFalse(violations.isEmpty(), "Quantity > 30 should fail");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("cannot exceed 30")));
    }

    @Test
    @DisplayName("Should accept quantity at boundary (1)")
    void testQuantityAtMinBoundary() {
        bookRequest.setTitle("Valid Title");
        bookRequest.setAuthor("Author");
        bookRequest.setIsbn("978-0-7432-7356-5");
        bookRequest.setGenre("Fiction");
        bookRequest.setQuantity(1);

        Set<ConstraintViolation<BookRequest>> violations = validator.validate(bookRequest);
        assertTrue(violations.isEmpty(), "Quantity = 1 should pass");
    }

    @Test
    @DisplayName("Should accept quantity at boundary (30)")
    void testQuantityAtMaxBoundary() {
        bookRequest.setTitle("Valid Title");
        bookRequest.setAuthor("Author");
        bookRequest.setIsbn("978-0-7432-7356-5");
        bookRequest.setGenre("Fiction");
        bookRequest.setQuantity(30);

        Set<ConstraintViolation<BookRequest>> violations = validator.validate(bookRequest);
        assertTrue(violations.isEmpty(), "Quantity = 30 should pass");
    }

    // ================== Constructor Tests ==================

    @Test
    @DisplayName("Should create BookRequest using all-args constructor")
    void testAllArgsConstructor() {
        BookRequest book = new BookRequest(
                "The Hobbit",
                "J.R.R. Tolkien",
                "978-0-547-92822-8",
                "Fantasy",
                10
        );

        assertNotNull(book);
        assertEquals("The Hobbit", book.getTitle());
        assertEquals("J.R.R. Tolkien", book.getAuthor());
        assertEquals("978-0-547-92822-8", book.getIsbn());
        assertEquals("Fantasy", book.getGenre());
        assertEquals(10, book.getQuantity());
    }

    @Test
    @DisplayName("Should create BookRequest using no-args constructor")
    void testNoArgsConstructor() {
        BookRequest book = new BookRequest();
        assertNotNull(book);
    }

    // ================== Security Tests ==================

    @Test
    @DisplayName("Should prevent SQL injection in title")
    void testSQLInjectionInTitle() {
        bookRequest.setTitle("'; DROP TABLE books; --");
        bookRequest.setAuthor("Author");
        bookRequest.setIsbn("978-0-7432-7356-5");
        bookRequest.setGenre("Fiction");
        bookRequest.setQuantity(5);

        Set<ConstraintViolation<BookRequest>> violations = validator.validate(bookRequest);
        assertFalse(violations.isEmpty(), "SQL injection attempt should fail");
    }

    @Test
    @DisplayName("Should prevent XSS injection in author")
    void testXSSInjectionInAuthor() {
        bookRequest.setTitle("Valid Title");
        bookRequest.setAuthor("Author<script>alert('xss')</script>");
        bookRequest.setIsbn("978-0-7432-7356-5");
        bookRequest.setGenre("Fiction");
        bookRequest.setQuantity(5);

        Set<ConstraintViolation<BookRequest>> violations = validator.validate(bookRequest);
        assertFalse(violations.isEmpty(), "XSS injection attempt should fail");
    }
}
