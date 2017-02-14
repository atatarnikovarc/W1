package bchat.src.thread.impl;

import bchat.src.model.ClientObject;
import bchat.src.model.EventType;
import bchat.src.thread.SocketThread;

import java.io.*;
import java.net.Socket;

public class GetClientInfo extends SocketThread {

    public GetClientInfo(Socket socket) {
        super(socket, null);
        Thread clientInfoThread = new Thread(this);
        clientInfoThread.start();
    }

    @Override
    public void run() {

        username = getValue(60);

        if (username == null) {
            try {
                writer.write("BAD", 0, 3);
                writer.newLine();
                writer.flush();
                socket.close();
            } catch (IOException ie) {
                System.err.println("Can't close socket[thread.GetClientInfo.run()]");
                ie.printStackTrace();
            }
        } else {
            ClientObject clientObject = new ClientObject(null, socket.getInetAddress(),
                    username, socket);
            setChanged();
            EventType eventObject = new EventType(3, clientObject);
            notifyObservers(eventObject);
            clearChanged();
        }
    }

    protected void finalize() {
        //System.writer.println("Thread " + Thread.currentThread().getName() + "finished");
    }
}