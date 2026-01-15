# API Endpoints Documentation

## Overview
Your Library Management System has the following endpoints organized by feature:

---

## 1. Authentication Endpoints

### POST `/api/auth/login`
**Description:** Authenticate user and receive JWT token

**Request Body:**
```json
{
  "username": "user",
  "password": "user123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "username": "user",
  "message": "Login successful"
}
```

**Response (401 Unauthorized):**
```json
{
  "message": "Invalid username or password: ...",
  "error": "Authentication Failed",
  "status": 401
}
```

**Auth Required:** No
**Role Required:** None

---

### POST `/api/auth/register`
**Description:** Register new user and receive JWT token

**Request Body:**
```json
{
  "username": "newuser",
  "password": "pass123",
  "fullName": "New User",
  "email": "newuser@gmail.com"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "username": "newuser",
  "message": "Registration successful!"
}
```

**Response (400 Bad Request):**
```json
{
  "token": null,
  "type": "Bearer",
  "username": null,
  "message": "Username already exists"
}
```

**Auth Required:** No
**Role Required:** None

---

## 2. HTML Form Endpoints (Web UI)

### GET `/login`
**Description:** Display login form page

**Response:** HTML login page
**Auth Required:** No

---

### GET `/register`
**Description:** Display registration form page

**Response:** HTML registration page
**Auth Required:** No

---

### POST `/register`
**Description:** Register user via HTML form (redirects to login on success)

**Form Data:**
```
username=newuser
password=pass123
fullName=New User
email=newuser@gmail.com
```

**Response:** Redirect to `/login` with success message
**Auth Required:** No

---

## 3. Book Management Endpoints

### GET `/`
**Description:** Display home/index page with all books

**Response:** HTML index page showing all books
**Auth Required:** Yes (any authenticated user)
**Role Required:** None

---

### GET `/admin/books`
**Description:** List all books in admin panel

**Response:** HTML page with book list
**Auth Required:** Yes
**Role Required:** LIBRARIAN

---

### GET `/admin/books/new`
**Description:** Display form to create new book

**Response:** HTML book creation form
**Auth Required:** Yes
**Role Required:** LIBRARIAN

---

### POST `/admin/books`
**Description:** Create new book via HTML form

**Form Data:**
```
title=The Great Gatsby
author=F. Scott Fitzgerald
isbn=978-0743273565
genre=Fiction
quantity=5
```

**Response:** Redirect to `/admin/books` with success message
**Auth Required:** Yes
**Role Required:** LIBRARIAN

---

### GET `/admin/books/edit/{id}`
**Description:** Display form to edit existing book

**Path Parameters:**
- `id` (required): Book ID

**Response:** HTML book edit form
**Auth Required:** Yes
**Role Required:** LIBRARIAN

---

### POST `/admin/books/{id}`
**Description:** Update existing book

**Path Parameters:**
- `id` (required): Book ID

**Form Data:**
```
title=Updated Title
author=Updated Author
isbn=Updated ISBN
genre=Updated Genre
quantity=10
```

**Response:** Redirect to `/admin/books` with success message
**Auth Required:** Yes
**Role Required:** LIBRARIAN

---

### POST `/admin/books/delete/{id}`
**Description:** Delete a book

**Path Parameters:**
- `id` (required): Book ID

**Response:** Redirect to `/admin/books` with success message
**Auth Required:** Yes
**Role Required:** LIBRARIAN

---

### GET `/books/search`
**Description:** Search books by keyword

**Query Parameters:**
- `keyword` (optional): Search term

**Response:** HTML page with search results
**Auth Required:** No
**Role Required:** None

---

### POST `/api/admin/books`
**Description:** Create book via API

**Request Body:**
```json
{
  "title": "1984",
  "author": "George Orwell",
  "isbn": "978-0451524935",
  "genre": "Dystopian",
  "quantity": 3
}
```

**Response (200 OK):**
```json
"Book added successfully!"
```

**Response (400 Bad Request):**
```json
"Title must not be empty"
```

**Auth Required:** Yes
**Role Required:** LIBRARIAN

---

## 4. Borrow/Return Endpoints

### GET `/borrow/available`
**Description:** Display available books for borrowing

**Response:** HTML page with available books
**Auth Required:** Yes (MEMBER or LIBRARIAN)
**Role Required:** MEMBER or LIBRARIAN

---

### POST `/borrow/book/{bookId}`
**Description:** Borrow a book

**Path Parameters:**
- `bookId` (required): Book ID to borrow

**Response:** Redirect to `/borrow/available` with success message
**Auth Required:** Yes
**Role Required:** MEMBER or LIBRARIAN

**Success Message:**
```
Book borrowed successfully! Return deadline is 14 days.
```

---

### GET `/borrow/history`
**Description:** View current user's borrow history

**Response:** HTML page with user's borrow records
**Auth Required:** Yes
**Role Required:** MEMBER or LIBRARIAN

---

### POST `/borrow/return/{recordId}`
**Description:** Return a borrowed book

**Path Parameters:**
- `recordId` (required): Borrow record ID

**Response:** Redirect to `/borrow/history` with success message
**Auth Required:** Yes
**Role Required:** MEMBER or LIBRARIAN

---

### GET `/admin/borrows`
**Description:** View all borrow records (admin view)

**Response:** HTML page with all borrow records
**Auth Required:** Yes
**Role Required:** LIBRARIAN

---

## 5. User Management Endpoints

### GET `/admin/users/borrow-history/{userId}`
**Description:** View specific user's borrow history

**Path Parameters:**
- `userId` (required): User ID

**Response:** HTML page with user's borrow records
**Auth Required:** Yes
**Role Required:** LIBRARIAN

---

## Authentication Requirements Summary

| Endpoint Type | Requires JWT | Requires Login | Roles |
|---------------|-------------|----------------|-------|
| Authentication | No | No | - |
| Admin Books | Yes (API) | Yes | LIBRARIAN |
| User Books | No | No | - |
| Borrowing | Yes | Yes | MEMBER, LIBRARIAN |
| Admin Management | Yes | Yes | LIBRARIAN |

---

## Using JWT Token

All authenticated endpoints accept JWT tokens in the `Authorization` header:

```
Authorization: Bearer {token}
```

Example cURL request:
```bash
curl -X GET http://localhost:8080/api/admin/books \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."
```

---

## Demo Accounts

**Admin (Librarian)**
- Username: `admin`
- Password: `admin123`
- Role: LIBRARIAN

**Regular User (Member)**
- Username: `user`
- Password: `user123`
- Role: MEMBER

---

## Error Handling

All endpoints return appropriate HTTP status codes:

| Status Code | Meaning |
|------------|---------|
| 200 | Success |
| 400 | Bad Request (validation error) |
| 401 | Unauthorized (invalid credentials) |
| 403 | Forbidden (insufficient permissions) |
| 404 | Not Found |
| 500 | Server Error |

---

## Quick Testing with cURL

### 1. Login and Get Token
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user","password":"user123"}'
```

### 2. Register New User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username":"testuser",
    "password":"test123",
    "fullName":"Test User",
    "email":"test@example.com"
  }'
```

### 3. Create Book (Admin Only)
```bash
TOKEN="your-jwt-token-here"
curl -X POST http://localhost:8080/api/admin/books \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title":"Test Book",
    "author":"Test Author",
    "isbn":"123456789",
    "genre":"Test",
    "quantity":5
  }'
```

---

## Endpoint Status

✅ All endpoints are functioning
✅ JWT authentication is now fixed
✅ Role-based authorization working
✅ Form validation enabled
