package com.zhmenko.web.security.services.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zhmenko.data.security.models.SecurityUserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@Data
public class UserDetailsImpl implements UserDetails {
  private static final long serialVersionUID = 1L;
  private UUID id;
  private String username;
  @JsonIgnore
  private String password;

  private Collection<? extends GrantedAuthority> authorities;

  public static UserDetailsImpl build(SecurityUserEntity securityUser) {
    List<GrantedAuthority> authorities = securityUser.getSecurityRoles().stream()
        .map(r -> new SimpleGrantedAuthority(r.getName()))
        .collect(Collectors.toList());

    return new UserDetailsImpl(
        securityUser.getId(),
        securityUser.getUsername(),
        securityUser.getPassword(),
        authorities);
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    UserDetailsImpl user = (UserDetailsImpl) o;
    return Objects.equals(id, user.id);
  }
}
