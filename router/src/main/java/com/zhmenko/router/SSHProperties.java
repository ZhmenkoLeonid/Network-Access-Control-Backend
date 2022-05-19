package com.zhmenko.router;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class SSHProperties {
    @Value("${netflow.router.username}")
    private String username;
    @Value("${netflow.router.password}")
    private String password;
    @Value("${netflow.router.ipAddress}")
    private String ipAddress;
    @Value("${netflow.router.accessListName}")
    private String accessListName;
    @Value("${netflow.router.enabledModePassword ?:cisco}")
    private String enabledModePassword;
}
