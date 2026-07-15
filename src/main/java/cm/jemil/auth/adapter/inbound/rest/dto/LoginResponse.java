package cm.jemil.auth.adapter.inbound.rest.dto;

import java.util.Set;

public record LoginResponse(String accessToken, String refreshToken, String userId, Set<String> roles) {}
