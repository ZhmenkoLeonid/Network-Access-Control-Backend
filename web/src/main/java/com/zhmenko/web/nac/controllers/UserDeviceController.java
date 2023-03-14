package com.zhmenko.web.nac.controllers;


import com.zhmenko.ids.models.ids.exception.UserNotExistException;
import com.zhmenko.web.nac.model.user_device.UserDeviceDto;
import com.zhmenko.web.nac.model.user_device.request.modify.UserDeviceModifyDto;
import com.zhmenko.web.nac.model.user_device.response.UserDeviceResponse;
import com.zhmenko.web.nac.services.UserDeviceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/user-device")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class UserDeviceController {
    private final UserDeviceService userDeviceService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDeviceResponse> getAll() {
        log.info("Get All Devices Query");
        return ResponseEntity.ok(new UserDeviceResponse(userDeviceService.findAllUsers()));
    }

    @DeleteMapping("/{mac}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteDevice(@PathVariable("mac") String macAddress) {
        log.info("delete device with mac: " + macAddress);
        if (!userDeviceService.deleteUserByMacAddress(macAddress))
            throw new UserNotExistException("Устройство с mac " + macAddress + " не найдено!");
        return new ResponseEntity<>("Успешно удалено устройство с mac: " + macAddress, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<String> updateDevice(@RequestBody @Valid UserDeviceModifyDto userDeviceModifyDto) {
        log.info("updating device: " + userDeviceModifyDto);
        userDeviceService.updateUser(userDeviceModifyDto);
        return ResponseEntity.ok("Обновление устройства успешно!");
    }

    @GetMapping("/exist/{mac}")
    @PreAuthorize("hasRole('ADMIN')")
    public boolean isDeviceExist(@PathVariable("mac") String macAddress) {
        return userDeviceService.isUserExistByMacAddress(macAddress);
    }

    @GetMapping("/{mac}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDeviceDto> getDeviceByMacAddress(@PathVariable("mac") String macAddress) {
        Optional<UserDeviceDto> userOpt = userDeviceService.findByMacAddress(macAddress);
        if (userOpt.isEmpty())
            throw new UserNotExistException("Устройства с mac: " + macAddress + " не cуществует");
        return ResponseEntity.ok(userOpt.get());
    }

}
