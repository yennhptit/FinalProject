package org.example.service;

import org.example.model.Book;
import java.util.List;
import java.util.Optional;

public interface BookService {
    Book addBook(Book book);
    Optional<Book> findBook(Long id);
    List<Book> listBooks();
    void updateStock(Long bookId, int newStock);
}
