-- Insert sample books for testing
INSERT INTO books (title, author, isbn, genre, quantity, is_available)
VALUES 
    ('Clean Code', 'Robert C. Martin', '978-0132350884', 'Technology', 5, TRUE),
    ('The Pragmatic Programmer', 'Andrew Hunt', '978-0201616224', 'Technology', 3, TRUE),
    ('Design Patterns', 'Erich Gamma', '978-0201633610', 'Technology', 4, TRUE),
    ('1984', 'George Orwell', '978-0451524935', 'Fiction', 10, TRUE),
    ('To Kill a Mockingbird', 'Harper Lee', '978-0061120084', 'Fiction', 8, TRUE),
    ('The Great Gatsby', 'F. Scott Fitzgerald', '978-0743273565', 'Fiction', 6, TRUE),
    ('Sapiens', 'Yuval Noah Harari', '978-0062316097', 'History', 7, TRUE),
    ('Educated', 'Tara Westover', '978-0399590504', 'Biography', 5, TRUE),
    ('Atomic Habits', 'James Clear', '978-0735211292', 'Self-Help', 12, TRUE),
    ('The Lean Startup', 'Eric Ries', '978-0307887894', 'Technology', 4, TRUE);