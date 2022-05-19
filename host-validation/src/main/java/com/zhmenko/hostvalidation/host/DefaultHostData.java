package com.zhmenko.hostvalidation.host;

import java.util.Map;

public class DefaultHostData implements HostData {
    private String macAddress;
    private String ipAddress;
    private String hostname;
    private Map<String, String> jsonValuesMap;

    public DefaultHostData(Map<String, String> jsonValuesMap, String ipAddress) {
        this.macAddress = jsonValuesMap.get("macAddress");
        this.ipAddress = ipAddress;
        this.hostname = jsonValuesMap.get("hostname");

        if (this.macAddress == null || this.ipAddress == null || this.hostname == null)
            throw new IllegalArgumentException("В передаваемых данных отсутствуют по крайней мере один из параметров:" +
                    " ip адрес, mac адрес, имя хоста");

        this.jsonValuesMap = jsonValuesMap;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getMacAddress() {
        return macAddress;
    }

    @Override
    public String getHostname() {
        return hostname;
    }
}
