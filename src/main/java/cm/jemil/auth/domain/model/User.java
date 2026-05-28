package cm.jemil.auth.domain.model;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User {
    private final UserId id;
    private final String email;
    private final String passwordHash;
    private final String phone;
    private final UserRole role;
    private final LocalDateTime createdAt;

    public static User register(UserId id, String email, String passwordHash, String phone, UserRole role) {
        return User.builder()
                .id(id)
                .email(email)
                .passwordHash(passwordHash)
                .phone(phone)
                .role(role)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
