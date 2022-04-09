package com.zhmenko.ips.Certificate;

/**
 *
 * 109.120.35.250
 *
 * keytool -genkey -keypass password -storepass password -keystore serverkeystore.jks
 *
 * keytool -export -storepass password  -file server.cer  -keystore serverkeystore.jks
 *
 * keytool -import -v -trustcacerts -file server.cer -keypass password -storepass password -keystore clienttruststore.jks
 *
 * keytool -genkey -keypass password -storepass password -keystore clientkeystore.jks
 *
 * keytool -export -storepass password  -file client.cer  -keystore clientkeystore.jks
 *
 * keytool -import -v -trustcacerts -file client.cer -keypass password -storepass password -keystore servertruststore.jks
 *
 * **/

public class serverTSL {
    public static void main(String[] args) {

/*
        System.setProperty("javax.net.ssl.keyStore","C:\\Users\\zmenk\\IdeaProjects\\UDPCollector\\serverkeystore.jks");
        System.setProperty("javax.net.ssl.keyStorePassword","password");
        System.setProperty("javax.net.ssl.trustStore","C:\\Users\\zmenk\\IdeaProjects\\UDPCollector\\servertruststore.jks");
        System.setProperty("javax.net.ssl.trustStorePassword","password");

        int port = 8443;
        ServerSocketFactory factory = SSLServerSocketFactory.getDefault();
        try (ServerSocket listener = factory.createServerSocket(port)) {
            SSLServerSocket sslListener = (SSLServerSocket) listener;
            sslListener.setNeedClientAuth(true);
            sslListener.setEnabledCipherSuites(
                    new String[] { "TLS_DHE_DSS_WITH_AES_256_CBC_SHA256" });
            sslListener.setEnabledProtocols(
                    new String[] { "TLSv1.2" });
            String msg;
            while (true) {
                try (Socket socket = sslListener.accept()) {
                    PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    //msg = bufferedReader.readLine();
                    while (true) {
                        msg = bufferedReader.readLine();
                        System.out.println("Сообщение: "+msg);
                        printWriter.println(new BufferedReader(new InputStreamReader(System.in)).readLine());
                    }
                    //out.println("Hello World!");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
}