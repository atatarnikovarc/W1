package bchat.src.model;

import bchat.src.thread.impl.ClientListening;

import java.net.InetAddress;
import java.net.Socket;

public class ClientObject {
    private ClientListening client;
    private String nickName;
    private InetAddress clientIPAddress;
    private Socket clientSocket;

    public ClientObject(ClientListening thread, InetAddress IPAddress,
                        String nickName, Socket clientSocket) {
        this.client = thread;
        this.clientIPAddress = IPAddress;
        this.nickName = nickName;
        this.clientSocket = clientSocket;
    }

    public InetAddress getClientIP() {
        return clientIPAddress;
    }

    public void setNickName(String nick) {
        nickName = nick;
    }

    public String getNickName() {
        return nickName;
    }

    public void setClienThread(ClientListening thread) {
        client = thread;
    }

    public ClientListening getClient() {
        return client;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }
}