package com.example.project.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating and updating books in the library system.
 * Includes comprehensive validation for security and data integrity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookRequest {
    
    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 70, message = "Title must be between 1 and 70 characters")
    @Pattern(regexp = "^[a-zA-Z0-9\\s\\-._,':()&]+$", message = "Title contains invalid characters")
    private String title;
    
    @NotBlank(message = "Author is required")
    @Size(min = 1, max = 70, message = "Author must be between 1 and 70 characters")
    @Pattern(regexp = "^[a-zA-Z\\s\\-._',()&]+$", message = "Author contains invalid characters")
    private String author;
    
    @NotBlank(message = "ISBN is required")
    @Size(min = 10, max = 17, message = "ISBN must be between 10 and 17 characters")
    @Pattern(regexp = "^[0-9\\-]+$", message = "ISBN must contain only numbers and hyphens")
    private String isbn;
    
    @NotBlank(message = "Genre is required")
    @Size(min = 1, max = 50, message = "Genre must be between 1 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s\\-._',()&]+$", message = "Genre contains invalid characters")
    private String genre;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 30, message = "Quantity cannot exceed 30")
    private Integer quantity;
}