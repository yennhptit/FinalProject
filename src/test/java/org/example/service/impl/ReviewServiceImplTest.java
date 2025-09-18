package org.example.service.impl;

import org.example.model.*;
import org.example.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

class ReviewServiceImplTest {

    private ReviewRepository reviewRepository;
    private UserRepository userRepository;
    private BookRepository bookRepository;
    private ReviewServiceImpl reviewService;

    private User user;
    private Book book;

    @BeforeEach
    void setup() {
        reviewRepository = mock(ReviewRepository.class);
        userRepository = mock(UserRepository.class);
        bookRepository = mock(BookRepository.class);

        reviewService = new ReviewServiceImpl(reviewRepository, userRepository, bookRepository);

        user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        book = new Book();
        book.setId(10L);
        book.setTitle("Test Book");
    }

    @Test
    void testAddReviewSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Review review = reviewService.addReview(1L, 10L, 5, "Excellent!");

        assertThat(review, is(notNullValue()));
        assertThat(review.getUser(), is(user));
        assertThat(review.getBook(), is(book));
        assertThat(review.getRating(), is(5));
        assertThat(review.getComment(), is("Excellent!"));
        assertThat(review.getCreatedAt(), is(notNullValue()));

        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void testAddReview_UserNotLoggedInThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> reviewService.addReview(1L, 10L, 5, "Excellent!")
        );

        assertThat(ex.getMessage(), is("User must login"));

        // Không gọi repository khác
        verify(bookRepository, never()).findById(anyLong());
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void testGetReviewsForBook() {
        List<Review> reviewList = new ArrayList<>();
        Review r1 = new Review();
        r1.setBook(book);
        r1.setUser(user);
        r1.setRating(4);
        r1.setComment("Good book");
        r1.setCreatedAt(LocalDateTime.now());
        reviewList.add(r1);

        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        when(reviewRepository.findByBook(book)).thenReturn(reviewList);

        List<Review> reviews = reviewService.getReviewsForBook(10L);

        assertThat(reviews, hasSize(1));
        assertThat(reviews.get(0).getComment(), is("Good book"));
        assertThat(reviews.get(0).getRating(), is(4));

        verify(reviewRepository, times(1)).findByBook(book);
    }
}
