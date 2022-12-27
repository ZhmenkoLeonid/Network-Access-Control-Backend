package com.zhmenko.web.security.model.authcontroller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupRequest {
  @Size(min = 3, max = 20)
  private String username;

  private Set<String> roles;

  @Size(min = 6, max = 40)
  private String password;

}
