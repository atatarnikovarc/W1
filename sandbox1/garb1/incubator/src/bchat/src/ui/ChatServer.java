package bchat.src.ui;

import bchat.src.model.ClientObject;
import bchat.src.model.EventType;
import bchat.src.thread.impl.AcceptClient;
import bchat.src.thread.impl.AutorizeClient;
import bchat.src.thread.impl.ClientListening;
import bchat.src.thread.impl.GetClientInfo;
import bchat.src.ui.controller.ChatAreaObserver;
import bchat.src.ui.controller.ChatController;
import bchat.src.util.ExtendedSocketFactory;

import javax.swing.*;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;


public class ChatServer extends Chatter {

    private boolean serverAppointed;
    private boolean serverMessageFlag;
    private BufferedReader in;
    private BufferedWriter out;
    private Calendar calendar;
    private ClientObject clientObject;
    private EventType eventObject;
    private ServerSocket serverSocket;
    private String outgoingMessage;
    private AcceptClient
            acceptClient;
    private AutorizeClient
            autorizeClient;
    private ClientListening
            clientListening;
    private GetClientInfo
            getClientInfo;

    public ChatServer(ChatController chatController, String serverAddress, int port) {
        super(chatController, serverAddress, port);
        connected = true;
        clientList = new Vector();
    }

    public void setServerMessageFlag(boolean value) {
        serverMessageFlag = value;
    }

    public void run() {
        ExtendedSocketFactory socketFactory = new ExtendedSocketFactory();
        serverSocket = socketFactory.createServerSocket(serverAddress, port);
        acceptClient = new AcceptClient(serverSocket);
        acceptClient.addObserver(this);
        chatAreaObserver = new ChatAreaObserver(this.chatController.getChatAreaDocument());
        try {
            clientObject = new ClientObject(null, InetAddress.getLocalHost(),
                    this.chatController.getUserName(), null);
        } catch (UnknownHostException uhe) {
            System.err.println("Can't get local address");
            uhe.printStackTrace();
        }
        clientList.add(clientObject);
        while (connected) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ie) {
                System.err.println("Can't waiting for 10 s.[ChatServer.run()]");
                ie.printStackTrace();
            }
        }
    }

    public synchronized void sendMessage(String message) {
        final DefaultListModel toListModel = this.chatController.getToListModel();
        synchronized (clientList) {
            calendar = Calendar.getInstance();
            String time = "(" + calendar.get(Calendar.HOUR) + ":" +
                    calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND) +
                    ")";
            String formattedMessage;
            if (serverMessageFlag) {
                String toClients = "";
                if (this.chatController.getToListModel().getSize() != 0) {
                    synchronized (toListModel) {
                        for (int i = 0; i < toListModel.getSize(); i++)
                            toClients = toClients.concat(toListModel.getElementAt(i) + ",");
                    }
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            chatController.getToLabel().setVisible(false);
                            toListModel.removeAllElements();
                            chatController.getToSP().setVisible(false);
                            chatController.getDeleteAllButton().setVisible(false);
                        }
                    });
                }
                formattedMessage = time + getUserName() + ":" + toClients + message;
                printMessage(formattedMessage);
            } else {
                formattedMessage = message;
            }
            if (clientList.size() > 1) {
                ClientObject tmpObject;
                ClientListening client;
                String formattedMSG = "MSG;" + formattedMessage;
                for (int i = 1; i < clientList.size(); i++) {
                    tmpObject = (ClientObject) clientList.get(i);
                    client = tmpObject.getClient();
                    client.sendMessage(formattedMSG);
                }
            }
        }
    }

    public synchronized void update(Observable watched, Object arg) {
        eventObject = (EventType) arg;
        switch (eventObject.getType()) {
            case 1:
                outgoingMessage = (String) eventObject.getArgObject();
                calendar = Calendar.getInstance();
                String time = "(" + calendar.get(Calendar.HOUR) + ":" +
                        calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND) +
                        ")";
                StringTokenizer tokenizer = new StringTokenizer(outgoingMessage, ";");
                if (tokenizer.countTokens() != 0) {
                    String formattedMessage;
                    String result = tokenizer.nextToken();
                    if (result.equals("MSG")) {
                        setServerMessageFlag(false);
                        formattedMessage = time + tokenizer.nextToken() + ":";
                        if (tokenizer.hasMoreTokens())
                            formattedMessage = formattedMessage.concat(tokenizer.nextToken());
                        formattedMessage = formattedMessage.replace('\u0000', ' ');
                        if (tokenizer.hasMoreTokens())
                            formattedMessage = formattedMessage.concat(tokenizer.nextToken());
                        if (tokenizer.hasMoreTokens()) {
                            formattedMessage = formattedMessage.concat(";");
                            while (tokenizer.hasMoreTokens())
                                formattedMessage = formattedMessage.concat(tokenizer.nextToken() + ";");
                            formattedMessage = formattedMessage.substring(0,
                                    formattedMessage.length() - 1);
                        }
                        printMessage(formattedMessage);
                        this.chatController.playMessageSound();
                        sendMessage(formattedMessage);
                    } else if (result.equals("UPDT")) {
                        String name = tokenizer.nextToken();
                        synchronized (clientList) {
                            int index = 0;
                            ClientObject tmpClient;
                            while (true) {
                                tmpClient = (ClientObject) clientList.get(index);
                                if (name.equals(tmpClient.getNickName())) {
                                    break;
                                } else
                                    ++index;
                            }
                            String tmpIP;
                            tmpIP = tmpClient.getClientIP().getHostAddress();
                            clientList.remove(index);
                            usersListModel.remove(index);
                            if (futureServerAddress.equals(tmpIP)) {
                                if (clientsList.size() > 1) {
                                    tmpClient = (ClientObject) clientsList.get(1);
                                    tmpClient.getClient().sendMessage("ASVR");
                                    String ip;
                                    ip = tmpClient.getClientIP().getHostAddress();
                                    futureServerAddress = ip;
                                    ClientListening t;
                                    for (int i = 2; i < clientList.size(); i++) {
                                        tmpClient = (ClientObject) clientList.get(i);
                                        t = tmpClient.getClient();
                                        t.sendMessage("NSVR;" + futureServerAddress);
                                    }
                                } else {
                                    serverAppointed = false;
                                }
                            }
                            if (toListModel.getSize() != 0) {
                                index = 0;
                                synchronized (toListModel) {
                                    while (true) {
                                        if (name.equals(toListModel.getElementAt(index)))
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
                            ClientListening tmp;
                            String removeMessage = "RMV;" + name + ";" + time;
                            for (int j = 1; j < clientList.size(); j++) {
                                tmpClient = (ClientObject) clientList.get(j);
                                tmp = tmpClient.getClient();
                                tmp.sendMessage(removeMessage);
                            }
                        }//synchronized (clientsList)
                        printMessage(time + "Logout: " + name);
                    } else if (result.equals("FSINFO")) {
                        if (tokenizer.hasMoreTokens())
                            futureServerAddress = tokenizer.nextToken();
                    }// else if ()
                }//if (tokenizer.hasMoreTokens())
                break;
            case 2:
                getClientInfo = new GetClientInfo(
                        (Socket) eventObject.getArgObject());
                getClientInfo.addObserver(this);
                break;
            case 3:
                autorizeClient = new AutorizeClient(clientList,
                        (ClientObject) eventObject.getArgObject());
                autorizeClient.addObserver(this);
                break;
            case 4:
                clientObject = (ClientObject) eventObject.getArgObject();
                clientSocket = clientObject.getClientSocket();
                try {
                    out = new BufferedWriter(new OutputStreamWriter(
                            clientSocket.getOutputStream()));
                    in = new BufferedReader(new InputStreamReader(
                            clientSocket.getInputStream()));
                } catch (IOException ie) {
                    System.err.println("Can't create socket streams[ChatServer.update()]");
                    ie.printStackTrace();
                }

                if (clientObject.getNickName() == null) {
                    try {
                        out.write("BAD", 0, 3);
                        out.newLine();
                        out.flush();
                        clientSocket.close();
                    } catch (IOException ie) {
                        System.err.println("Can't close client socket[ChatServer.update()]");
                        ie.printStackTrace();
                    }
                } else {
                    clientListening = new ClientListening(clientSocket,
                            clientObject.getNickName());
                    clientListening.addObserver(this);
                    clientObject.setClienThread(clientListening);
                    synchronized (clientList) {
                        clientList.add(clientObject);
                    }
                    synchronized (usersListModel) {
                        usersListModel.addElement(
                                clientObject.getNickName());
                    }
                    String clients;
                    ClientObject tmpObj;
                    clients = "OK";
                    synchronized (clientList) {
                        Calendar calendar1 = Calendar.getInstance();
                        String loginTime = calendar1.get(Calendar.HOUR) + ":" +
                                calendar1.get(Calendar.MINUTE) + ":" + calendar1.get(Calendar.SECOND);
                        clients = clients.concat(";" + loginTime);
                        for (int i = 0; i < clientList.size(); i++) {
                            tmpObj = (ClientObject) clientList.get(i);
                            clients = clients.concat(";" + tmpObj.getNickName() + ";" +
                                    tmpObj.getClientIP().getHostAddress());
                        }
                        String updateMessage = "UPDT;";
                        updateMessage = updateMessage.concat(
                                clientObject.getNickName() + ";"
                                        + clientObject.getClientIP().getHostAddress() + ";" + loginTime);
                        String info;
                        info = "(" + loginTime + ")" + "Login: " + clientObject.getNickName();
                        printMessage(info);
                        ClientObject tmpClient;
                        ClientListening client;
                        for (int i = 1; i < clientList.size() - 1; i++) {
                            tmpClient = (ClientObject) clientList.get(i);
                            client = tmpClient.getClient();
                            client.sendMessage(updateMessage);
                        }
                    }
                    try {
                        out.write(clients, 0, clients.length());
                        out.newLine();
                        out.flush();
                    } catch (IOException ie) {
                        System.err.println("Can't send 'OK'[ChatServer.update()]");
                        ie.printStackTrace();
                    }
                    if (serverAppointed) {
                        String fsInfo;
                        fsInfo = "NSVR;" + futureServerAddress;
                        try {
                            out.write(fsInfo, 0, fsInfo.length());
                            out.newLine();
                            out.flush();
                        } catch (IOException ie) {
                            System.err.println("Can't send fsInfo [ChatServer.update()]");
                            ie.printStackTrace();
                        }
                    } else {
                        //futureServerAddress = clientSocket.getInetAddress().getHostAddress();
                        serverAppointed = true;
                        String appointingMessage;
                        appointingMessage = "ASVR"; //+ futureServerAddress;
                        try {
                            out.write(appointingMessage, 0, appointingMessage.length());
                            out.newLine();
                            out.flush();
                        } catch (IOException ie) {
                            System.err.println("Can't send appointedMessage [ChatServer.update()]");
                            ie.printStackTrace();
                        }
                    }
                }
                break;
            default:
                System.err.println("Bad event type");
                System.exit(0);
        }
    }

    public void stop() {
        alreadyStopped = true;
        acceptClient.setConnected(false);
        acceptClient.deleteObservers();
        acceptClient.finishThread();
        chatAreaObserver.setConnected(false);
        chatAreaObserver.finishThread();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (toListModel.getSize() != 0) {
                    toListModel.removeAllElements();
                    toLabel.setVisible(false);
                    toSP.setVisible(false);
                    deleteAllButton.setVisible(false);
                }
                chatterLabel.setVisible(false);
                usersListModel.removeAllElements();
                usersSP.setVisible(false);
                toAllButton.setVisible(false);
                messageArea.selectAll();
                messageArea.removeAll();
                messageArea.setEditable(false);
                sendButton.setEnabled(false);
                settingsButton.setEnabled(true);
                startServerButton.setText("StartServer");
                startServerButton.setToolTipText("Start Server");
                connectButton.setEnabled(true);
            }
        });
        ClientObject tmpObj;
        for (int i = 1; i < clientList.size(); i++) {
            tmpObj = (ClientObject) clientList.get(i);
            tmpObj.getClient().sendMessage("STOP");
            tmpObj.getClient().setConnected(false);
            tmpObj.getClient().deleteObservers();
        }
        try {
            serverSocket.close();
        } catch (IOException ie) {
            System.err.println("Can't close server socket[ChatServer.finalize()]");
            ie.printStackTrace();
        }
        setConnected(false);
        System.gc();
    }

    protected void finalize() {

    }
}
