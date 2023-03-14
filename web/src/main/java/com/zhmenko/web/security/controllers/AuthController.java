package com.zhmenko.web.security.controllers;

import com.zhmenko.data.security.models.ERole;
import com.zhmenko.data.security.models.RefreshTokenEntity;
import com.zhmenko.data.security.models.SecurityRoleEntity;
import com.zhmenko.data.security.models.SecurityUserEntity;
import com.zhmenko.data.security.repository.SecurityRoleRepository;
import com.zhmenko.data.security.repository.SecurityUserRepository;
import com.zhmenko.web.model.Error;
import com.zhmenko.web.nac.exceptions.not_found.RoleNotFoundException;
import com.zhmenko.web.security.exceptions.TokenRefreshException;
import com.zhmenko.web.security.jwt.JwtUtils;
import com.zhmenko.web.security.model.authcontroller.request.LoginRequest;
import com.zhmenko.web.security.model.authcontroller.request.SignupRequest;
import com.zhmenko.web.security.model.authcontroller.response.MessageResponse;
import com.zhmenko.web.security.model.jwt.request.TokenRefreshRequest;
import com.zhmenko.web.security.model.jwt.response.JwtResponse;
import com.zhmenko.web.security.model.jwt.response.TokenRefreshResponse;
import com.zhmenko.web.security.services.RefreshTokenService;
import com.zhmenko.web.security.services.impl.UserDetailsImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
@Slf4j
public class AuthController {
    private AuthenticationManager authenticationManager;

    private final SecurityUserRepository securityUserRepository;

    private final SecurityRoleRepository securityRoleRepository;

    private final PasswordEncoder encoder;

    private final JwtUtils jwtUtils;

    private final RefreshTokenService refreshTokenService;

    @PostMapping("/signin")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        log.info("auth invoke");
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String jwt = jwtUtils.generateJwtToken(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        RefreshTokenEntity refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

      JwtResponse jwtResponse = JwtResponse.builder()
              .token(jwt)
              .refreshToken(refreshToken.getToken())
              .id(userDetails.getId())
              .username(userDetails.getUsername())
              .roles(roles)
              .build();
      log.info("Успешная авторизация " + request.getRemoteAddr() + "; " + jwtResponse.toString());
        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        log.info("register user with name: " + signUpRequest.getUsername());

        if (securityUserRepository.findByUsername(signUpRequest.getUsername()).isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body(new Error(HttpStatus.BAD_REQUEST.value(), "Error: Username is already taken!"));
        }

        // Create new user's account
        SecurityUserEntity securityUserEntity = SecurityUserEntity.builder()
                .id(UUID.randomUUID())
                .username(signUpRequest.getUsername())
                .password(encoder.encode(signUpRequest.getPassword()))
                .build();

        Set<String> strRoles = signUpRequest.getRoles();
        Set<SecurityRoleEntity> roles = new HashSet<>();

        if (strRoles == null) {
            SecurityRoleEntity userRole = securityRoleRepository.findByName(ERole.ROLE_CLIENT.getRoleName())
                    .orElseThrow(() -> new RoleNotFoundException(ERole.ROLE_CLIENT.getRoleName()));
            roles.add(userRole);
        } else {
            for (String strRole : strRoles) {
                ERole role = ERole.valueOf(strRole.toUpperCase());
                SecurityRoleEntity roleEntity = securityRoleRepository.findByName(role.getRoleName())
                        .orElseThrow(() -> new RoleNotFoundException(role.getRoleName()));
                roles.add(roleEntity);
            }
        }

        securityUserEntity.setSecurityRoles(roles);
        securityUserRepository.save(securityUserEntity);

        return new ResponseEntity<>(new MessageResponse("User registered successfully!"), HttpStatus.CREATED);
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshTokenEntity::getUser)
                .map(user -> {
                    String token = jwtUtils.generateTokenFromUsername(user.getUsername());
                    return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                        "Refresh token is not in database!"));
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UUID userId = userDetails.getId();
        refreshTokenService.deleteByUserId(userId);
        return ResponseEntity.ok(new MessageResponse("Log out successful!"));
    }
}
