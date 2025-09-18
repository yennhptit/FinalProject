package org.example.model.enums;
import lombok.*;
import org.example.model.CartItem;
import org.example.model.User;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    private Long id;
    private User user;
    private List<CartItem> items;
}
