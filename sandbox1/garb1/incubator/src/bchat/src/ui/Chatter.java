package bchat.src.ui;

import bchat.src.model.ClientObject;
import bchat.src.ui.controller.ChatAreaObserver;
import bchat.src.ui.controller.ChatController;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.net.Socket;
import java.util.Observer;
import java.util.Vector;

public abstract class Chatter implements Observer, Runnable {
    protected boolean connected;
    protected Socket clientSocket;
    protected ChatAreaObserver chatAreaObserver;
    protected ChatController chatController;
    protected String serverAddress;
    protected int port;
    protected Vector<ClientObject> clientList;
    private final static String NEW_LINE_CHARACTER = "\n";

    protected Chatter(ChatController controller, String serverAddress, int port) {
        this.chatController = controller;
        this.serverAddress = serverAddress;
        this.port = port;
    }

    protected abstract void sendMessage(String message);

    protected abstract void stop();

    protected void setConnected(boolean value) {
        this.connected = value;
    }

    protected synchronized void printMessage(String message) {
        final ChatController controller = this.chatController;
        final String tmpMessage = message;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    JTextPane pane = controller.getChatArea();
                    pane.getDocument().insertString(pane.getDocument().getLength(),
                            tmpMessage + NEW_LINE_CHARACTER, null);
                } catch (BadLocationException ble) {
                    System.err.println("Can't insert text at chat area");
                }
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        JScrollPane pane = controller.getChatSP();
                        int index = pane.getVerticalScrollBar().getModel().getMaximum();
                        pane.getVerticalScrollBar().setValue(index);
                    }
                });
            }
        });
    }
}
