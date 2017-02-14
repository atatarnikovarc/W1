package bchat.src.ui.controller;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class ChatAreaObserver implements Runnable {
    private boolean connected;
    private int limit;
    private int removeIndex;
    private Document document;

    public ChatAreaObserver(Document doc) {
        Thread observerThread = new Thread(this, "Observer Thread");
        document = doc;
        connected = true;
        limit = 20000;
        observerThread.start();
    }

    public void setConnected(boolean value) {
        connected = value;
    }

    public void run() {
        while (connected) {
            try {
                Thread.sleep(120000);
            } catch (InterruptedException ie) {
                System.err.println("Can't waiting for 30 sec. [ui.ChatAreaObserver.run()]");
                ie.printStackTrace();
            }
            synchronized (document) {
                int currentLength = document.getLength();
                if (currentLength > limit) {
                    removeIndex = currentLength - limit;

                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            try {
                                document.remove(0, removeIndex);
                            } catch (BadLocationException ble) {
                                System.err.println("Can't remove extra content at chat area");
                            }
                        }
                    });
                }
            }
        }
    }

    public void finishThread() {
        //observerThread.stop();
    }

    protected void finalize() {
        //System.out.println("Thread " + Thread.currentThread().getName() + "finished");
    }
}