package com.zhmenko.ips.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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
        ServerSocket server = new ServerSocket(port);
            while (true) {
                // Блокируется до возникновения нового соединения:
                Socket socket = server.accept();
                try {
                    System.out.println("Новое соединение: "+socket.getInetAddress());
                    serverList.add(new ServerInteraction(socket)); // добавить новое соединенние в список
                } catch (IOException e) {
                    // Если завершится неудачей, закрывается сокет,
                    // в противном случае, нить закроет его при завершении работы:
                    socket.close();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
        }
    }
}