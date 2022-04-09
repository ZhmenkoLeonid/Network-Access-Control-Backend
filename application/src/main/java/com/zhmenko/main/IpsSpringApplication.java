package com.zhmenko.main;

import com.jcraft.jsch.JSchException;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@SpringBootApplication(scanBasePackages = {"com.zhmenko.*"})
@OpenAPIDefinition
public class IpsSpringApplication {

    public static void main(String[] args) throws IOException, JSchException, InterruptedException {
        //ApplicationContext context = SpringApplication.run(IpsSpringApplication.class, args);
        ApplicationContext context = new SpringApplicationBuilder(IpsSpringApplication.class).headless(false).run(args);
        System.out.println(context.containsBean("console"));
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

}
