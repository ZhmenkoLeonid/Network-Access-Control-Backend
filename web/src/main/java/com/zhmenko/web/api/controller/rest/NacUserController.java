package com.zhmenko.web.api.controller.rest;

import com.zhmenko.ids.model.nac.NacUserDto;
import com.zhmenko.web.services.NacUserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/nac-user")
@AllArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class NacUserController {
    private final NacUserService nacUserService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<NacUserDto> getAll() {
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

    @PutMapping("/{mac}")
    public void updateUser(@PathVariable("mac") String macAddress,
                           @RequestBody NacUserDto nacUserDto) {
        log.info("updating: " + nacUserDto.toString());
        nacUserService.updateUser(nacUserDto);
    }

    @GetMapping("/{mac}")
    @PreAuthorize("hasRole('ADMIN')")
    public boolean isExist(@PathVariable("mac") String macAddress) {
        return nacUserService.isUserExist(macAddress);
    }

}
