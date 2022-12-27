package com.zhmenko.web.security.services.impl;


import com.zhmenko.data.security.models.SecurityUserEntity;
import com.zhmenko.data.security.repository.SecurityUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Primary
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
  private final SecurityUserRepository securityUserDao;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    SecurityUserEntity user = securityUserDao.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

    return UserDetailsImpl.build(user);
  }
}