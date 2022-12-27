package com.zhmenko.web.ids.controllers;

import com.zhmenko.web.netflow.model.NetflowUserStatisticDto;
import com.zhmenko.web.ids.services.NetflowUserStatisticService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/netflow-user-stat")
@Slf4j
public class NetflowUserStatisticController {
    private final NetflowUserStatisticService netflowUserStatisticService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, NetflowUserStatisticDto> getAll() {
        log.info("invoke getAllStats");
        return netflowUserStatisticService.getAll();
    }

    @GetMapping("/{mac}")
    @PreAuthorize("hasRole('ADMIN')")
    public NetflowUserStatisticDto getByMacAddress(@PathVariable("mac") String macAddress){
        NetflowUserStatisticDto dto = netflowUserStatisticService.getUserStatisticByMacAddress(macAddress);
        log.info("get stat for user:" + macAddress +". ret: " + dto);
        return dto;
    }
}
