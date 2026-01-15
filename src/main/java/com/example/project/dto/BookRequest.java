package com.example.project.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class BookRequest {
    
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Author is required")
    private String author;
    
    @NotBlank(message = "ISBN is required")
    private String isbn;
    
    @NotBlank(message = "Genre is required")
    private String genre;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}