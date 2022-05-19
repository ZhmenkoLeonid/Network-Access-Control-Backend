package com.zhmenko.ids.model.nac;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NacUserDto {
    private String macAddress;
    private String hostname;
    private boolean isBlacklisted;
    private String ipAddress;
    private List<Integer> ports;
    private List<String> alerts;

    public NacUserDto(String macAddress, String hostname, String ipAddress){
        this.macAddress = macAddress;
        this.hostname = hostname;
        this.ipAddress = ipAddress;
        this.ports = new ArrayList<>();
        this.alerts = new ArrayList<>();
    }
}