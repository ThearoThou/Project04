package com.example.project.controller;

import com.example.project.dto.BookRequest;
import com.example.project.entity.Book;
import com.example.project.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;

@Controller
@RequiredArgsConstructor
public class BookController {
    
    private final BookService bookService;
    
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("books", bookService.getAllBooks());
        return "index";
    }
    
    @GetMapping("/admin/books")
    public String listBooks(Model model) {
        model.addAttribute("books", bookService.getAllBooks());
        return "books/list";
    }
    
    @GetMapping("/admin/books/new")
    public String showCreateForm(Model model) {
        model.addAttribute("book", new Book());
        return "books/form";
    }
    
    @PostMapping("/admin/books")
    public String createBook(@Valid @ModelAttribute BookRequest dto, 
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> 
                redirectAttributes.addFlashAttribute("error", error.getDefaultMessage())
            );
            return "redirect:/admin/books/new";
        }
        
        try {
            Book book = new Book(dto.getTitle(), dto.getAuthor(), 
                                dto.getIsbn(), dto.getGenre(), dto.getQuantity());
            bookService.saveBook(book);
            redirectAttributes.addFlashAttribute("success", "Book added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error adding book: " + e.getMessage());
        }
        return "redirect:/admin/books";
    }
    
    @GetMapping("/admin/books/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return bookService.getBookById(id)
            .map(book -> {
                model.addAttribute("book", book);
                return "books/form";
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute("error", "Book not found!");
                return "redirect:/admin/books";
            });
    }
    
    @PostMapping("/admin/books/{id}")
    public String updateBook(@PathVariable Long id, 
                            @Valid @ModelAttribute BookRequest dto,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> 
                redirectAttributes.addFlashAttribute("error", error.getDefaultMessage())
            );
            return "redirect:/admin/books/edit/" + id;
        }
        
        try {
            Book book = new Book(dto.getTitle(), dto.getAuthor(), 
                                dto.getIsbn(), dto.getGenre(), dto.getQuantity());
            bookService.updateBook(id, book);
            redirectAttributes.addFlashAttribute("success", "Book updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating book: " + e.getMessage());
        }
        return "redirect:/admin/books";
    }
    
    @PostMapping("/admin/books/delete/{id}")
    public String deleteBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            bookService.deleteBook(id);
            redirectAttributes.addFlashAttribute("success", "Book deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting book: " + e.getMessage());
        }
        return "redirect:/admin/books";
    }
    
    @GetMapping("/books/search")
    public String searchBooks(@RequestParam(required = false) String keyword, Model model) {
        if (keyword != null && !keyword.isEmpty()) {
            model.addAttribute("books", bookService.searchBooks(keyword));
        } else {
            model.addAttribute("books", bookService.getAllBooks());
        }
        model.addAttribute("keyword", keyword);
        return "books/search";
    }
    
    @PostMapping("/api/admin/books")
    public ResponseEntity<?> createBookApi(@Valid @RequestBody BookRequest dto,BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String error = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(error);
        }
        
        try {
            Book book = new Book(dto.getTitle(), dto.getAuthor(), 
                                dto.getIsbn(), dto.getGenre(), dto.getQuantity());
            bookService.saveBook(book);
            return ResponseEntity.ok("Book added successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error adding book: " + e.getMessage());
        }
    }
}