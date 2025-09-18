package org.example.model;
import lombok.*;
import org.example.model.enums.UserRole;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@RequiredArgsConstructor
public class User {
    private String username;
    private String password;
    private String email;
    private Long id;
    private String fullName;
    private String address;
    private String phone;
    private UserRole role;
}

