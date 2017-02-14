package bchat.src.thread.impl;

import bchat.src.model.EventType;
import bchat.src.thread.SocketThread;

import java.io.*;
import java.net.Socket;

public class ClientListening extends SocketThread {

    public ClientListening(Socket socket, String username) {
        super(socket, username);
        Thread listeningThread = new Thread(this);
        listeningThread.start();
    }

    @Override
    public void run() {

        String message;
        while (isconnected) {
            try {
                message = reader.readLine();
            } catch (IOException ie) {
                setChanged();
                String updateMessage = "UPDT;" + username;
                EventType eventObject = new EventType(1, updateMessage);
                notifyObservers(eventObject);
                clearChanged();
                isconnected = false;
                break;
            }

            if (message == null) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ie) {
                    System.err.println("Can't waiting 2 s.[thread.ClientListening.run()]");
                    ie.printStackTrace();
                }

            } else {
                setChanged();
                EventType eventObject = new EventType(1, message);
                notifyObservers(eventObject);
                clearChanged();
            }
        }
    }

    public void sendMessage(String message) {
        try {
            writer.write(message, 0, message.length());
            writer.newLine();
            writer.flush();
        } catch (IOException ie) {
            //System.err.println("Can't send message to client[thread.ClientListening.sendMessage()]");
            //ie.printStackTrace();
        }
    }

    protected void finalize() {
        //System.writer.println("Thread " + Thread.currentThread().getName() + "finished");
        /*try {
            writer.write("quit", 0, 4);
            writer.newLine();
            writer.flush();
        } catch (IOException ie) {
          System.err.println("Can't send bye message[thread.ClientListening.finalize()]");
          ie.printStackTrace();
        }*/
    }
}