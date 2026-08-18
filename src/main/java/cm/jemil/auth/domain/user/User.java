package cm.jemil.auth.domain.user;

import cm.jemil.shared.utils.CreatedAt;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class User {
    private final UserId id;
    private String email;
    private String passwordHash;
    private String phoneNumber;
    private Set<UserRole> roles;
    private boolean active;
    private CreatedAt createdAt;

    public static User create(String email, String passwordHash, String phoneNumber, UserRole role) {
        return new User(
                UserId.generate(),
                email,
                passwordHash,
                phoneNumber,
                new HashSet<>(Set.of(role)),
                true,
                CreatedAt.now());
    }

    public static User create(String email, String passwordHash, String phoneNumber, Set<UserRole> roles) {
        return new User(
                UserId.generate(), email, passwordHash, phoneNumber, new HashSet<>(roles), true, CreatedAt.now());
    }

    public void deactivate() {
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }

    public void addRole(UserRole role) {
        this.roles.add(role);
    }

    public void removeRole(UserRole role) {
        this.roles.remove(role);
    }

    public boolean hasRole(UserRole role) {
        return this.roles.contains(role);
    }
}
