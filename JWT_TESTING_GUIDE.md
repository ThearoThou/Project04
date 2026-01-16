# JWT Testing Guide

## What Was Fixed

Your JWT implementation had **3 critical issues** that have been fixed:

### Issue 1: Wrong Username in Login Response
**File:** `AuthController.java` (Line 104)
- **Problem:** Was using `loginRequest.getUsername()` instead of getting the actual authenticated username
- **Fix:** Changed to `authentication.getName()` to get the correct username from the authentication object

### Issue 2: No Token Generated on Registration
**File:** `AuthController.java` (Line 153)
- **Problem:** API register was returning `null` for the token, so users couldn't login immediately after registration
- **Fix:** Now generates a JWT token using `tokenProvider.generateTokenFromUsername(dto.getUsername())` on successful registration

### Issue 3: Improved JWT Filter Logging
**File:** `JwtAuthenticationFilter.java`
- **Problem:** Missing proper logging for debugging JWT validation issues
- **Fix:** Added `@Slf4j` annotation and better debug logging

---

## Testing Your JWT Implementation

### Test 1: API Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user",
    "password": "user123"
  }'
```

**Expected Response:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "username": "user",
  "message": "Login successful"
}
```

### Test 2: API Register (New User)
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "password": "pass123",
    "fullName": "New User",
    "email": "newuser@gmail.com"
  }'
```

**Expected Response:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "username": "newuser",
  "message": "Registration successful!"
}
```

### Test 3: Using JWT Token to Access Protected API
```bash
# Get the token from login
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user",
    "password": "user123"
  }' | jq -r '.token')

# Use the token to access a protected endpoint
curl -X GET http://localhost:8080/api/protected-endpoint \
  -H "Authorization: Bearer $TOKEN"
```

---

## Configuration

Your JWT configuration is set in `application.properties`:

```properties
jwt.secret=MyVerySecretKeyForJWTAuthenticationAndEncodingPurposes123456789
jwt.expiration=86400000  # 24 hours in milliseconds
```

**Change the secret to a strong, unique value in production!**

---

## How JWT Works in Your App

1. **User logs in** → Receives JWT token from `/api/auth/login`
2. **Client stores token** → Save it in localStorage or sessionStorage
3. **Client sends token** → Include in `Authorization: Bearer {token}` header
4. **Server validates token** → `JwtAuthenticationFilter` validates and sets SecurityContext
5. **Access granted** → Authenticated requests proceed

---

## What Each Component Does

| Component | Purpose |
|-----------|---------|
| `JwtTokenProvider` | Generates, validates, and extracts claims from JWT tokens |
| `JwtAuthenticationFilter` | Intercepts requests, extracts token, validates it, and sets authentication |
| `AuthController` | Handles login/register and returns JWT tokens |
| `SecurityConfig` | Configures security rules and adds JWT filter |

---

## Debugging Tips

If JWT still isn't working:

1. **Check logs** for any error messages from `JwtAuthenticationFilter`
2. **Verify token format** - Make sure Authorization header is `Bearer {token}`
3. **Check token expiration** - Default is 24 hours
4. **Verify secret key** - Must be at least 256 bits for HS512
5. **Check UserDetailsService** - Ensure it loads users correctly

---

## API Endpoints

| Method | Endpoint | Auth Required | Purpose |
|--------|----------|---------------|---------|
| POST | `/api/auth/login` | No | Get JWT token |
| POST | `/api/auth/register` | No | Register and get JWT token |
| POST | `/admin/books` | Yes (LIBRARIAN) | Create book |
| GET | `/borrow/available` | Yes (MEMBER) | View available books |
| POST | `/borrow/book/{id}` | Yes (MEMBER) | Borrow a book |

