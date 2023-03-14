package com.zhmenko.web.security.controllers;

import com.zhmenko.ids.models.ids.exception.UserNotExistException;
import com.zhmenko.web.security.model.securityusercontroller.SecurityUserDto;
import com.zhmenko.web.security.model.securityusercontroller.request.modify.SecurityUserModifyDto;
import com.zhmenko.web.security.model.securityusercontroller.response.SecurityUserResponse;
import com.zhmenko.web.security.services.SecurityUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/security-user")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class SecurityUserController {
    private final SecurityUserService securityUserService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SecurityUserResponse> getAll() {
        log.info("Get All Security Users!");
        return ResponseEntity.ok(new SecurityUserResponse(securityUserService.findAll()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SecurityUserDto> getById(@PathVariable(value = "id") UUID id) {
        log.info("Get security user by id: " + id);
        SecurityUserDto user = securityUserService.findById(id)
                .orElseThrow(() -> new UserNotExistException("Пользователь с id \"" + id + "\" не найден!"));
        return ResponseEntity.ok(user);
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateUser(@RequestBody @Valid SecurityUserModifyDto securityUserModifyDto) {
        log.info("updating security user: " + securityUserModifyDto);
        securityUserService.updateUser(securityUserModifyDto);
        return ResponseEntity.ok("Обновление пользователя успешно!");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable("id") UUID id) {
        log.info("delete security user with id:  " + id);
        if (!securityUserService.deleteUserById(id))
            throw new UserNotExistException("Пользователь с id " + id + " не найден!");
        return ResponseEntity.ok("Успешно удалён пользователь с id: " + id);
    }
}
