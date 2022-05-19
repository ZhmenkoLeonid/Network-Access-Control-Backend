package com.zhmenko.ids.collector;

import com.zhmenko.ids.model.exception.BlockedUserException;
import com.zhmenko.ids.model.exception.UnsupportedProtocolException;
import com.zhmenko.ids.collector.factory.v5.NetflowPacketFactoryV5;
import lombok.AllArgsConstructor;
import nettrack.net.IpAddr;
import nettrack.net.netflow.*;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Component
@AllArgsConstructor
public class FlowHandler implements Accountant {
    private final NetflowPacketFactoryV5 factory;

    public void account(Flow f) {
        try {
            factory.createNetflowClass(
                    IpAddr.toString(f.getSrcAddr()),
                    IpAddr.toString(f.getDstAddr()),
                    String.valueOf(((V5Flow)f).getProt()),
                    String.valueOf(f.getSrcPort()),
                    String.valueOf(f.getDstPort()),
                    new Timestamp(System.currentTimeMillis()),
                    ((V5Flow) f).getTcpFlags());
        } catch (UnsupportedProtocolException | BlockedUserException e) {
            e.printStackTrace();
        }
    }

    public void destroy() {
    }
}