package xyz.baddeveloper.lwsl.server;

import xyz.baddeveloper.lwsl.server.events.OnConnectEvent;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SocketServer {

    public static void main(String[] args){
        new SocketServer().setPort(80).setMaxConnections(2);
    }

    private int port;
    private int maxconnections;
    private int timeout;

    private List<OnConnectEvent> connectEvents;

    private boolean running = false;
    private ServerSocket serverSocket;

    public SocketServer() {
        this.port = 8080;
        this.maxconnections = 1000;
        this.timeout = 0;
    }

    public SocketServer(int port) {
        this.port = port;
        this.maxconnections = 1000;
        this.timeout = 0;
    }

    public void start(){
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(timeout);

            connectEvents = new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
        }
        running = serverSocket != null;
        listen();
    }

    private void listen(){
        new Thread(() -> {
            while(!serverSocket.isClosed() && serverSocket.isBound()){
                try {
                    Socket socket = serverSocket.accept();
                    connectEvents.forEach(onConnectEvent -> onConnectEvent.onConnect(socket));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void stop(){
        if(serverSocket != null && serverSocket.isClosed())
            try { serverSocket.close(); } catch (IOException e) { e.printStackTrace(); }
    }

    public SocketServer setPort(int port){
        this.port = port;
        return this;
    }

    public SocketServer setMaxConnections(int maxConnections){
        this.maxconnections = maxConnections;
        return this;
    }

    public SocketServer setTimeout(int timeout){
        this.timeout = timeout;
        return this;
    }

    public boolean isRunning() {
        return running;
    }
}
