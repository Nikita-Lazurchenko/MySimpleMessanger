package network;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Scanner;

public class TCPConnection {
    private final Socket socket;
    private final Thread thread;
    private TCPConnectionListener eventListener;
    private final BufferedReader reader;
    private final BufferedWriter writer;

    public TCPConnection(Socket socket,TCPConnectionListener eventListener) throws IOException {
        this.socket = socket;
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    eventListener.onConnectionReady(TCPConnection.this);
                    while(!thread.isInterrupted()){
                        eventListener.onReceiveString(TCPConnection.this, reader.readLine());
                    }
                }catch (Exception e){
                    eventListener.onException(TCPConnection.this,e);
                }finally {
                    eventListener.onDisconnect(TCPConnection.this);
                }
            }
        });
        thread.start();
    }

    public TCPConnection(TCPConnectionListener eventListener,String ipAddr,int port) throws IOException {
        this(new Socket(ipAddr,port),eventListener);
    }

    public synchronized void sendString(String value){
        try {
            writer.write(value+"\r\n");
            writer.flush();
        } catch (Exception e) {
            eventListener.onException(TCPConnection.this,e);
            disconnect();
        }
    }

    public synchronized void disconnect(){
        thread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            eventListener.onException(TCPConnection.this,e);
        }
    }

    //Override this method for logs that will show the connection or disconnection of all clients.
    @Override
    public String toString() {
        return "TCPConnection: "+socket.getInetAddress()+": "+socket.getPort();
    }
}