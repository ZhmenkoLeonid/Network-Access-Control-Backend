package com.zhmenko.ips.collector.config;

import com.zhmenko.ips.collector.HandlerAction;
import nettrack.net.netflow.V5FlowHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
public class NetflowHandlerConfiguration {
    @Bean
    V5FlowHandler v5FlowHandler(
            @Value("${netflow.collector.routerAddress}") String inetIpAddress,
            @Autowired HandlerAction handlerAction) throws UnknownHostException {
        InetAddress source = InetAddress.getByName(inetIpAddress);
        V5FlowHandler v5FlowHandler = new V5FlowHandler(source, 100);
        v5FlowHandler.addAccountant(handlerAction);
        return v5FlowHandler;
    }
}
