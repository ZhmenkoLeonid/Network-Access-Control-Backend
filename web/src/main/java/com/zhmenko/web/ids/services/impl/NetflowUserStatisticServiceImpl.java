package com.zhmenko.web.ids.services.impl;

import com.zhmenko.ids.models.ids.device.NetflowDevice;
import com.zhmenko.ids.models.ids.device.NetflowDeviceList;
import com.zhmenko.web.netflow.model.NetflowUserStatisticDto;
import com.zhmenko.web.netflow.mapper.NetflowUserStatisticMapper;
import com.zhmenko.web.ids.services.NetflowUserStatisticService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class NetflowUserStatisticServiceImpl implements NetflowUserStatisticService {
    private final NetflowDeviceList netflowDeviceList;
    private final NetflowUserStatisticMapper mapper;

    @Override
    public Map<String, NetflowUserStatisticDto> getAll() {
        List<NetflowDevice> users = netflowDeviceList.getUserList();
        if (users.size() == 0) return new HashMap<>();

        return mapper.build(users.stream()
                .map(NetflowDevice::getNetflowDeviceStatistic)
                .collect(Collectors.toList()));
    }

    @Override
    public NetflowUserStatisticDto getUserStatisticByMacAddress(String macAddress) {
        NetflowDevice user = netflowDeviceList.getUserByMacAddress(macAddress);
        if (user == null) return new NetflowUserStatisticDto();
        return mapper.build(user.getNetflowDeviceStatistic());
    }
}
