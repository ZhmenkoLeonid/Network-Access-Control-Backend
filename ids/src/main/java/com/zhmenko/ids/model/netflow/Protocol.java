package com.zhmenko.ids.model.netflow;

public enum Protocol {
    ICMP("1"),
    IGMP("2"),
    TCP("6"),
    UDP("17");
    private String value;

    Protocol(String value) {
        this.value = value;
    }
    public String getValue(){
        return this.value;
    }

    public static Protocol fromStringNumber(String protNum) {
        for (Protocol b : Protocol.values()) {
            if (b.value.equals(protNum)) {
                return b;
            }
        }
        return null;
    }
    public static Protocol fromStringName(String protString) {
        for (Protocol b : Protocol.values()) {
            if (b.name().equals(protString)) {
                return b;
            }
        }
        return null;
    }
}
