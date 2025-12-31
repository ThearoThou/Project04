package com.example.project.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "borrow_records")
@Data
@NoArgsConstructor
public class BorrowRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
    
    @Column(name = "borrow_date", nullable = false)
    private LocalDate borrowDate;
    
    @Column(name = "return_deadline", nullable = false)
    private LocalDate returnDeadline;
    
    @Column(name = "actual_return_date")
    private LocalDate actualReturnDate;
    
    @Column(name = "status", length = 20, nullable = false)
    private String status; // BORROWED, RETURNED, OVERDUE
    
    public BorrowRecord(User user, Book book, LocalDate borrowDate, LocalDate returnDeadline) {
        this.user = user;
        this.book = book;
        this.borrowDate = borrowDate;
        this.returnDeadline = returnDeadline;
        this.status = "BORROWED";
    }
}