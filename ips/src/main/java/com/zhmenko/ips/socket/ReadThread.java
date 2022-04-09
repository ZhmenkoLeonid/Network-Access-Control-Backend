package com.zhmenko.ips.socket;
import com.jcraft.jsch.Channel;

import java.io.InputStream;

public class ReadThread extends Thread  {
    private InputStream in;
    private Channel channel;

    public ReadThread(InputStream in,Channel channel) {
        this.in = in;
        this.channel = channel;
    }
    @Override
    public void run() {
        byte[] tmp = new byte[1024];
        try {
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    System.out.print(new String(tmp, 0, i));
                }
                if (channel.isClosed()) {
                    System.out.println("exit-status: " + channel.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}