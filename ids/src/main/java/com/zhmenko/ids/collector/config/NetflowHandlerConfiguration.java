package com.zhmenko.ids.collector.config;

import com.zhmenko.ids.collector.FlowHandler;
import nettrack.net.netflow.V5FlowHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
@Profile({"dev","prod"})
@Import(FlowHandler.class)
public class NetflowHandlerConfiguration {
    @Bean
    V5FlowHandler v5FlowHandler(
            @Value("${netflow.router.ipAddress}") String inetIpAddress,
            @Autowired FlowHandler flowHandler) throws UnknownHostException {
        InetAddress source = InetAddress.getByName(inetIpAddress);
        V5FlowHandler v5FlowHandler = new V5FlowHandler(source, 100);
        v5FlowHandler.addAccountant(flowHandler);
        return v5FlowHandler;
    }

}
