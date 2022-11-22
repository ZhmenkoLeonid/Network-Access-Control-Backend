package com.zhmenko.ids.traffic_analyze.analyzers;

import com.zhmenko.data.netflow.models.user.NetflowUser;

import java.util.List;

public interface TrafficAnalyzer {
    List<String> analyze(NetflowUser netflowUser);
}
