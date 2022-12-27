package com.zhmenko.data.netflow.models.config;

import com.zhmenko.data.netflow.models.device.NetflowDeviceList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;

@Configuration
@Import(NetflowDeviceList.class)
@Profile("prod")
public class NetflowLoadEntitiesConfiguration {
    @Autowired
    private NetflowDeviceList netflowDeviceList;

    @EventListener(ApplicationReadyEvent.class)
    public void loadNetflowEntitiesProd() {
        netflowDeviceList.loadUsersFromDb();
    }
}
