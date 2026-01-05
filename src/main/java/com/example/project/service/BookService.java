package com.example.project.service;

import com.example.project.entity.Book;
import com.example.project.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookService {
    
    private final BookRepository bookRepository;
    
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
    
    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }
    
    public List<Book> getAvailableBooks() {
        return bookRepository.findByIsAvailable(true);
    }
    
    @Transactional
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }
    
    @Transactional
    public Book updateBook(Long id, Book bookDetails) {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Book not found"));
        
        book.setTitle(bookDetails.getTitle());
        book.setAuthor(bookDetails.getAuthor());
        book.setIsbn(bookDetails.getIsbn());
        book.setGenre(bookDetails.getGenre());
        book.setQuantity(bookDetails.getQuantity());
        book.setIsAvailable(bookDetails.getIsAvailable());
        
        return bookRepository.save(book);
    }
    
    @Transactional
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }
    
    public List<Book> searchBooks(String keyword) {
        return bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(keyword, keyword);
    }
    
    @Transactional
    public void updateAvailability(Long bookId, boolean isAvailable) {
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new RuntimeException("Book not found"));
        book.setIsAvailable(isAvailable);
        bookRepository.save(book);
    }
}