package client;

import network.TCPConnection;
import network.TCPConnectionListener;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class ClientWindow extends JFrame implements TCPConnectionListener, ActionListener {
    //Server IP_ADDR
    private final String IP_ADDR = "YOUR SERVER IP";
    //This is port that the server listen
    private final int PORT = 3306;
    private final int WIDTH = 600;
    private final int HEIGTH = 400;
    private TCPConnection connection;

    private final JTextArea log = new JTextArea();
    private final JTextField fieldNickName = new JTextField("Your_name....");
    private final JTextField fieldInput = new JTextField("Your_message....");


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientWindow();
            }
        });
    }

    public ClientWindow(){
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH,HEIGTH);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        log.setEditable(false);
        log.setLineWrap(true);
        add(log, BorderLayout.CENTER);
        add(fieldNickName, BorderLayout.NORTH);
        add(fieldInput, BorderLayout.SOUTH);

        fieldInput.addActionListener(this);

        setVisible(true);

        try {
            connection = new TCPConnection(this,IP_ADDR,PORT);
        } catch (IOException e) {
            printMessage("Connection exception: "+e);
        }
    }

    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        printMessage("Connection ready.....");
    }

    @Override
    public synchronized void onReceiveString(TCPConnection tcpConnection, String value) {
        printMessage(value);
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
        System.out.println("Connection close");
    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception e) {
        printMessage("Exception"+e);
    }

    private synchronized void printMessage(String value){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append(value+"\n");
                log.setCaretPosition(log.getDocument().getLength());
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String message = fieldInput.getText();
        if(message.equals(""))return;
        fieldInput.setText(null);
        connection.sendString(fieldNickName.getText()+" : "+message);
    }
}