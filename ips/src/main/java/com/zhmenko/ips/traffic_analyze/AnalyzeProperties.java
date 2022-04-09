package com.zhmenko.ips.traffic_analyze;

import com.zhmenko.ips.router_interaction.Router;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("netflow.analyze")
@Data
public class AnalyzeProperties {
    private Router routerType;
    private long analyzeFrequencyMillis;
    private long updateMeanValueTimeMillis;
    private int flowMultiplierLimitation;
    private int maxUniqueDestinationPortCount;
}
