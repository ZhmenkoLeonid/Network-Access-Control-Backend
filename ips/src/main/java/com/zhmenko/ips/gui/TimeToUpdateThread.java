package com.zhmenko.ips.gui;

import com.zhmenko.ips.traffic_analyze.AnalyzeThread;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TimeToUpdateThread extends Thread {
    private SimpleDateFormat simpleDateFormat;
    private JFrame consoleFrame;
    TimeToUpdateThread(JFrame consoleFrame) {
        this.consoleFrame = consoleFrame;
        simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Override
    public void run() {
        try {
            while (true) {
                long timeBeforeUpdate = AnalyzeThread.getTimeLeftBfrUpdateMillis();
                consoleFrame.setTitle("Время до обновления статистики: " +
                        simpleDateFormat.format(
                                new Date(timeBeforeUpdate))
                );
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
