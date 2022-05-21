package com.zhmenko.web.api.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhmenko.hostvalidation.host.ValidationPacket;
import com.zhmenko.web.services.ConnectService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
@Slf4j
public class HostConnectController {
    private final ConnectService connectService;

    @PostMapping(value = "/connect"/*, consumes = "application/json"*/)
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<String> connect(@RequestBody String clientDeviceData, HttpServletRequest request) {
        ObjectMapper objectMapper = new ObjectMapper();
        ValidationPacket validationPacket = null;
        try {
            validationPacket = objectMapper.readValue(clientDeviceData, ValidationPacket.class);
        } catch (Exception e) {e.printStackTrace();}

        if (validationPacket == null) {
            log.info("null data");
            return new ResponseEntity<>("Bad body :(", HttpStatus.BAD_REQUEST);
        }

        return connectService.connect(validationPacket, request.getRemoteAddr())
                ? new ResponseEntity<>("Good data!", HttpStatus.OK)
                : new ResponseEntity<>("Bad data! :(", HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/post-connect")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<String> postConnect(@RequestBody String clientDeviceData, HttpServletRequest request) {
        ObjectMapper objectMapper = new ObjectMapper();
        ValidationPacket validationPacket = null;
        try {
            validationPacket = objectMapper.readValue(clientDeviceData, ValidationPacket.class);
        } catch (Exception e) {e.printStackTrace();}

        if (validationPacket == null) {
            log.info("null data");
            return new ResponseEntity<>("Bad post-connect refresh ! :(", HttpStatus.BAD_REQUEST);
        }
        return connectService.connect(validationPacket, request.getRemoteAddr())
                ? new ResponseEntity<>("Post-connect refresh successful!", HttpStatus.OK)
                : new ResponseEntity<>("Bad post-connect refresh ! :(", HttpStatus.UNAUTHORIZED);
    }
}
