package org.example.repository;
import org.example.model.*;
import java.util.List;
import java.util.Optional;
public interface ReviewRepository {
    Optional<Review> findById(Long id);
    Review save(Review review);
    List<Review> findByBook(Book book);
}
