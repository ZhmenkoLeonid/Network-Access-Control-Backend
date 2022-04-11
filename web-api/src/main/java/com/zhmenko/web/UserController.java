package com.zhmenko.web;

import com.zhmenko.web.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping(value = "/save", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@RequestBody String ipAddress) {
        userService.add(ipAddress);
    }

    @PostMapping(value = "/saveList", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveList(@RequestBody List<String> ipAddresses) {
        userService.addList(ipAddresses);
    }

    @GetMapping(value = "/exist")
    public String isExist(String ipAddress) {
        return userService.isExist(ipAddress);
    }
}
