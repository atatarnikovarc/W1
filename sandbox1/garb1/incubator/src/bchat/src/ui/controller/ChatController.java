package bchat.src.ui.controller;

import bchat.src.ui.ChatClient;
import bchat.src.ui.ChatServer;
import bchat.src.ui.view.SettingsDialog;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

public class ChatController extends JFrame implements ActionListener, KeyListener {


    private AudioClip clip;
    private ChatClient
            client;
    private ChatServer
            server;
    private boolean alreadyStopped;
    private boolean isClientRun;
    private boolean isServerRun;
    private DefaultListModel
            usersListModel;

    private DefaultListModel
            toListModel;
    private Document chatAreaDocument;
    private JButton connectButton;
    private JButton deleteAllButton;
    private JButton exitButton;
    private JButton sendButton;
    private JButton settingsButton;
    private JButton startServerButton;
    private JButton toAllButton;
    private JLabel chatterLabel;
    private JLabel toLabel;
    private JList toList;
    private JList usersList;
    private JScrollPane chatSP;
    private JScrollPane messageSP;
    private JScrollPane toSP;
    private JScrollPane usersSP;
    private JTextPane chatArea;
    private JTextPane messageArea;


    private int currPort;
    private Integer port;
    private MouseListener
            clientsML;
    private MouseListener
            toClientsML;

    private String strPort;
    private String serverIPAddress;
    private String userName;
    private URL codebase;
    private URL complete;

    public ChatController() {
        super("Floating chat");
        initGuiControls();
        loadProperties();
        pack();
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    } //ChatController

    public void setServerIPAddress(String serverIPAddress) {
        this.serverIPAddress = serverIPAddress;
    }

    public void setCurrPort(int currPort) {
        this.currPort = currPort;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Document getChatAreaDocument() {
        return chatAreaDocument;
    }

    public JTextPane getChatArea() {
        return chatArea;
    }

    public JScrollPane getChatSP() {
        return chatSP;
    }

    public DefaultListModel getToListModel() {
        return toListModel;
    }

    public JLabel getToLabel() {
        return toLabel;
    }

    public JScrollPane getToSP() {
        return toSP;
    }

    public JButton getDeleteAllButton() {
        return deleteAllButton;
    }

    private void initGuiControls() {
        Container contentPane = getContentPane();
        SpringLayout layout = new SpringLayout();
        contentPane.setLayout(layout);
        //Mouse listeners initialization
        clientsML = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = usersList.locationToIndex(e.getPoint());
                    if (toListModel.getSize() == 0) {
                        toListModel.addElement(usersListModel.getElementAt(index));
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                toLabel.setVisible(true);
                                toSP.setVisible(true);
                                deleteAllButton.setVisible(true);
                            }
                        });
                    } else {
                        boolean is = false;
                        for (int i = 0; i < toListModel.getSize(); i++) {
                            if (usersListModel.getElementAt(index) ==
                                    toListModel.getElementAt(i)) {
                                is = true;
                                break;
                            }
                        }
                        if (!is) {
                            toListModel.addElement(usersListModel.getElementAt(index));
                        }
                    }
                }
            }
        };

        toClientsML = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = toList.locationToIndex(e.getPoint());
                    toListModel.remove(index);
                    if (toListModel.getSize() == 0) {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                deleteAllButton.setVisible(false);
                                toLabel.setVisible(false);
                                toSP.setVisible(false);
                            }
                        });
                    }
                }
            }
        };

        //chatArea initialization
        chatArea = new JTextPane();
        chatArea.setEditable(false);
        chatAreaDocument = chatArea.getDocument();
        chatSP = new JScrollPane(chatArea);
        chatSP.setPreferredSize(new Dimension(400, 300));
        chatSP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        chatSP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        chatSP.setViewportBorder(BorderFactory.createEtchedBorder());

        //messgeArea initialization
        messageArea = new JTextPane();
        messageArea.addKeyListener(this);
        messageArea.setEditable(false);
        messageSP = new JScrollPane(messageArea);
        messageSP.setPreferredSize(new Dimension(350, 100));
        messageSP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        messageSP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        messageSP.setViewportBorder(BorderFactory.createEtchedBorder());

        //usersList initialization
        usersListModel = new DefaultListModel();
        usersList = new JList(usersListModel);
        usersList.addMouseListener(clientsML);
        usersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        usersSP = new JScrollPane(usersList);
        usersSP.setPreferredSize(new Dimension(100, 260));
        usersSP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        usersSP.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        usersSP.setViewportBorder(BorderFactory.createLoweredBevelBorder());
        usersSP.setVisible(false);

        //toList initialization
        toListModel = new DefaultListModel();
        toList = new JList(toListModel);
        toList.addMouseListener(toClientsML);
        toList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        toSP = new JScrollPane(toList);
        toSP.setPreferredSize(new Dimension(100, 150));
        toSP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        toSP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        toSP.setViewportBorder(BorderFactory.createRaisedBevelBorder());
        toSP.setVisible(false);

        //other controls initialization
        JLabel messageLabel = new JLabel("Message");
        ////////////////////////////////
        sendButton = new JButton("Send Message");
        sendButton.setToolTipText("Send Message");
        sendButton.addActionListener(this);
        sendButton.setEnabled(false);
        ////////////////////////////////
        settingsButton = new JButton("Settings");
        settingsButton.setToolTipText("Settings");
        settingsButton.addActionListener(this);
        ////////////////////////////////
        startServerButton = new JButton("StartServer");
        startServerButton.setToolTipText("Start Server");
        startServerButton.addActionListener(this);
        ////////////////////////////////
        connectButton = new JButton("Connect");
        connectButton.setToolTipText("Connect");
        connectButton.addActionListener(this);
        ////////////////////////////////
        exitButton = new JButton("Exit");
        exitButton.setToolTipText("Exit");
        exitButton.addActionListener(this);
        ////////////////////////////////
        toLabel = new JLabel("To");
        chatterLabel = new JLabel("Chatter");
        toLabel.setVisible(false);
        chatterLabel.setVisible(false);
        ////////////////////////////////
        toAllButton = new JButton("toAll");
        toAllButton.setToolTipText("Send to all");
        toAllButton.addActionListener(this);
        toAllButton.setVisible(false);
        ////////////////////////////////
        deleteAllButton = new JButton("Delete all");
        deleteAllButton.setToolTipText("Delete all");
        deleteAllButton.addActionListener(this);
        deleteAllButton.setVisible(false);

        //lay out components
        Spring currX;
        Spring currY;

        //chatSP
        contentPane.add(chatSP);
        SpringLayout.Constraints chatSpCons = layout.getConstraints(chatSP);
        currX = Spring.constant(6);
        currY = Spring.constant(6);
        chatSpCons.setX(currX);
        chatSpCons.setY(currY);

        //usersSp
        contentPane.add(usersSP);
        SpringLayout.Constraints usersSpCons = layout.getConstraints(usersSP);
        currX = Spring.sum(chatSpCons.getConstraint("East"), Spring.constant(15));
        currY = Spring.sum(chatSpCons.getY(), Spring.constant(15));
        usersSpCons.setX(currX);
        usersSpCons.setY(currY);

        //settingsButton
        contentPane.add(settingsButton);
        SpringLayout.Constraints settingsButtonCons = layout.getConstraints(
                settingsButton);
        currX = Spring.sum(usersSpCons.getConstraint("East"), Spring.constant(15));
        currY = Spring.sum(usersSpCons.getY(), Spring.constant(235));
        settingsButtonCons.setX(currX);
        settingsButtonCons.setY(currY);

        //startServerButton
        contentPane.add(startServerButton);
        SpringLayout.Constraints startServerButtonCons = layout.getConstraints(
                startServerButton);
        currX = settingsButtonCons.getX();
        currY = Spring.sum(settingsButtonCons.getConstraint("South"),
                Spring.constant(15));
        startServerButtonCons.setX(currX);
        startServerButtonCons.setY(currY);

        //connectButton
        contentPane.add(connectButton);
        SpringLayout.Constraints connectButtonCons = layout.getConstraints(
                connectButton);
        currX = startServerButtonCons.getX();
        currY = Spring.sum(startServerButtonCons.getConstraint("South"),
                Spring.constant(15));
        connectButtonCons.setX(currX);
        connectButtonCons.setY(currY);

        //exitButton
        contentPane.add(exitButton);
        SpringLayout.Constraints exitButtonCons = layout.getConstraints(exitButton);
        currX = connectButtonCons.getX();
        currY = Spring.sum(connectButtonCons.getConstraint("South"),
                Spring.constant(15));
        exitButtonCons.setX(currX);
        exitButtonCons.setY(currY);

        //messageLabel
        contentPane.add(messageLabel);
        SpringLayout.Constraints messageLabelCons = layout.getConstraints(messageLabel);
        currX = chatSpCons.getX();
        currY = Spring.sum(chatSpCons.getConstraint("South"), Spring.constant(5));
        messageLabelCons.setX(currX);
        messageLabelCons.setY(currY);

        //messageSP
        contentPane.add(messageSP);
        SpringLayout.Constraints messageSpCons = layout.getConstraints(messageSP);
        currX = chatSpCons.getX();
        currY = Spring.sum(chatSpCons.getConstraint("South"), Spring.constant(25));
        messageSpCons.setX(currX);
        messageSpCons.setY(currY);

        //sendButton
        contentPane.add(sendButton);
        SpringLayout.Constraints sendButtonCons = layout.getConstraints(sendButton);
        currX = Spring.sum(messageSpCons.getConstraint("East"),
                Spring.constant(10));
        currY = messageSpCons.getY();
        sendButtonCons.setX(currX);
        sendButtonCons.setY(currY);

        //chatterLabel
        contentPane.add(chatterLabel);
        SpringLayout.Constraints chatterLblCons = layout.getConstraints(chatterLabel);
        currX = usersSpCons.getX();
        currY = Spring.sum(usersSpCons.getY(), Spring.minus(Spring.constant(15)));
        chatterLblCons.setX(currX);
        chatterLblCons.setY(currY);

        //toSP
        contentPane.add(toSP);
        SpringLayout.Constraints toSpCons = layout.getConstraints(toSP);
        currX = Spring.sum(usersSpCons.getConstraint("East"), Spring.constant(10));
        currY = usersSpCons.getY();
        toSpCons.setX(currX);
        toSpCons.setY(currY);

        //toLabel
        contentPane.add(toLabel);
        SpringLayout.Constraints toLblCons = layout.getConstraints(toLabel);
        currX = toSpCons.getX();
        currY = Spring.sum(toSpCons.getY(), Spring.minus(Spring.constant(15)));
        toLblCons.setX(currX);
        toLblCons.setY(currY);

        //toAllButton
        contentPane.add(toAllButton);
        SpringLayout.Constraints toAllBtnCons = layout.getConstraints(toAllButton);
        currX = usersSpCons.getX();
        currY = Spring.sum(usersSpCons.getConstraint("South"), Spring.constant(6));
        toAllBtnCons.setX(currX);
        toAllBtnCons.setY(currY);

        //deleteAllButton
        contentPane.add(deleteAllButton);
        SpringLayout.Constraints deleteAllBtnCons = layout.getConstraints(deleteAllButton);
        currX = toSpCons.getX();
        currY = Spring.sum(toSpCons.getConstraint("South"), Spring.constant(6));
        deleteAllBtnCons.setX(currX);
        deleteAllBtnCons.setY(currY);

        //lay out main frame
        SpringLayout.Constraints pCons = layout.getConstraints(contentPane);
        pCons.setConstraint(SpringLayout.EAST, Spring.sum(Spring.constant(275),
                Spring.constant(380)));
        pCons.setConstraint(SpringLayout.SOUTH,
                Spring.sum(Spring.constant(220), Spring.constant(220)));
    }

    private void loadProperties() {
        try {
            System.out.println(new File(".").getAbsolutePath());
            FileReader in = new FileReader(".//incubator//src//bchat//config//bchat.properties");
            BufferedReader br = new BufferedReader(in);
            userName = br.readLine();
            serverIPAddress = br.readLine();
            strPort = br.readLine();
            currPort = Integer.valueOf(strPort);
            in.close();
        } catch (FileNotFoundException fnfe) {
            System.err.println("Couldn't find file");
        } catch (IOException ie) {
            System.err.println("Couldn't read line of the file");
        }

        try {
            codebase = new URL("file:" + ".//incubator//src//bchat//data//");
            complete = new URL(codebase, "Chat.wav");
        } catch (MalformedURLException mue) {
            System.err.println("Can't create URL resource");
            mue.printStackTrace();
        }
        clip = Applet.newAudioClip(complete);
        ImageIcon image = new ImageIcon(".//incubator//src//bchat//data//bold.gif");
        setIconImage(image.getImage());
    }

    //ActionListener implementation
    public void actionPerformed(ActionEvent ae) {
        final ChatController controller = this;
        if (ae.getActionCommand().equals("delete")) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    sendMessage(messageArea.getText());
                    try {
                        messageArea.getDocument().remove(0, messageArea.getDocument().getLength());
                    } catch (BadLocationException ble) {
                        System.err.println("Can't delete message text");
                    }
                }
            });
        } else if (ae.getActionCommand().equals("toAll")) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    if (toListModel.getSize() == 0) {
                        for (int i = 0; i < usersListModel.getSize(); i++)
                            toListModel.addElement(usersListModel.getElementAt(i));
                        toLabel.setVisible(true);
                        toSP.setVisible(true);
                        deleteAllButton.setVisible(true);
                    } else {
                        toListModel.removeAllElements();
                        for (int i = 0; i < usersListModel.getSize(); i++)
                            toListModel.addElement(usersListModel.getElementAt(i));
                    }
                }
            });
        } else if (ae.getActionCommand().equals("Delete all")) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    toListModel.removeAllElements();
                    toLabel.setVisible(false);
                    deleteAllButton.setVisible(false);
                    toSP.setVisible(false);
                }
            });
        } else if (ae.getActionCommand().equals("Settings")) {
            final JFrame tmpFrame = this;
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    new SettingsDialog((ChatController) tmpFrame, tmpFrame, serverIPAddress,
                            strPort, userName);
                }
            });
        } else if (ae.getActionCommand().equals("StartServer")) {

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    server = new ChatServer(controller, serverIPAddress, port);
                    new Thread(server).start();
                    isServerRun = true;
                    isClientRun = false;
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
        } else if (ae.getActionCommand().equals("StopServer")) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    server.stop();
                }
            });
        } else if (ae.getActionCommand().equals("Connect")) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    client = new ChatClient(controller, serverIPAddress, port);
                    new Thread(client).start();
                    isClientRun = true;
                    isServerRun = false;
                }
            });
        } else if (ae.getActionCommand().equals("Disconnect")) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    client.stop();
                }
            });
        } else if (ae.getActionCommand().equals("Exit")) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    if (isServerRun)
                        server.stop();
                    else if (isClientRun)
                        client.stop();
                    dispose();
                    System.exit(0);
                }
            });
        }
    }//ActionListener

    //KeyListener implementation
    public void keyPressed(KeyEvent ke) {
        boolean ctrlInfo = ke.isControlDown();
        if ((ke.getKeyCode() == KeyEvent.VK_ENTER) && ctrlInfo) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    sendMessage(messageArea.getText());
                    try {
                        messageArea.getDocument().remove(0, messageArea.getDocument().getLength());
                    } catch (BadLocationException ble) {
                        System.err.println("Can't delete message text");
                    }
                }
            });
        }
    }

    public void keyReleased(KeyEvent ke) {
    }

    public void keyTyped(KeyEvent ke) {
    }

    public String getUserName() {
        return userName;
    }

    public void playMessageSound() {
        clip.play();
    }

    public void sendMessage(String message) {
        if (isServerRun) {
            server.setServerMessageFlag(true);
            server.sendMessage(message);
        } else {
            client.setIncomingMessageFlag(false);
            client.sendMessage(message);
        }
    }

    protected void finalize() {
        try {
            super.finalize();
        } catch (Throwable e) {
            System.out.print("");
        }

        if (alreadyStopped)
            System.out.println("Bye!");
        else {
            if (isServerRun)
                server.stop();
            else
                client.stop();
        }
    }
}
