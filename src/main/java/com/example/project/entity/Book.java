package com.example.project.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
public class Book {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false)
    private String author;
    
    @Column(unique = true, nullable = false)
    private String isbn;
    
    @Column(nullable = false)
    private String genre;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(nullable = false)
    private Boolean isAvailable = true;
    
    public Book(String title, String author, String isbn, String genre, Integer quantity) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.genre = genre;
        this.quantity = quantity;
        this.isAvailable = true;
    }
}