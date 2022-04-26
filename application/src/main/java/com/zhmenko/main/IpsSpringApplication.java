package com.zhmenko.main;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import com.ulisesbocchio.jasyptspringboot.annotation.EncryptablePropertySource;
import com.zhmenko.router.SSH;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@SpringBootApplication(scanBasePackages = {"com.zhmenko.*"})
@EnableEncryptableProperties
@EncryptablePropertySource(name = "mainconf", value = "classpath:application.yml")
@OpenAPIDefinition
@EnableTransactionManagement
public class IpsSpringApplication {

    public static void main(String[] args) {
        //ApplicationContext context = SpringApplication.run(IpsSpringApplication.class, args);
        ApplicationContext context = new SpringApplicationBuilder(IpsSpringApplication.class).headless(false).run(args);

/*        Console console = (Console) context.getBean("console");
        AnalyzeThread thread = context.getBean(AnalyzeThread.class);
        thread.setConsole(console);*/
        //Console.frameCreate();
        //new DebugSSHConsole().start();
/*        InetAddress source = InetAddress.getByName("192.168.1.1");

        Collector collector = new Collector(2055);
        V5FlowHandler handler = new V5FlowHandler(source, 100);
        handler.addAccountant(new HandlerAction());
        collector.addFlowHandler(handler);
        collector.start();*/
        //new BlackList();
        //new NetflowPacketDeleteByTimeThread( 15 * 60,context.getBean(NetflowService.class));
        //new AnalyzeThread(Router.KEENETIC).start();
    }

    @Component

    public class Run implements CommandLineRunner {
        @Autowired
        private SSH ssh;

        @Override
        public void run(String... args) throws InterruptedException {
            List<Integer> ports = List.of(90,1900,32,17);
            String ipAddress = "192.168.12.5";
            ThreadLocalRandom current = ThreadLocalRandom.current();
            List<Integer> randPorts = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                randPorts.add(current.nextInt(9000)+1);
            }

            ssh.permitUserPorts(ipAddress,randPorts);

            Thread.sleep(10000);

            ssh.denyUserPorts(ipAddress,randPorts);
        }
    }
}
