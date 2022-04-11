package com.zhmenko.ips.collector;

import com.zhmenko.model.exceptions.BlockedUserException;
import com.zhmenko.model.exceptions.UnsupportedProtocolException;
import com.zhmenko.ips.v5.NetflowPacketFactoryV5;
import nettrack.net.IpAddr;
import nettrack.net.netflow.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Date;

@Component
public class HandlerAction implements Accountant {
    private NetflowPacketFactoryV5 factory;

    public HandlerAction(@Autowired NetflowPacketFactoryV5 factory){
        this.factory = factory;
    }

    public void account(Flow f) {
        try {
            factory.createNetflowClass(IpAddr.toString(f.getSrcAddr()),IpAddr.toString(f.getDstAddr())
                    ,String.valueOf(((V5Flow)f).getProt())
                    ,String.valueOf(f.getSrcPort()),String.valueOf(f.getDstPort()), new Date(System.currentTimeMillis()),
                    ((V5Flow) f).getTcpFlags());

        } catch (UnsupportedProtocolException | BlockedUserException e) {
            e.printStackTrace();
        }
    }

    public void destroy() {
    }
}