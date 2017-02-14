package bchat.src.ui.view;

import bchat.src.ui.controller.ChatController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class SettingsDialog extends JDialog implements ActionListener,
        KeyListener {
    private JTextField ip;
    private JTextField username;
    private JTextField port;
    private final ChatController chatController;

    public SettingsDialog(ChatController controller, JFrame owner, String ip, String port, String nick) {
        super(owner, "Settings", true);
        Container dialogContentPane = getContentPane();
        SpringLayout dialogLayout = new SpringLayout();
        dialogContentPane.setLayout(dialogLayout);

        this.chatController = controller;
        this.ip = new JTextField(ip, 15);
        this.ip.selectAll();
        this.ip.addKeyListener(this);
        this.port = new JTextField(port, 5);
        this.port.selectAll();
        this.port.addKeyListener(this);
        username = new JTextField(nick, 20);
        username.selectAll();
        username.addKeyListener(this);
        JButton applyButton = new JButton("Apply");
        applyButton.addActionListener(this);
        applyButton.setToolTipText("Apply");
        JLabel addressLabel = new JLabel("Address");
        JLabel portLabel = new JLabel("Port");
        JLabel nickLabel = new JLabel("Nick");

        dialogContentPane.add(this.ip);
        dialogContentPane.add(this.port);
        dialogContentPane.add(username);
        dialogContentPane.add(applyButton);
        dialogContentPane.add(addressLabel);
        dialogContentPane.add(portLabel);
        dialogContentPane.add(nickLabel);

        SpringLayout.Constraints ipFieldCons = dialogLayout.getConstraints(
                this.ip);
        SpringLayout.Constraints portCons = dialogLayout.getConstraints(
                this.port);
        SpringLayout.Constraints nickCons = dialogLayout.getConstraints(
                username);
        SpringLayout.Constraints applyCons = dialogLayout.getConstraints(
                applyButton);
        SpringLayout.Constraints addrLblCons = dialogLayout.getConstraints(
                addressLabel);
        SpringLayout.Constraints portLblCons = dialogLayout.getConstraints(
                portLabel);
        SpringLayout.Constraints nickLblCons = dialogLayout.getConstraints(
                nickLabel);
        Spring currX = Spring.constant(18);
        Spring currY = Spring.constant(18);
        ipFieldCons.setX(currX);
        ipFieldCons.setY(currY);

        currX = Spring.sum(ipFieldCons.getConstraint("East"), Spring.constant(20));
        currY = ipFieldCons.getY();
        portCons.setX(currX);
        portCons.setY(currY);

        currX = ipFieldCons.getX();
        currY = Spring.sum(ipFieldCons.getConstraint("South"), Spring.constant(18));
        nickCons.setX(currX);
        nickCons.setY(currY);

        currX = Spring.sum(nickCons.getX(), Spring.constant(85));
        currY = Spring.sum(nickCons.getConstraint("South"), Spring.constant(10));
        applyCons.setX(currX);
        applyCons.setY(currY);

        currX = ipFieldCons.getX();
        currY = Spring.sum(ipFieldCons.getY(), Spring.minus(Spring.constant(18)));
        addrLblCons.setX(currX);
        addrLblCons.setY(currY);

        currX = portCons.getX();
        currY = Spring.sum(portCons.getY(), Spring.minus(Spring.constant(18)));
        portLblCons.setX(currX);
        portLblCons.setY(currY);

        currX = nickCons.getX();
        currY = Spring.sum(nickCons.getY(), Spring.minus(Spring.constant(18)));
        nickLblCons.setX(currX);
        nickLblCons.setY(currY);

        SpringLayout.Constraints dialogCons = dialogLayout.getConstraints(
                dialogContentPane);
        dialogCons.setConstraint(SpringLayout.EAST, Spring.sum(Spring.constant(130),
                Spring.constant(130)));
        dialogCons.setConstraint(SpringLayout.SOUTH, Spring.sum(currY,
                Spring.constant(80)));
        pack();
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("Apply"))
            applySettings();
    }

    public void keyPressed(KeyEvent ke) {
        if (ke.getKeyCode() == KeyEvent.VK_ENTER)
            applySettings();
    }

    public void keyTyped(KeyEvent ke) {
    }

    public void keyReleased(KeyEvent ke) {
    }

    private void applySettings() {
        this.chatController.setServerIPAddress(ip.getText());
        this.chatController.setCurrPort(Integer.valueOf(port.getText()));
        this.chatController.setUserName(username.getText());
        this.dispose();
    }
}
