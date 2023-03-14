package com.zhmenko.ids.traffic_analyze.analyzers;

import com.zhmenko.ids.models.ids.device.NetflowDevice;

import java.util.List;

public interface TrafficAnalyzer {
    List<String> analyze(NetflowDevice netflowDevice);
}
