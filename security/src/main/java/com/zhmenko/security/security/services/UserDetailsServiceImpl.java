package com.zhmenko.security.security.services;


import com.zhmenko.security.data.SecurityUserDao;
import com.zhmenko.security.models.User;
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
  private final SecurityUserDao securityUserDao;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = securityUserDao.findByUsername(username);
    if (user == null) throw new UsernameNotFoundException("User Not Found with username: " + username);

    return UserDetailsImpl.build(user);
  }
}