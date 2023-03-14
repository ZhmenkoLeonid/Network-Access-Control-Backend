package com.zhmenko.ids.config;

import com.zhmenko.ids.models.ids.device.NetflowDeviceList;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;

@Configuration
@Import(NetflowDeviceList.class)
@Profile("dev")
@RequiredArgsConstructor
public class NetflowLoadEntitiesConfigurationDev {
    private final NetflowDeviceList netflowDeviceList;

    @EventListener(ApplicationReadyEvent.class)
    public void loadNetflowEntitiesDev() {
        netflowDeviceList.loadUsersFromDbDebug();
    }
}
