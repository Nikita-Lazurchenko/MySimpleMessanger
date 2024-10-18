package server;

import network.TCPConnection;
import network.TCPConnectionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ChatServer implements TCPConnectionListener {
    private final ArrayList<TCPConnection> connections = new ArrayList<>();

    public static void main(String[] args) {
        new ChatServer();
    }

    private ChatServer(){
        System.out.println("Server SimpleMessenger running.....");
        try(ServerSocket serverSocket = new ServerSocket(3306)){
            while(true){
                try{
                    new TCPConnection(serverSocket.accept(), this);
                }catch (IOException e){
                    System.out.println("TCPConnection exception: " + e);
                }
            }
        }catch (IOException e){
            throw new RuntimeException();
        }
    }

    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        connections.add(tcpConnection);
        sendToALLConnections("Connected " + tcpConnection);
    }

    @Override
    public synchronized void onReceiveString(TCPConnection tcpConnection, String value) {
        sendToALLConnections(value);
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
        connections.remove(tcpConnection);
        sendToALLConnections("Disconnected " + tcpConnection);
    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception e) {
        System.out.println("TCPConnection exception : "+e);
    }

    private void sendToALLConnections(String value){
        System.out.println(value);
        for (TCPConnection connection : connections) connection.sendString(value);
    }
}