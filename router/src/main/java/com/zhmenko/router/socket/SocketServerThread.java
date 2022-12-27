package com.zhmenko.router.socket;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.LinkedList;

public class SocketServerThread extends Thread {
    private int port = 8080;
    private static LinkedList<ServerInteraction> serverList = new LinkedList<>(); // список всех нитей

    public static LinkedList<ServerInteraction> getServerList() {
        return serverList;
    }

    public SocketServerThread(int port) {
        this.port = port;
        start();
    }
    public SocketServerThread() {
        this.port = 9998;
        start();
    }
    @Override
    public void run() {
        try {
        DatagramSocket server = new DatagramSocket(port);
        //server.receive();
            while (true) {
                byte[] buffer = new byte[512];
                // Блокируется до возникновения нового соединения:
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                server.receive(packet);
                System.out.println(packet.toString());
                //Socket socket = server.accept();
                
/*                try {
                    System.out.println("Новое соединение: "+socket.getInetAddress());
                    serverList.add(new ServerInteraction(socket)); // добавить новое соединенние в список
                } catch (IOException e) {
                    // Если завершится неудачей, закрывается сокет,
                    // в противном случае, нить закроет его при завершении работы:
                    socket.close();
                }*/
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
        }
    }
}