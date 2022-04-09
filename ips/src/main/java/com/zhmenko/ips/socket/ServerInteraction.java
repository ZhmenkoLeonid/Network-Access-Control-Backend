package com.zhmenko.ips.socket;

import java.io.*;
import java.net.Socket;

public class ServerInteraction extends Thread {
    private Socket socket; // сокет, через который сервер общается с клиентом,
    // кроме него - клиент и сервер никак не связаны
    private BufferedReader in; // поток чтения из сокета
    private BufferedWriter out; // поток записи в сокет

    public ServerInteraction(Socket socket) throws IOException {
        this.socket = socket;
        // если потоку ввода/вывода приведут к генерированию исключения, оно проброситься дальше
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        start(); // вызываем run()
    }
    @Override
    public void run() {
        String msg;
        try {
            while (true) {
                msg = in.readLine();
                System.out.println("Msg from client "+socket.getInetAddress()+":\n"+msg);
                //for (ClientInteractionThread vr : SocketServerThread.getServerList()) {
               //     System.out.println("Msg from client: "+socket.getInetAddress()+"\n"+msg);
                //}
            }

        } catch (IOException e) {
        }
    }

    private void send(String msg) {
        try {
            out.write(msg + "\n");
            out.flush();
        } catch (IOException ignored) {}
    }
}
