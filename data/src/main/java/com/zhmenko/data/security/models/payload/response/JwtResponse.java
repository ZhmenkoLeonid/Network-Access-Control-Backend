package com.zhmenko.data.security.models.payload.response;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@RequiredArgsConstructor
public class JwtResponse {
  @NonNull private String token;
  private String type = "Bearer";
  @NonNull private UUID id;
  @NonNull private String username;
  @NonNull private List<String> roles;
}
