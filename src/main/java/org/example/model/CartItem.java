package org.example.model;
import lombok.*;
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    private Long id;
    private Book book;
    private int quantity;
}