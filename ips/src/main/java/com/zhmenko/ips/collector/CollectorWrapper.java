package com.zhmenko.ips.collector;

import nettrack.net.netflow.Collector;
import nettrack.net.netflow.V5FlowHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.SocketException;

@Component
public class CollectorWrapper extends Collector {
    public CollectorWrapper(@Value("${netflow.collector.port}") int port,
                            @Autowired V5FlowHandler flowHandler)
            throws SocketException {
        super(port);
        this.addFlowHandler(flowHandler);
        this.start();
    }
}
