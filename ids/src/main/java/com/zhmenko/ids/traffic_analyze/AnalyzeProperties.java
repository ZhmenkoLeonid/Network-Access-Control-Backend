package com.zhmenko.ids.traffic_analyze;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("netflow.analyze")
@Data
@Profile({"dev","prod"})
public class AnalyzeProperties {
    private long analyzeFrequencyMillis;
    private long updateMeanValueTimeMillis;
    private int flowMultiplierLimitation;
    private int maxUniqueDestinationPortCount;
    private long analyzeExecuteTime;
}
