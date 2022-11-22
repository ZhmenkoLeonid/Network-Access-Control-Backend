package com.zhmenko.web.nac.controllers;


import com.zhmenko.data.nac.models.NacUserEntity;
import com.zhmenko.web.nac.services.NacUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/nac-user")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class NacUserController {
    private final NacUserService nacUserService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<NacUserEntity> getAll() {
        log.info("getAll Query");
        return nacUserService.findAllUsers();
    }

    @DeleteMapping("/{mac}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable("mac") String macAddress) {
        log.info("delete user with mac:" + macAddress);
        nacUserService.deleteUser(macAddress);
        return new ResponseEntity<>("deleted" + macAddress, HttpStatus.OK);
    }

    @PutMapping
    public void updateUser(@RequestBody NacUserEntity nacUserEntity) {
        log.info("updating: " + nacUserEntity.toString());
        nacUserService.updateUser(nacUserEntity);
    }

    @GetMapping("/{mac}")
    @PreAuthorize("hasRole('ADMIN')")
    public boolean isExist(@PathVariable("mac") String macAddress) {
        return nacUserService.isUserExist(macAddress);
    }

}
