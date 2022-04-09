package com.zhmenko.web.certificate;

import org.apache.tomcat.jni.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
// keytool -genkeypair -keyalg RSA -keysize 4096 -validity 3650 -keypass changeit -keystore client.p12 -storeType PKCS12 -storepass changeit -ext "SAN:c=DNS:localhost,IP:127.0.0.1"
// curl -k --cert client.p12:changeit https://localhost:8443
// keytool -genkeypair -keyalg RSA -keysize 4096 -validity 3650 -keypass changeit -keystore keystore.p12 -storeType PKCS12 -storepass changeit
@Component
public class CertificateUtil {
    @Value("${server.ssl.trust-store-password}")
    private static String trustStorePass;
    @Value("${server.ssl.key-store-password}")
    private static String keyStorePass;
    @Value("${server.ssl.trust-store-password}")
    private static String storeType;

    public static void createCertificate(String name, String path){
        String command = "keytool -genkey -keypass password -storepass password -keystore clientkeystore.jks";
    }

    private String runCmd(String command) throws IOException {
        StringBuilder response = new StringBuilder();
        ProcessBuilder builder = new ProcessBuilder(
                "cmd.exe", "/c", command);
        builder.redirectErrorStream(true);
        Process p = builder.start();
        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
        while (true) {
            String line = r.readLine();
            if (line == null) { break; }
            response.append(line);
        }
        return response.toString();
    }
}
