package com.zhmenko.web.nac.controllers;

import com.zhmenko.hostvalidation.host.ValidationPacket;
import com.zhmenko.web.nac.services.NacHostConnectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@Slf4j
public class NacHostConnectController {
    private final NacHostConnectService nacHostConnectService;

    @PostMapping(value = "/connect", consumes = "application/json")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<String> connect(@RequestBody @Valid ValidationPacket validationPacket, HttpServletRequest request) {

        return nacHostConnectService.connect(validationPacket)
                ? new ResponseEntity<>("Клиент с ip " + validationPacket.getIpAddress()
                + " прошёл pre-connection проверку", HttpStatus.OK)
                : new ResponseEntity<>("Клиент с ip " + validationPacket.getIpAddress()
                + " не прошёл pre-connection проверку", HttpStatus.FORBIDDEN);
    }

    @PostMapping(value = "/post-connect", consumes = "application/json")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<String> postConnect(@RequestBody @Valid ValidationPacket validationPacket, HttpServletRequest request) {
        log.info(request.getUserPrincipal().getName());
        return nacHostConnectService.postConnect(validationPacket)
                ? new ResponseEntity<>("Post-connect refresh successful!", HttpStatus.OK)
                : new ResponseEntity<>("Bad post-connect refresh ! :(", HttpStatus.FORBIDDEN);
    }
}
