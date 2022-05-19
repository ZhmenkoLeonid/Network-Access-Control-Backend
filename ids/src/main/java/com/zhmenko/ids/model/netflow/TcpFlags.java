package com.zhmenko.ids.model.netflow;

public enum TcpFlags {
    FIN(0),
    SYN(1),
    RST(2),
    PSH(3),
    ACK(4),
    URG(5),
    ECE(6),
    CWR(7);
    private int value;

    private TcpFlags(int value) {
        this.value = value;
    }

    public static TcpFlags fromInt(int flagNum) {
        for (TcpFlags b : TcpFlags.values()) {
            if (b.value == flagNum) {
                return b;
            }
        }
        return null;
    }
}
