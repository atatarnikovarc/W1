package bchat.src.thread.impl;

import bchat.src.model.EventType;
import bchat.src.thread.AbstractThread;
import bchat.src.thread.SocketThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class AcceptClient extends SocketThread {
    private ServerSocket serversocket;

    public AcceptClient(ServerSocket serversocket) {
        super(null, null);
        this.serversocket = serversocket;
        Thread acceptingThread = new Thread(this);
        acceptingThread.start();
    }

    @Override
    public void run() {
        while (isconnected) {
            try {
                socket = serversocket.accept();
            } catch (IOException ie) {
                //System.err.println("Can't accept client request[thread.AcceptClient.run()]");
                //ie.printStackTrace();

            }
            setChanged();
            EventType eventObject = new EventType(2, socket);
            notifyObservers(eventObject);
            clearChanged();
        }
    }

    public void finishThread() {
        //acceptingThread.stop();
    }

    protected void finalize() {
        //System.out.println("Thread " + Thread.currentThread().getName() + "finished");
    }
}