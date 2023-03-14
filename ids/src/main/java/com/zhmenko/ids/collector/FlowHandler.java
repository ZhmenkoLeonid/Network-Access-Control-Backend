package com.zhmenko.ids.collector;

import com.zhmenko.ids.collector.factory.v5.NetflowPacketFactoryV5;
import com.zhmenko.ids.models.ids.exception.BlockedUserException;
import com.zhmenko.ids.models.ids.exception.UnsupportedProtocolException;
import lombok.RequiredArgsConstructor;
import nettrack.net.IpAddr;
import nettrack.net.netflow.Accountant;
import nettrack.net.netflow.Flow;
import nettrack.net.netflow.V5Flow;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Component
@RequiredArgsConstructor
@Profile({"dev","prod"})
public class FlowHandler implements Accountant {
    private final NetflowPacketFactoryV5 factory;

    public void account(Flow f) {
        try {
            factory.addNetflowRecord(
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