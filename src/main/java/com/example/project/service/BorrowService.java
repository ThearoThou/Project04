package com.example.project.service;

import com.example.project.entity.Book;
import com.example.project.entity.BorrowRecord;
import com.example.project.entity.User;
import com.example.project.repository.BorrowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BorrowService {
    
    private final BorrowRepository borrowRepository;
    private final BookService bookService;
    
    @Transactional
    public BorrowRecord borrowBook(User user, Book book) {
        if (!book.getIsAvailable()) {
            throw new RuntimeException("Book is not available");
        }
        
        // Set book as unavailable
        book.setIsAvailable(false);
        bookService.saveBook(book);
        
        // Create borrow record with 14-day return deadline
        LocalDate borrowDate = LocalDate.now();
        LocalDate returnDeadline = borrowDate.plusDays(14);
        
        BorrowRecord borrowRecord = new BorrowRecord(user, book, borrowDate, returnDeadline);
        return borrowRepository.save(borrowRecord);
    }
    
    @Transactional
    public BorrowRecord returnBook(Long borrowRecordId) {
        BorrowRecord record = borrowRepository.findById(borrowRecordId)
            .orElseThrow(() -> new RuntimeException("Borrow record not found"));
        
        // Set actual return date
        record.setActualReturnDate(LocalDate.now());
        
        // Update status based on return deadline
        if (LocalDate.now().isAfter(record.getReturnDeadline())) {
            record.setStatus("RETURNED_LATE");
        } else {
            record.setStatus("RETURNED");
        }
        
        // Make book available again
        Book book = record.getBook();
        book.setIsAvailable(true);
        bookService.saveBook(book);
        
        return borrowRepository.save(record);
    }
    
    public List<BorrowRecord> getUserBorrowHistory(User user) {
        return borrowRepository.findByUserOrderByBorrowDateDesc(user);
    }
    
    public List<BorrowRecord> getAllBorrowRecords() {
        return borrowRepository.findAll();
    }
    
    public List<BorrowRecord> getActiveBorrows() {
        return borrowRepository.findByStatus("BORROWED");
    }
    
    @Transactional
    public void updateOverdueRecords() {
        List<BorrowRecord> activeRecords = borrowRepository.findByStatus("BORROWED");
        LocalDate today = LocalDate.now();
        
        for (BorrowRecord record : activeRecords) {
            if (today.isAfter(record.getReturnDeadline())) {
                record.setStatus("OVERDUE");
                borrowRepository.save(record);
            }
        }
    }
}