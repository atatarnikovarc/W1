package bchat.src.ui;

import bchat.src.model.ClientInfo;
import bchat.src.thread.impl.ServerListening;
import bchat.src.ui.controller.ChatAreaObserver;
import bchat.src.ui.controller.ChatController;
import bchat.src.util.ExtendedSocketFactory;

import javax.swing.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.StringTokenizer;
import java.util.Vector;

public class ChatClient extends Chatter {

    private boolean IAmServer;
    private boolean incomingMessageFlag;
    private ServerListening
            serverListening;


    public ChatClient(ChatController controller, String serverAddress, int port) {
        super(controller, serverAddress, port);
        connected = true;
        incomingMessageFlag = true;
        clientList = new Vector();
    }

    public void setIncomingMessageFlag(boolean value) {
        incomingMessageFlag = value;
    }

    public void run() {
        ExtendedSocketFactory socketFactory = new ExtendedSocketFactory();
        clientSocket = socketFactory.createSocket(serverAddress, port);
        serverListening = new ServerListening(clientSocket, chatController.getUserName());
        serverListening.addObserver(this);
        chatAreaObserver = new ChatAreaObserver(chatController.getChatAreaDocument());
        while (connected) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ie) {
                System.err.println("Can't waiting for 10 s.[ChatClient.run()]");
                ie.printStackTrace();
            }
        }
    }

    public synchronized void update(Observable watched, Object arg) {
        StringTokenizer tokenizer = new StringTokenizer((String) arg, ";");
        String tmp = (String) arg;
        if (tokenizer.countTokens() != 0) {
            String result = tokenizer.nextToken();
            if (result.equals("MSG")) {
                if (incomingMessageFlag) {
                    this.chatController.playMessageSound();
                }
                String tmpMessage = "";
                if (tokenizer.hasMoreTokens())
                    tmpMessage = tokenizer.nextToken();
                while (tokenizer.hasMoreTokens())
                    tmpMessage = tmpMessage.concat(";" + tokenizer.nextToken());
                if (tmp.charAt(tmp.length() - 1) == ';')
                    tmpMessage = tmpMessage.concat(";");
                printMessage(tmpMessage);
                setIncomingMessageFlag(true);
            } else if (result.equals("UPDT")) {
                if (tokenizer.hasMoreTokens()) {
                    String name = tokenizer.nextToken();
                    ClientInfo newClient = new ClientInfo(name, tokenizer.nextToken());
                    synchronized (clientList) {
                        clientList.add(newClient);
                    }
                    synchronized (usersListModel) {
                        usersListModel.addElement(name);
                    }
                    printMessage("(" + tokenizer.nextToken() + ")" + "Login: " + name);
                }
            } else if (result.equals("RMV")) {
                if (tokenizer.hasMoreTokens()) {
                    String tmpName = tokenizer.nextToken();
                    synchronized (clientList) {
                        int index = 0;
                        ClientInfo tmpClient;
                        while (true) {
                            tmpClient = (ClientInfo) clientList.get(index);
                            if (tmpName.equals(tmpClient.getName())) {
                                break;
                            } else
                                ++index;
                        }
                        clientList.remove(index);
                        synchronized (usersListModel) {
                            usersListModel.remove(index);
                        }
                        if (toListModel.getSize() != 0) {
                            index = 0;
                            synchronized (toListModel) {
                                while (true) {
                                    if (tmpName.equals(toListModel.getElementAt(index)))
                                        break;
                                    else
                                        ++index;
                                }
                                toListModel.remove(index);
                                if (toListModel.getSize() == 0) {
                                    SwingUtilities.invokeLater(new Runnable() {
                                        public void run() {
                                            toLabel.setVisible(false);
                                            toSP.setVisible(false);
                                            deleteAllButton.setVisible(false);
                                        }
                                    });
                                }
                            }
                        }
                    }
                    printMessage(tokenizer.nextToken() + "Logout: " + tmpName);
                }//if (tokenizer.hasMoreTokens())
            } else if (result.equals("OK")) {
                String time = tokenizer.nextToken();
                printMessage("(" + time + ")" + "Login: " + getUserName());
                synchronized (clientList) {
                    while (tokenizer.hasMoreTokens()) {
                        String currNick = tokenizer.nextToken();
                        ClientInfo client = new ClientInfo(currNick,
                                tokenizer.nextToken());
                        clientList.add(client);
                        synchronized (usersListModel) {
                            usersListModel.addElement(currNick);
                        }
                    }
                }
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        settingsButton.setEnabled(false);
                        startServerButton.setEnabled(false);
                        connectButton.setText("Disconnect");
                        connectButton.setToolTipText("Disconnect");
                        messageArea.setEditable(true);
                        sendButton.setEnabled(true);
                        chatterLabel.setVisible(true);
                        usersSP.setVisible(true);
                        toAllButton.setVisible(true);
                    }
                });
            } else if (result.equals("STOP")) {
                serverListening.setConnected(false);
                chatAreaObserver.setConnected(false);
                printMessage("Server was blown");
                if (toListModel.getSize() != 0) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            toListModel.removeAllElements();
                            toLabel.setVisible(false);
                            toSP.setVisible(false);
                            deleteAllButton.setVisible(false);
                        }
                    });
                }
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        chatterLabel.setVisible(false);
                        usersListModel.removeAllElements();
                        usersSP.setVisible(false);
                        toAllButton.setVisible(false);
                        sendButton.setEnabled(false);
                        messageArea.selectAll();
                        messageArea.removeAll();
                        messageArea.setEditable(false);
                        settingsButton.setEnabled(true);
                        startServerButton.setEnabled(true);
                        connectButton.setText("Connect");
                        connectButton.setToolTipText("Connect");
                        setConnected(false);
                    }
                });
                if (IAmServer) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            server = new bChat.ChatServer(futureServerAddress, currPort);
                            server.start();
                            isServerRun = true;
                            serverIPAddress = futureServerAddress;
                            settingsButton.setEnabled(false);
                            connectButton.setEnabled(false);
                            startServerButton.setText("StopServer");
                            startServerButton.setToolTipText("Stop Server");
                            messageArea.setEditable(true);
                            sendButton.setEnabled(true);
                            usersListModel.addElement(userName);
                            chatterLabel.setVisible(true);
                            usersSP.setVisible(true);
                            toAllButton.setVisible(true);
                        }
                    });
                } else {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException ie) {
                        System.err.println("Can't waiting for 10 sec. [ChatClient.update()]");
                        ie.printStackTrace();
                    }
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            client = new bChat.ChatClient(futureServerAddress, currPort);
                            client.start();
                            isClientRun = true;
                            serverIPAddress = futureServerAddress;
                        }
                    });
                }
            } else if (result.equals("END")) {
                printMessage("Autorization failed or server answer timeout");
                try {
                    clientSocket.close();
                } catch (IOException ie) {
                    System.err.println("Can't close socket[ChatClient.finalize()]");
                    ie.printStackTrace();
                }
                setConnected(false);
            } else if (result.equals("NSVR")) {
                if (tokenizer.hasMoreTokens())
                    futureServerAddress = tokenizer.nextToken();
            } else if (result.equals("ASVR")) {
                IAmServer = true;
                try {
                    futureServerAddress = InetAddress.getLocalHost().getHostAddress();
                } catch (UnknownHostException uhe) {
                    System.err.println("Can't get localhost ip-string");
                }
                String fsIP = "FSINFO;" + futureServerAddress;
                serverListening.sendMessage(fsIP);
            }
        } //if (tokenizer.hasMoreTokens())
    }

    public synchronized void sendMessage(String message) {
        char ss = '\u0000';
        String toClients = "" + ss;
        if (toListModel.getSize() != 0) {
            synchronized (toListModel) {
                for (int i = 0; i < toListModel.getSize(); i++)
                    toClients = toClients.concat(toListModel.getElementAt(i) + ",");
            }
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    toLabel.setVisible(false);
                    toListModel.removeAllElements();
                    toSP.setVisible(false);
                    deleteAllButton.setVisible(false);
                }
            });
        }
        String formattedMessage = "MSG;" + getUserName() + ";" + toClients + ";" +
                message;
        serverListening.sendMessage(formattedMessage);
    }

    public void stop() {
        alreadyStopped = true;
        isClientRun = false;
        serverListening.sendMessage("UPDT;" + getUserName());
        serverListening.setConnected(false);
        serverListening.deleteObservers();
        chatAreaObserver.setConnected(false);
        chatAreaObserver.finishThread();
        try {
            clientSocket.close();
        } catch (IOException ie) {
            System.err.println("Can't close socket [ChatClient.stop()]");
            ie.printStackTrace();
        }
        if (toListModel.getSize() != 0) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    toListModel.removeAllElements();
                    toLabel.setVisible(false);
                    toSP.setVisible(false);
                    deleteAllButton.setVisible(false);
                }
            });
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                chatterLabel.setVisible(false);
                usersListModel.removeAllElements();
                usersSP.setVisible(false);
                toAllButton.setVisible(false);
                sendButton.setEnabled(false);
                messageArea.selectAll();
                messageArea.removeAll();
                messageArea.setEditable(false);
                settingsButton.setEnabled(true);
                startServerButton.setEnabled(true);
                connectButton.setText("Connect");
                connectButton.setToolTipText("Connect");
                setConnected(false);
            }
        });
    }

    protected void finalize() {

    }
}
