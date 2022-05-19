package com.zhmenko.ids.traffic_analyze.analyzers;

import com.zhmenko.ids.model.netflow.user.NetflowUser;

import java.util.List;

public interface TrafficAnalyzer {
    List<String> analyze(NetflowUser netflowUser);
}
