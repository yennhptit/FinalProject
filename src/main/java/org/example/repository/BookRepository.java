package org.example.repository;
import org.example.model.*;
import java.util.List;
import java.util.Optional;
public interface BookRepository {
    Optional<Book> findById(Long id);
    List<Book> findAll();
    Book save(Book book);
    void delete(Long id);
}