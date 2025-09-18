package org.example.service.impl;

import org.example.model.Book;
import org.example.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

class BookServiceImplTest {

    private BookRepository bookRepository;
    private BookServiceImpl bookService;

    private Book book1;
    private Book book2;

    @BeforeEach
    void setup() {
        bookRepository = mock(BookRepository.class);
        bookService = new BookServiceImpl(bookRepository);

        book1 = new Book();
        book1.setId(1L);
        book1.setTitle("Book One");
        book1.setStockQuantity(10);
        book1.setPrice(100);

        book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Book Two");
        book2.setStockQuantity(5);
        book2.setPrice(200);
    }

    @Test
    void testAddBook_Success() {
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Book savedBook = bookService.addBook(book1);

        assertThat(savedBook, is(notNullValue()));
        assertThat(savedBook.getTitle(), is("Book One"));
        verify(bookRepository, times(1)).save(book1);
    }

    @Test
    void testAddBook_NullBookThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> bookService.addBook(null));
        verify(bookRepository, never()).save(any());
    }

    @Test
    void testFindBook_Found() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book1));

        Optional<Book> found = bookService.findBook(1L);

        assertThat(found, is(Optional.of(book1)));
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void testFindBook_NotFound() {
        when(bookRepository.findById(3L)).thenReturn(Optional.empty());

        Optional<Book> found = bookService.findBook(3L);

        assertThat(found, is(Optional.empty()));
        verify(bookRepository, times(1)).findById(3L);
    }

    @Test
    void testListBooks_Success() {
        when(bookRepository.findAll()).thenReturn(Arrays.asList(book1, book2));

        List<Book> books = bookService.listBooks();

        assertThat(books, hasSize(2));
        assertThat(books, containsInAnyOrder(book1, book2));
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void testListBooks_Empty() {
        when(bookRepository.findAll()).thenReturn(List.of());

        List<Book> books = bookService.listBooks();

        assertThat(books, is(empty()));
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void testUpdateStock_Success() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book1));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        bookService.updateStock(1L, 20);

        assertThat(book1.getStockQuantity(), is(20));
        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).save(book1);
    }

    @Test
    void testUpdateStock_BookNotFound() {
        when(bookRepository.findById(5L)).thenReturn(Optional.empty());

        bookService.updateStock(5L, 50);

        verify(bookRepository, times(1)).findById(5L);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void testUpdateStock_NegativeQuantityThrowsException() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book1));

        assertThrows(IllegalArgumentException.class, () -> bookService.updateStock(1L, -10));

        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, never()).save(any(Book.class));
    }
}
