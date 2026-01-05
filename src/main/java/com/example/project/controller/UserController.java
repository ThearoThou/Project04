package com.example.project.controller;

import com.example.project.entity.User;
import com.example.project.entity.BorrowRecord;
import com.example.project.repository.UserRepository;
import com.example.project.service.BorrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserRepository userRepository;
    private final BorrowService borrowService;
    
    @GetMapping("/borrow-history/{userId}")
    @Transactional(readOnly = true)
    public String showUserBorrowHistory(@PathVariable Long userId, Model model, RedirectAttributes redirectAttributes) {
        System.out.println("=== UserController: Accessing User Borrow History ===");
        System.out.println("User ID: " + userId);
        
        try {
            // Check if user exists
            User user = userRepository.findById(userId).orElse(null);
            
            if (user == null) {
                System.err.println("User not found with ID: " + userId);
                redirectAttributes.addFlashAttribute("error", "User not found with ID: " + userId);
                return "redirect:/admin/borrows";
            }
            
            System.out.println("User found: " + user.getUsername());
            System.out.println("User email: " + user.getEmail());
            
            // Initialize roles to avoid lazy loading issues
            if (user.getRoles() != null) {
                int rolesSize = user.getRoles().size();
                System.out.println("Roles loaded: " + rolesSize);
            }
            
            // Get borrow records
            List<BorrowRecord> records = borrowService.getUserBorrowHistory(user);
            System.out.println("Borrow records found: " + (records != null ? records.size() : 0));
            
            // Add to model
            model.addAttribute("user", user);
            model.addAttribute("borrowRecords", records != null ? records : List.of());
            
            System.out.println("Returning to user-history template");
            return "borrows/user-history";
            
        } catch (Exception e) {
            System.err.println("=== ERROR in UserController ===");
            System.err.println("Error message: " + e.getMessage());
            System.err.println("Error class: " + e.getClass().getName());
            e.printStackTrace();
            
            redirectAttributes.addFlashAttribute("error", "Error loading user history: " + e.getMessage());
            return "redirect:/admin/borrows";
        }
    }
}