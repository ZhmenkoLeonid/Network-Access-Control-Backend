package com.zhmenko.ids.traffic_analyze.analyzers;

import com.zhmenko.data.netflow.models.device.NetflowDevice;

import java.util.List;

public interface TrafficAnalyzer {
    List<String> analyze(NetflowDevice netflowDevice);
}
