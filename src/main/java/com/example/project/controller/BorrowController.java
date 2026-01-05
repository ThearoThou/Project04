package com.example.project.controller;

import com.example.project.entity.Book;
import com.example.project.entity.User;
import com.example.project.repository.UserRepository;
import com.example.project.service.BookService;
import com.example.project.service.BorrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class BorrowController {
    
    private final BorrowService borrowService;
    private final BookService bookService;
    private final UserRepository userRepository;
    
    @GetMapping("/borrow/available")
    public String showAvailableBooks(Model model) {
        model.addAttribute("books", bookService.getAvailableBooks());
        return "borrows/available";
    }
    
    @PostMapping("/borrow/book/{bookId}")
    public String borrowBook(@PathVariable Long bookId, 
                            Authentication authentication,
                            RedirectAttributes redirectAttributes) {
        try {
            User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            Book book = bookService.getBookById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
            
            borrowService.borrowBook(user, book);
            redirectAttributes.addFlashAttribute("success", "Book borrowed successfully! Return deadline is 14 days.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error borrowing book: " + e.getMessage());
        }
        
        return "redirect:/borrow/available";
    }
    
    @GetMapping("/borrow/history")
    public String showBorrowHistory(Authentication authentication, Model model) {
        User user = userRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        model.addAttribute("borrowRecords", borrowService.getUserBorrowHistory(user));
        return "borrows/history";
    }
    
    @PostMapping("/borrow/return/{recordId}")
    public String returnBook(@PathVariable Long recordId, RedirectAttributes redirectAttributes) {
        try {
            borrowService.returnBook(recordId);
            redirectAttributes.addFlashAttribute("success", "Book returned successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error returning book: " + e.getMessage());
        }
        
        return "redirect:/borrow/history";
    }
    
    @GetMapping("/admin/borrows")
    @Transactional(readOnly = true)
    public String showAllBorrows(Model model) {
        model.addAttribute("borrowRecords", borrowService.getAllBorrowRecords());
        return "borrows/admin-list";
    }
}