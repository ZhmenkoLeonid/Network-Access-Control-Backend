package com.zhmenko.web.security.model.jwt.response;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class JwtResponse {
    @NonNull
    private String token;
    private final String type = "Bearer";
    private String refreshToken;
    @NonNull
    private UUID id;
    @NonNull
    private String username;
    @NonNull
    private List<String> roles;
}
