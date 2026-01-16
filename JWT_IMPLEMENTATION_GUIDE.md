# JWT & DTO Implementation Guide

## Summary of Changes

I've successfully implemented **JWT authentication** and **DTO validation** in your Library Management System. Here's what was added:

---

## 1. Dependencies Added

**File:** `build.gradle`

```gradle
implementation 'io.jsonwebtoken:jjwt-api:0.12.3'
runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.12.3'
runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.12.3'
```

---

## 2. DTOs Created (with Validation)

### **LoginRequest.java**
```java
@NotBlank(message = "Username is required")
private String username;

@NotBlank(message = "Password is required")
@Size(min = 6, message = "Password must be at least 6 characters")
private String password;
```

### **RegisterRequest.java**
```java
@NotBlank(message = "Username is required")
@Size(min = 3, max = 50, message = "Username must be 3-50 characters")
private String username;

@NotBlank(message = "Full name is required")
@Size(min = 2, max = 100, message = "Full name must be 2-100 characters")
private String fullName;

@NotBlank(message = "Email is required")
@Email(message = "Email must be valid")
private String email;

@NotBlank(message = "Password is required")
@Size(min = 6, max = 255, message = "Password must be at least 6 characters")
private String password;
```

### **BookRequest.java**
```java
@NotBlank(message = "Title is required")
@Size(min = 1, max = 255, message = "Title must not be empty")
private String title;

@NotBlank(message = "Author is required")
@Size(min = 1, max = 255, message = "Author must not be empty")
private String author;

@NotBlank(message = "ISBN is required")
@Size(min = 1, max = 255, message = "ISBN must not be empty")
private String isbn;

@NotBlank(message = "Genre is required")
@Size(min = 1, max = 255, message = "Genre must not be empty")
private String genre;

@NotNull(message = "Quantity is required")
@Min(value = 1, message = "Quantity must be at least 1")
private Integer quantity;
```

### **BorrowRequest.java**
```java
@NotNull(message = "Book ID is required")
private Long bookId;
```

### **ErrorResponse.java**
```java
private String message;
private String error;
private int status;
```

### **JwtAuthResponse.java** (Enhanced)
```java
private String token;
private String type = "Bearer";
private String username;
private String message;
```

---

## 3. JWT Security Classes

### **JwtTokenProvider.java**
- `generateToken(Authentication)` - Create JWT from authentication
- `generateTokenFromUsername(String)` - Create JWT from username
- `getUsernameFromToken(String)` - Extract username from token
- `validateToken(String)` - Verify token is valid & not expired

### **JwtAuthenticationFilter.java**
- Extracts JWT from `Authorization: Bearer <token>` header
- Validates token & sets authentication in Spring Security context
- Runs on every request before authentication

---

## 4. SecurityConfig Updates

**File:** `SecurityConfig.java`

**New Features:**
```java
@Bean
public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) 
        throws Exception {
    return authConfig.getAuthenticationManager();
}

@Bean
public JwtAuthenticationFilter jwtAuthenticationFilter() {
    return new JwtAuthenticationFilter();
}

// Added to security filter chain:
.csrf(csrf -> csrf.disable())  // Disable CSRF for API endpoints
.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
```

**Endpoints Protected:**
- `/api/auth/login` - Allowed (anyone)
- `/api/auth/register` - Allowed (anyone)
- `/api/admin/**` - Requires LIBRARIAN role
- `/api/borrow/**` - Requires MEMBER or LIBRARIAN role

---

## 5. AuthController Updates

### New API Endpoints

**POST /api/auth/login** - Get JWT Token
```
Request:
{
  "username": "admin",
  "password": "admin123"
}

Response:
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "username": "admin",
  "message": "Login successful"
}
```

**POST /api/auth/register** - Register New User
```
Request:
{
  "username": "newuser",
  "fullName": "New User",
  "email": "user@example.com",
  "password": "password123"
}

Response:
{
  "message": "Registration successful! Please login."
}
```

### Existing Endpoints (Enhanced with DTO Validation)
- **POST /register** - Now uses `RegisterRequest` DTO
- Form validation automatically applied

---

## 6. BookController Updates

### New API Endpoint

**POST /api/admin/books** - Create Book (API)
```
Request (with JWT token):
POST /api/admin/books
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
Content-Type: application/json

{
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "isbn": "0132350882",
  "genre": "Technology",
  "quantity": 5
}

Response:
{
  "message": "Book added successfully!"
}
```

### Existing Endpoints (Enhanced with DTO Validation)
- **POST /admin/books** - Now uses `BookRequest` DTO
- Form validation automatically applied

---

## 7. Configuration

**File:** `application.properties`

```properties
# JWT Configuration
jwt.secret=MyVerySecretKeyForJWTAuthenticationAndEncodingPurposes123456789
jwt.expiration=86400000  # 24 hours in milliseconds
```

---

## How to Use

### 1. For Web Form Login (Existing)
```
1. Visit http://localhost:8080/login
2. Enter credentials (admin/admin123 or user/user123)
3. Session created automatically
4. Use web application normally
```

### 2. For API with JWT (New)

**Step 1: Get Token**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

**Step 2: Use Token in Postman**
```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

**Step 3: Make API Requests**
```bash
# Borrow a book
curl -X POST http://localhost:8080/api/borrow/book/1 \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."

# Create a book (admin only)
curl -X POST http://localhost:8080/api/admin/books \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..." \
  -H "Content-Type: application/json" \
  -d '{"title":"...","author":"...","isbn":"...","genre":"...","quantity":5}'
```

---

## Testing in Postman

### 1. Login Request
```
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

Copy the token from response.

### 2. Use Token
```
GET http://localhost:8080/api/borrow/available
Authorization: Bearer <paste_token_here>
```

---

## Validation Rules

| Field | Rule | Example |
|-------|------|---------|
| **username** | Required, 3-50 chars | "admin" |
| **password** | Required, min 6 chars | "admin123" |
| **email** | Required, valid format | "user@example.com" |
| **fullName** | Required, 2-100 chars | "John Doe" |
| **title** | Required, non-empty | "Clean Code" |
| **author** | Required, non-empty | "Robert Martin" |
| **isbn** | Required, unique | "0132350882" |
| **genre** | Required, non-empty | "Technology" |
| **quantity** | Required, min 1 | 5 |

---

## Security Features

✅ **JWT Token** - Stateless, scalable authentication  
✅ **Token Validation** - Signature verification  
✅ **Token Expiration** - 24-hour lifetime  
✅ **DTO Validation** - Input validation on all fields  
✅ **BindingResult** - Validation error handling  
✅ **Role-Based Access** - LIBRARIAN vs MEMBER  
✅ **CSRF Disabled** - For API endpoints  
✅ **Dual Auth** - Form login (web) + JWT (API)  

---

## Architecture Flow

### Form-Based Login (Web)
```
User Form → AuthController (/register) → DTO Validation → 
Database → Redirect to Login
```

### JWT Login (API)
```
JSON Request → AuthController (/api/auth/login) → 
DTO Validation → AuthenticationManager → 
JwtTokenProvider.generateToken() → Response with Token
```

### Protected API Request
```
Request + JWT Token → JwtAuthenticationFilter → 
JwtTokenProvider.validateToken() → Set Authentication → 
Proceed to Controller → DTO Validation → Response
```

---

## Next Steps

1. **Test JWT endpoints** in Postman
2. **Try validation** - Submit empty fields, see error messages
3. **Create REST API clients** for mobile/frontend apps
4. **Add more API endpoints** using same pattern
5. **Monitor token expiration** - Refresh mechanism (optional)

---

## Files Modified/Created

✅ `build.gradle` - Added JWT dependencies  
✅ `SecurityConfig.java` - Updated with JWT filter & API routes  
✅ `AuthController.java` - Added JWT API endpoints  
✅ `BookController.java` - Added API endpoint with validation  
✅ `application.properties` - Added JWT config  
✅ `LoginRequest.java` - Enhanced with validation  
✅ `RegisterRequest.java` - Created with validation  
✅ `BookRequest.java` - Created with validation  
✅ `BorrowRequest.java` - Created with validation  
✅ `ErrorResponse.java` - Created for error handling  
✅ `JwtTokenProvider.java` - Already created (complete)  
✅ `JwtAuthenticationFilter.java` - Already created (complete)  

---

**Your project now supports both traditional form-based login AND modern JWT API authentication!** 🎉
