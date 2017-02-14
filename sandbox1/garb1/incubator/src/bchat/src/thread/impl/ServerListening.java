package bchat.src.thread.impl;

import bchat.src.thread.SocketThread;

import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;

public class ServerListening extends SocketThread {
    public ServerListening(Socket socket, String username) {
        super(socket, username);
        Thread listeningThread = new Thread(this, "Message listening thread");
        listeningThread.start();
    }

    @Override
    public void run() {

        try {
            writer.write(username, 0, username.length());
            writer.newLine();
            writer.flush();
        } catch (IOException ie) {
            System.err.println("Can't send nickname[thread.ServerListening.run()]");
            ie.printStackTrace();
        }

        //get protocol message
        String result = getValue(60);

        String message;
        StringTokenizer tokenizer = new StringTokenizer(result, ";");
        if (result != null) {
            if (tokenizer.nextToken().equals("OK")) {
                setChanged();
                notifyObservers(result);
                clearChanged();

                while (isconnected) {
                    try {
                        message = reader.readLine();
                    } catch (IOException ie) {
                        //System.err.println("Can't read from input stream[thread.ServerListening.run()]");
                        //ie.printStackTrace();
                        setChanged();
                        notifyObservers("STOP");
                        clearChanged();
                        isconnected = false;
                        break;
                    }

                    if (message == null) {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException ie) {
                            System.err.println("Can't wait 2 s.[thread.ServerListening.run()]");
                            ie.printStackTrace();
                        }

                    } else {
                        setChanged();
                        notifyObservers(message);
                        clearChanged();

                        //System.writer.println("new message");
                    }
                }
            } else {
                message = "END";
                setChanged();
                notifyObservers(message);
                clearChanged();

            }
        } else {
            message = "END";
            setChanged();
            notifyObservers(message);
            clearChanged();
            //System.err.println("\n" + "Server answer timeout");
        }
    }

    public void sendMessage(String message) {
        try {
            writer.write(message, 0, message.length());
            writer.newLine();
            writer.flush();
        } catch (IOException ie) {
            //System.err.println("Can't send message[thread.ServerListening.sendMessage()]");
            //ie.printStackTrace();

        }
    }


    protected void finalize() {
        try {
            super.finalize();
            writer.close();
            reader.close();
            socket.close();
        } catch (IOException ie) {
            System.err.println("Can't close i/o streams[thread.ServerListening.finalize()]");
            ie.printStackTrace();
        } catch (Throwable e) {
            System.out.print("");
        }
        //System.writer.println("Thread " + Thread.currentThread().getName() + "finished");
    }
}