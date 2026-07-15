package cm.jemil.auth.adapter.inbound.rest.dto;

import cm.jemil.auth.domain.user.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.Set;

public record RegisterRequest(
        @NotBlank @Email String email,
        @NotBlank String password,
        String phoneNumber,
        @NotEmpty Set<UserRole> roles) {}
