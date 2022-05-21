package ids;

import com.zhmenko.ids.model.netflow.user.NetflowUser;
import com.zhmenko.ids.model.netflow.user.NetflowUserList;
import com.zhmenko.main.IpsSpringApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@SpringBootTest(classes = {IpsSpringApplication.class})
@RunWith(SpringRunner.class)
public class NetflowUserListTest {
    @Autowired
    private NetflowUserList netflowUserList;

    @Test
    public void timerDeleting() throws InterruptedException {
        netflowUserList.setUserSessionTTLMillis(500);
        NetflowUser netflowUser = new NetflowUser("mac","ip","hostname",10000);
        netflowUserList.addUser(netflowUser);
        Thread.sleep(550);
        assertFalse(netflowUserList.isExistByMacAddress("mac"));
    }
    @Test
    public void timerUpdating() throws InterruptedException {
        netflowUserList.setUserSessionTTLMillis(1000);
        NetflowUser netflowUser = new NetflowUser("mac","ip","hostname",10000);
        netflowUserList.addUser(netflowUser);
        Thread.sleep(500);
        netflowUserList.updateUserTTLTimer(netflowUser);
        Thread.sleep(700);
        assertTrue(netflowUserList.isExistByMacAddress("mac"));
        Thread.sleep(300);
        assertFalse(netflowUserList.isExistByMacAddress("mac"));
    }
}
