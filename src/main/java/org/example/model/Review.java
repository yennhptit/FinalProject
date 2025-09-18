package org.example.model;

import java.time.LocalDateTime;
import lombok.*;
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Review {
    private Long id;
    private User user;
    private Book book;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
}
