package com.zhmenko.data.security.models.payload.request;

import lombok.Data;

import javax.validation.constraints.Size;
import java.util.Set;

@Data
public class SignupRequest {
  @Size(min = 3, max = 20)
  private String username;

  private Set<String> roles;

  @Size(min = 6, max = 40)
  private String password;

}
