package org.example.service;

import org.example.model.Review;
import java.util.List;

public interface ReviewService {
    Review addReview(Long userId, Long bookId, int rating, String comment);
    List<Review> getReviewsForBook(Long bookId);
}
