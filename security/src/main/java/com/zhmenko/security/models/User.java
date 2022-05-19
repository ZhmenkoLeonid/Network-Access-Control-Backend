package com.zhmenko.security.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class User {
  private final UUID id;

  @NotBlank
  @Size(max = 20)
  private final String username;

  @NotBlank
  private final String password;

  private Set<String> roles = new HashSet<>();
}