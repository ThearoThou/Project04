package com.example.project.service;

import com.example.project.dto.BookRequest;
import com.example.project.entity.Book;
import com.example.project.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("BookService Integration Tests")
class BookServiceIntegrationTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

    private BookRequest validBookRequest;

    @BeforeEach
    void setUp() {
        // Clear database before each test
        bookRepository.deleteAll();

        // Setup valid book request
        validBookRequest = new BookRequest(
                "The Great Gatsby",
                "F. Scott Fitzgerald",
                "978-0-7432-7356-5",
                "Fiction",
                5
        );
    }

    // ================== Save Book Tests ==================

    @Test
    @DisplayName("Should save book successfully with valid BookRequest")
    void testSaveBookWithValidRequest() {
        Book book = new Book(
                validBookRequest.getTitle(),
                validBookRequest.getAuthor(),
                validBookRequest.getIsbn(),
                validBookRequest.getGenre(),
                validBookRequest.getQuantity()
        );

        Book savedBook = bookService.saveBook(book);

        assertNotNull(savedBook.getId(), "Saved book should have an ID");
        assertEquals(validBookRequest.getTitle(), savedBook.getTitle());
        assertEquals(validBookRequest.getAuthor(), savedBook.getAuthor());
        assertEquals(validBookRequest.getIsbn(), savedBook.getIsbn());
        assertEquals(validBookRequest.getGenre(), savedBook.getGenre());
        assertEquals(validBookRequest.getQuantity(), savedBook.getQuantity());
    }

    @Test
    @DisplayName("Should retrieve saved book by ID")
    void testGetBookById() {
        Book book = new Book(
                validBookRequest.getTitle(),
                validBookRequest.getAuthor(),
                validBookRequest.getIsbn(),
                validBookRequest.getGenre(),
                validBookRequest.getQuantity()
        );
        Book savedBook = bookService.saveBook(book);

        Optional<Book> retrievedBook = bookService.getBookById(savedBook.getId());

        assertTrue(retrievedBook.isPresent(), "Book should be found by ID");
        assertEquals(validBookRequest.getTitle(), retrievedBook.get().getTitle());
    }

    @Test
    @DisplayName("Should return empty Optional when book not found")
    void testGetBookByIdNotFound() {
        Optional<Book> retrievedBook = bookService.getBookById(999L);

        assertFalse(retrievedBook.isPresent(), "Book should not be found");
    }

    // ================== Get All Books Tests ==================

    @Test
    @DisplayName("Should return all books")
    void testGetAllBooks() {
        Book book1 = new Book(validBookRequest.getTitle(), validBookRequest.getAuthor(),
                validBookRequest.getIsbn(), validBookRequest.getGenre(), validBookRequest.getQuantity());
        Book book2 = new Book("1984", "George Orwell", "978-0-451-52494-2", "Dystopia", 3);

        bookService.saveBook(book1);
        bookService.saveBook(book2);

        List<Book> books = bookService.getAllBooks();

        assertEquals(2, books.size(), "Should have 2 books");
    }

    @Test
    @DisplayName("Should return empty list when no books exist")
    void testGetAllBooksEmpty() {
        List<Book> books = bookService.getAllBooks();

        assertTrue(books.isEmpty(), "Book list should be empty");
    }

    // ================== Update Book Tests ==================

    @Test
    @DisplayName("Should update book successfully")
    void testUpdateBook() {
        Book book = new Book(
                validBookRequest.getTitle(),
                validBookRequest.getAuthor(),
                validBookRequest.getIsbn(),
                validBookRequest.getGenre(),
                validBookRequest.getQuantity()
        );
        Book savedBook = bookService.saveBook(book);

        Book updateDetails = new Book(
                "Updated Title",
                "Updated Author",
                "978-0-7432-7356-5",
                "Science Fiction",
                10
        );

        Book updatedBook = bookService.updateBook(savedBook.getId(), updateDetails);

        assertEquals("Updated Title", updatedBook.getTitle());
        assertEquals("Updated Author", updatedBook.getAuthor());
        assertEquals("Science Fiction", updatedBook.getGenre());
        assertEquals(10, updatedBook.getQuantity());
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent book")
    void testUpdateNonExistentBook() {
        Book updateDetails = new Book(
                "Updated Title",
                "Updated Author",
                "978-0-7432-7356-5",
                "Science Fiction",
                10
        );

        assertThrows(RuntimeException.class, () -> {
            bookService.updateBook(999L, updateDetails);
        }, "Should throw exception for non-existent book");
    }

    // ================== Delete Book Tests ==================

    @Test
    @DisplayName("Should delete book successfully")
    void testDeleteBook() {
        Book book = new Book(
                validBookRequest.getTitle(),
                validBookRequest.getAuthor(),
                validBookRequest.getIsbn(),
                validBookRequest.getGenre(),
                validBookRequest.getQuantity()
        );
        Book savedBook = bookService.saveBook(book);

        bookService.deleteBook(savedBook.getId());

        Optional<Book> deletedBook = bookService.getBookById(savedBook.getId());
        assertFalse(deletedBook.isPresent(), "Book should be deleted");
    }

    // ================== Search Books Tests ==================

    @Test
    @DisplayName("Should search books by title")
    void testSearchBooksByTitle() {
        Book book1 = new Book(validBookRequest.getTitle(), validBookRequest.getAuthor(),
                validBookRequest.getIsbn(), validBookRequest.getGenre(), validBookRequest.getQuantity());
        Book book2 = new Book("1984", "George Orwell", "978-0-451-52494-2", "Dystopia", 3);

        bookService.saveBook(book1);
        bookService.saveBook(book2);

        List<Book> results = bookService.searchBooks("Great");

        assertEquals(1, results.size(), "Should find 1 book");
        assertEquals(validBookRequest.getTitle(), results.get(0).getTitle());
    }

    @Test
    @DisplayName("Should search books by author")
    void testSearchBooksByAuthor() {
        Book book1 = new Book(validBookRequest.getTitle(), validBookRequest.getAuthor(),
                validBookRequest.getIsbn(), validBookRequest.getGenre(), validBookRequest.getQuantity());
        Book book2 = new Book("1984", "George Orwell", "978-0-451-52494-2", "Dystopia", 3);

        bookService.saveBook(book1);
        bookService.saveBook(book2);

        List<Book> results = bookService.searchBooks("Orwell");

        assertEquals(1, results.size(), "Should find 1 book");
        assertEquals("George Orwell", results.get(0).getAuthor());
    }

    @Test
    @DisplayName("Should return empty list when no books match search")
    void testSearchBooksNoMatch() {
        Book book1 = new Book(validBookRequest.getTitle(), validBookRequest.getAuthor(),
                validBookRequest.getIsbn(), validBookRequest.getGenre(), validBookRequest.getQuantity());
        bookService.saveBook(book1);

        List<Book> results = bookService.searchBooks("NonExistent");

        assertTrue(results.isEmpty(), "Should return empty list");
    }

    // ================== Availability Tests ==================

    @Test
    @DisplayName("Should update book availability")
    void testUpdateAvailability() {
        Book book = new Book(
                validBookRequest.getTitle(),
                validBookRequest.getAuthor(),
                validBookRequest.getIsbn(),
                validBookRequest.getGenre(),
                validBookRequest.getQuantity()
        );
        Book savedBook = bookService.saveBook(book);

        bookService.updateAvailability(savedBook.getId(), false);

        Optional<Book> updatedBook = bookService.getBookById(savedBook.getId());
        assertFalse(updatedBook.get().getIsAvailable(), "Book should be unavailable");
    }

    @Test
    @DisplayName("Should get available books only")
    void testGetAvailableBooks() {
        Book book1 = new Book(validBookRequest.getTitle(), validBookRequest.getAuthor(),
                validBookRequest.getIsbn(), validBookRequest.getGenre(), validBookRequest.getQuantity());
        book1.setIsAvailable(true);

        Book book2 = new Book("1984", "George Orwell", "978-0-451-52494-2", "Dystopia", 3);
        book2.setIsAvailable(false);

        bookService.saveBook(book1);
        bookService.saveBook(book2);

        List<Book> availableBooks = bookService.getAvailableBooks();

        assertEquals(1, availableBooks.size(), "Should have 1 available book");
        assertEquals(validBookRequest.getTitle(), availableBooks.get(0).getTitle());
    }
}
