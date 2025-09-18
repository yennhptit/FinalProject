package org.example.model;
import lombok.*;
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private Long id;
    private Book book;
    private int quantity;
    private User user;
    private boolean canceled = false;
}
