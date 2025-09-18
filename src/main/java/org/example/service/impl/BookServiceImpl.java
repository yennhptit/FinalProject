package org.example.service.impl;

import org.example.model.Book;
import org.example.repository.BookRepository;
import org.example.service.BookService;

import java.util.List;
import java.util.Optional;

public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Book addBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }
        return bookRepository.save(book);
    }

    @Override
    public Optional<Book> findBook(Long id) {
        return bookRepository.findById(id);
    }

    @Override
    public List<Book> listBooks() {
        return bookRepository.findAll();
    }

    @Override
    public void updateStock(Long id, int newQuantity) {
        bookRepository.findById(id).ifPresent(book -> {
            if (newQuantity < 0) {
                throw new IllegalArgumentException("Stock quantity cannot be negative");
            }
            book.setStockQuantity(newQuantity);
            bookRepository.save(book);
        });
    }

}
