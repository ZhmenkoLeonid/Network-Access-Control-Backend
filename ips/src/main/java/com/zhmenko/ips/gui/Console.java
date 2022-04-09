package com.zhmenko.ips.gui;


import com.zhmenko.ips.user.BlackList;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.io.IOException;

@Component
@Scope("singleton")
public class Console {
    private JFrame consoleFrame;
    @Getter
    private JTextField consoleTextInput;
    @Getter
    private JTextArea consoleTextOutput;
    private JScrollPane consoleScrollPane;


    public Console(@Autowired BlackList blackList) throws IOException {
        consoleTextInput = new JTextField(20);
        consoleTextOutput = new JTextArea();
        consoleFrame = new JFrame();
        consoleFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        consoleFrame.setSize(800, 800);
        consoleFrame.setIconImage(ImageIO.read(Console.class.getResource("/images/first.png")));
        consoleTextOutput.setEditable(false);

        consoleScrollPane = new JScrollPane(consoleTextOutput,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        //chatPanel.add(chatTextField);

        //chatPanel.add(chatTextField);
        consoleTextInput.setPreferredSize(new Dimension(700, 25));
        consoleTextInput.addKeyListener(new EnterAction(this, blackList));
        consoleTextInput.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        consoleFrame.getContentPane().add(BorderLayout.SOUTH, consoleTextInput);
        consoleFrame.getContentPane().add(BorderLayout.CENTER, consoleScrollPane);
        consoleFrame.setVisible(true);
        new TimeToUpdateThread(consoleFrame).start();
    }

    public void appendMsg(String msg) throws BadLocationException {
        consoleTextOutput.append(msg + "\n");
        consoleTextOutput.setCaretPosition(consoleTextOutput.getText().length());
    }
}
