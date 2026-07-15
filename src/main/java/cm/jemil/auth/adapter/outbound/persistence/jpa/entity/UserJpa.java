package cm.jemil.auth.adapter.outbound.persistence.jpa.entity;

import cm.jemil.auth.domain.user.UserRole;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_user")
public class UserJpa {
    @Id
    @Column(name = "c_id")
    private UUID id;

    @Column(name = "c_email", nullable = false, unique = true)
    private String email;

    @Column(name = "c_password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "c_phone_number")
    private String phoneNumber;

    @ElementCollection
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "c_id"))
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<UserRole> roles = new HashSet<>();

    @Column(name = "c_active", nullable = false)
    private boolean active;

    @Column(name = "c_created_at", nullable = false)
    private LocalDateTime createdAt;
}
