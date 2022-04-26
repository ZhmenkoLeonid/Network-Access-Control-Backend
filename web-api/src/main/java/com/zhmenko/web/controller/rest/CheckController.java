package com.zhmenko.web.controller.rest;

import com.zhmenko.model.host.DefaultHostData;
import com.zhmenko.model.host.HostData;
import com.zhmenko.web.services.ConnectService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@AllArgsConstructor
public class CheckController {
    private final ConnectService connectService;

    @PostMapping(value = "/connect", consumes = "application/json")
    public ResponseEntity<String> connect(@RequestBody Map<String,String> data) {
        return connectService.connect(new DefaultHostData(data))
                ? new ResponseEntity<>("Good data!",HttpStatus.OK)
                : new ResponseEntity<>("Bad data! :(",HttpStatus.UNAUTHORIZED);
    }
}
