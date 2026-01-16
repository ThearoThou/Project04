package com.example.project.controller;

import com.example.project.dto.LoginRequest;
import com.example.project.dto.RegisterRequest;
import com.example.project.dto.JwtAuthResponse;
import com.example.project.dto.ErrorResponse;
import com.example.project.entity.Role;
import com.example.project.entity.User;
import com.example.project.repository.RoleRepository;
import com.example.project.repository.UserRepository;
import com.example.project.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;

@Controller
@RequiredArgsConstructor
public class AuthController {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }
    
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute RegisterRequest dto, 
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> 
                redirectAttributes.addFlashAttribute("error", error.getDefaultMessage())
            );
            return "redirect:/register";
        }
        
        if (userRepository.existsByUsername(dto.getUsername())) {
            redirectAttributes.addFlashAttribute("error", "Username already exists");
            return "redirect:/register";
        }
        
        if (userRepository.existsByEmail(dto.getEmail())) {
            redirectAttributes.addFlashAttribute("error", "Email already exists");
            return "redirect:/register";
        }
        
        String email = dto.getEmail().toLowerCase().trim();
        
        if (email.contains("@admin.library.com")) {
            redirectAttributes.addFlashAttribute("error", "This email is not allowed for registration");
            return "redirect:/register";
        }

        User user = new User(dto.getUsername(), dto.getPassword(), dto.getFullName(), email);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        Role memberRole = roleRepository.findByName("MEMBER")
            .orElseThrow(() -> new RuntimeException("Member role not found"));
        user.getRoles().add(memberRole);
        
        userRepository.save(user);
        
        redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
        return "redirect:/login";
    }
    
    @PostMapping("/api/auth/login")
    public ResponseEntity<?> apiLogin(@RequestBody LoginRequest loginRequest) {
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = tokenProvider.generateToken(authentication);
            String username = authentication.getName();
            
            return ResponseEntity.ok(new JwtAuthResponse(token, username));
        } catch (Exception e) {
            System.out.println("Auth error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(401).body(
                new ErrorResponse("Invalid username or password: " + e.getMessage(), "Authentication Failed", 401)
            );
        }
    }
    
    @PostMapping("/api/auth/register")
    public ResponseEntity<?> apiRegister(@Valid @RequestBody RegisterRequest dto, 
                                         BindingResult bindingResult) {
        
        if (bindingResult.hasErrors()) {
            String error = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(new JwtAuthResponse(null, error));
        }
        
        if (userRepository.existsByUsername(dto.getUsername())) {
            return ResponseEntity.badRequest().body(
                new JwtAuthResponse(null, "Username already exists")
            );
        }
        
        if (userRepository.existsByEmail(dto.getEmail())) {
            return ResponseEntity.badRequest().body(
                new JwtAuthResponse(null, "Email already exists")
            );
        }
        
        String email = dto.getEmail().toLowerCase().trim();
        
        if (email.contains("@admin.library.com")) {
            return ResponseEntity.badRequest().body(
                new JwtAuthResponse(null, "This email is not allowed for registration")
            );
        }

        User user = new User(dto.getUsername(), dto.getPassword(), dto.getFullName(), email);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        Role memberRole = roleRepository.findByName("MEMBER")
            .orElseThrow(() -> new RuntimeException("Member role not found"));
        user.getRoles().add(memberRole);
        
        userRepository.save(user);
        
        // Generate token for newly registered user
        String token = tokenProvider.generateTokenFromUsername(dto.getUsername());
        JwtAuthResponse response = new JwtAuthResponse(token, dto.getUsername());
        response.setMessage("Registration successful!");
        return ResponseEntity.ok(response);
    }
}