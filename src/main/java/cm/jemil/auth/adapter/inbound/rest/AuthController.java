package cm.jemil.auth.adapter.inbound.rest;

import cm.jemil.auth.adapter.inbound.rest.dto.LoginRequest;
import cm.jemil.auth.adapter.inbound.rest.dto.LoginResponse;
import cm.jemil.auth.adapter.inbound.rest.dto.RefreshTokenRequest;
import cm.jemil.auth.adapter.inbound.rest.dto.RegisterRequest;
import cm.jemil.auth.application.inbound.usecase.LoginUseCase;
import cm.jemil.auth.application.inbound.usecase.RefreshTokenUseCase;
import cm.jemil.auth.application.inbound.usecase.RegisterUserUseCase;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUseCase loginUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest request) {
        var command = new RegisterUserUseCase.Command(
                request.email(), request.password(), request.phoneNumber(), request.roles());
        var userId = registerUserUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("userId", userId.value().toString()));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        var command = new LoginUseCase.Command(request.email(), request.password());
        var result = loginUseCase.execute(command);
        return ResponseEntity.ok(
                new LoginResponse(result.accessToken(), result.refreshToken(), result.userId(), result.roles()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        var result = refreshTokenUseCase.execute(request.refreshToken());
        return ResponseEntity.ok(
                new LoginResponse(result.accessToken(), result.refreshToken(), result.userId(), result.roles()));
    }
}
