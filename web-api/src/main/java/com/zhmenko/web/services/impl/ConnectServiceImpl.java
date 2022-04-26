package com.zhmenko.web.services.impl;

import com.zhmenko.dao.UserOpenedPortsDao;
import com.zhmenko.dao.list.user.UserList;
import com.zhmenko.model.host.HostData;
import com.zhmenko.router.SSH;
import com.zhmenko.verification.Verification;
import com.zhmenko.web.services.ConnectService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@AllArgsConstructor
public class ConnectServiceImpl implements ConnectService {
    private Verification verification;
    private SSH ssh;
    private UserList userList;

    private UserOpenedPortsDao userOpenedPortsDao;

    @Override
    public boolean connect(HostData data) {
        if (verification.check(data)) {
            String macAddress = data.getMacAddress();

            if (!userList.isExist(macAddress))
                userList.addUser(macAddress);
            // TODO как-то сделать условие на открытие портов
            List<Integer> openPortList = List.of(45,50,1900);
            //TODO сымитировать транзакцию (сохранение портов в бд + открытие доступа роутере [ssh])
            userOpenedPortsDao.saveList(macAddress,openPortList);

            ssh.permitUserPorts(data.getIpAddress(),openPortList);
            return true;
        }
        return false;
    }
}
