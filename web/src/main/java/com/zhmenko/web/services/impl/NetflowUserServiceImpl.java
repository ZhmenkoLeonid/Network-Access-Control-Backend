package com.zhmenko.web.services.impl;

import com.zhmenko.ids.model.netflow.user.NetflowUser;
import com.zhmenko.ids.model.netflow.user.NetflowUserList;
import com.zhmenko.ids.model.netflow.user.NetflowUserStatisticDto;
import com.zhmenko.ids.mapper.NetflowUserStatisticMapper;
import com.zhmenko.web.services.NetflowUserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class NetflowUserServiceImpl implements NetflowUserService {
    private final NetflowUserList netflowUserList;
    private final NetflowUserStatisticMapper mapper;

    @Override
    public Map<String, NetflowUserStatisticDto> getAll() {
        List<NetflowUser> users = netflowUserList.getUserList();
        if (users.size() == 0) return new HashMap<>();

        return mapper.build(users.stream()
                .map(NetflowUser::getNetflowUserStatistic)
                .collect(Collectors.toList()));
    }

    @Override
    public NetflowUserStatisticDto getUserStatisticByMacAddress(String macAddress) {
        NetflowUser user = netflowUserList.getUserByMacAddress(macAddress);
        if (user == null) return new NetflowUserStatisticDto();
        return mapper.build(user.getNetflowUserStatistic());
    }
}
