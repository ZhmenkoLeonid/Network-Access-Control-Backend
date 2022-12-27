/*
package com.zhmenko.ids.gui;

import com.zhmenko.data.netflow.models.user.BlackList;
import com.zhmenko.web.netflow.mapper.NetflowUserStatisticMapper;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class EnterAction implements KeyListener {
    Console console;
    BlackList blackList;
    NetflowUserStatisticMapper userStatistics;

    public EnterAction(Console console, BlackList blackList, NetflowUserStatisticMapper userStatistics) {
        this.userStatistics = userStatistics;
        this.blackList = blackList;
        this.console = console;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        String[] msg;
        try {
            if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                String text = console.getConsoleTextInput().getText();
                if (text.equals("cls")) {
                    console.getConsoleTextOutput().setText(null);
                } else if (text.contains("show")) {
                    if ((msg = text.split(" ")).length > 1) {
                        switch (msg[1]) {
                            case "stats":
*/
/*                                text = userStatistics.getAllUserStats();
                                console.appendMsg("show stats\n" + text);*//*

                                break;
                            case "flows":
                                text = "UserStatistics.getAllUserFlows()";
                                console.appendMsg("show flows\n" + text);
                                break;
                            case "blacklist":
                                console.appendMsg("show blacklist\n" + blackList.
                                        getBlockedMacAddressesList().toString());
                                break;
                            default:
                                console.appendMsg("Bad argument! Use: show stats; show flows; show blacklist");
                        }
                    } else {
                        console.appendMsg("Bad argument! Use: show stats; show flows; show blacklist");
                    }
                } else if (text.contains("unblock")) {
                    if ((msg = text.split(" ")).length > 1) {
                        blackList.unblockUser(msg[1]);
                        console.appendMsg("User unblocked!");
                    }
                } else if (text.contains("block")) {
                    if ((msg = text.split(" ")).length > 1) {
                        if (blackList.blockUser(msg[1])) {
                            console.appendMsg("User blocked!");
                        } else {
                            console.appendMsg("Error while blocking! User already in black list!");
                        }
                    }
                } else {
                    console.appendMsg("Bad command! \"" + text + "\"");
                }
                console.getConsoleTextInput().setText("");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
*/
