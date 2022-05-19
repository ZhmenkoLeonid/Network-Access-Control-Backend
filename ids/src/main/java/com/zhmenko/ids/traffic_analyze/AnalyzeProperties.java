package com.zhmenko.ids.traffic_analyze;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("netflow.analyze")
@Data
public class AnalyzeProperties {
    private long analyzeFrequencyMillis;
    private long updateMeanValueTimeMillis;
    private int flowMultiplierLimitation;
    private int maxUniqueDestinationPortCount;
    private long analyzeExecuteTime;
}
