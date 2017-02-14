package bchat.src.thread;

import java.io.*;
import java.net.Socket;

public abstract class SocketThread extends AbstractThread {
    protected Socket socket;
    protected BufferedReader reader;
    protected BufferedWriter writer;
    protected boolean isconnected;
    protected String username;

    protected SocketThread(Socket socket, String username) {
        this.socket = socket;
        this.username = username;
        this.isconnected = true;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(
                    socket.getOutputStream()));
        } catch (IOException ie) {
            System.out.println("Can't create i/o streams[thread.ServerListening.run()]");
            ie.printStackTrace();
        }
    }

    public void setConnected(boolean value) {
        this.isconnected = value;
    }

    protected String getValue(int timeout) {
        String result = null;
        int timerValue = 0;
        while (true) {
            if (timerValue > timeout)
                break;
            try {
                result = reader.readLine();
            } catch (IOException ie) {
                System.err.println("I can't read autorization result![thread.ServerListening.run()]");
                ie.printStackTrace();
            }
            if (result == null) {
                timerValue += 2;
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ie) {
                    System.err.println("Can't waiting for 2 s.[thread.ServerListening.run()]");
                    ie.printStackTrace();
                }

            } else
                break;
        }
        return result;
    }
}
