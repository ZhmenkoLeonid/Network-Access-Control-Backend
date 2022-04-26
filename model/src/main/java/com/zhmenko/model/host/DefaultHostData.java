package com.zhmenko.model.host;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.Map;

public class DefaultHostData implements HostData {
    private String macAddress;
    private String ipAddress;
    private Map<String, String> jsonValuesMap;

    public DefaultHostData(Map<String, String> jsonValuesMap) {
        this.macAddress = jsonValuesMap.get("macAddress");
        this.ipAddress = jsonValuesMap.get("ipAddress");

        if (this.macAddress == null || this.ipAddress == null)
            throw new IllegalArgumentException("В передаваемых данных отсутствуют ip и/или mac адрес");

        this.jsonValuesMap = jsonValuesMap;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getMacAddress() {
        return macAddress;
    }
}
