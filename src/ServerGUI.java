import javax.swing.*;
import java.awt.event.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Mathias on 12-09-2016.
 */
public class ServerGUI extends JFrame implements ActionListener {

    private JButton sendButton;
    private JButton createServer;
    private JTextArea textboxArea;
    private JTextArea infoboxArea;
    private JTextField textField;
    private JTextField adminNameField;
    private JTextField ipField;
    private JTextField portField;
    private JScrollPane scrollPaneTextbox;
    private JScrollPane scrollPaneInfobox;
    private JLabel loginAsLabel;
    private JLabel ipLabel;
    private JLabel portLabel;
    private JLabel usersOnlineLabel;
    private int usersOnline;
    private String ip;

    private static final int FRAME_WIDTH = 1000;
    private static final int FRAME_HEIGHT = 900;

    private Server server;

    public ServerGUI() {
        createGUI();
    }

    private void createGUI() {
        sendButton = new JButton();
        sendButton.setText("Send");
        sendButton.setOpaque(false);
        sendButton.setContentAreaFilled(false);
        sendButton.setBorderPainted(true);
        sendButton.setBounds(750, 775, 225, 70);
        sendButton.addActionListener(this);

        loginAsLabel = new JLabel();
        loginAsLabel.setBounds(25, 25, 300, 100);

        ipLabel = new JLabel();
        try {
            ip = InetAddress.getLocalHost().toString();
        } catch(UnknownHostException e) {
            System.out.println("Couldn't get IP-adress" + e);
        }
        ipLabel.setText("IP: " + ip);
        ipLabel.setBounds(275, 25, 300, 100);

        portLabel = new JLabel();
        portLabel.setBounds(555, 25, 300, 100);

        usersOnlineLabel = new JLabel();
        usersOnline = 3204;
        usersOnlineLabel.setText("Online Users: " + usersOnline);
        usersOnlineLabel.setBounds(800, 25, 300, 100);

        textboxArea = new JTextArea();
        textboxArea.setEnabled(false);

        infoboxArea = new JTextArea();
        infoboxArea.setEnabled(false);

        textField = new JTextField();
        textField.setBounds(25, 775, 700, 70);

        //Username
        JLabel enterAdminName = new JLabel();
        enterAdminName.setText("Admin Name:");
        enterAdminName.setBounds(25, 10, 165, 25);

        adminNameField = new JTextField(12);
        adminNameField.setBounds(25,30,165,25);
        adminNameField.setText("Insert Name");
        adminNameField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                if(adminNameField.getText().equals("Insert Name")) {
                    adminNameField.setText("");
                    repaint();
                    revalidate();
                }
            }
        });


        //IP
        JLabel enterIpAdress = new JLabel();
        enterIpAdress.setText("IP-Adress:");
        enterIpAdress.setBounds(275, 10, 165, 25);

        try {
            ip = InetAddress.getLocalHost().toString();
        } catch(UnknownHostException e) {
            System.out.println("Couldn't get IP-adress" + e);
        }
        ipField = new JTextField(20);
        ipField.setBounds(275,30,165,25);
        ipField.setText(ip);
        ipField.setEnabled(false);

        //Port
        JLabel enterPort = new JLabel();
        enterPort.setText("Port-Number:");
        enterPort.setBounds(555, 10, 165, 25);

        portField = new JTextField(5);
        portField.setBounds(555,30,165,25);
        portField.setText("Insert Port");
        portField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                if(portField.getText().equals("Insert Port")) {
                    portField.setText("");
                    repaint();
                    revalidate();
                }
            }
        });

        //Login button
        createServer = new JButton(new AbstractAction("Create Server") {
            public void actionPerformed(ActionEvent e) {
                if(createServer.getText().equalsIgnoreCase("Create Server")) {
                    createServer.setText("Disconnect Server");
                    loginAsLabel.setText("Logged in as: " + adminNameField.getText());
                    portLabel.setText("Port: " + portField.getText());
                    loginAsLabel.setVisible(true);
                    ipLabel.setVisible(true);
                    portLabel.setVisible(true);
                    enterAdminName.setVisible(false);
                    enterIpAdress.setVisible(false);
                    enterPort.setVisible(false);
                    adminNameField.setVisible(false);
                    ipField.setVisible(false);
                    portField.setVisible(false);
                    login();
                } else {
                    createServer.setText("Create Server");
                    loginAsLabel.setVisible(false);
                    ipLabel.setVisible(false);
                    portLabel.setVisible(false);
                    enterAdminName.setVisible(true);
                    enterIpAdress.setVisible(true);
                    enterPort.setVisible(true);
                    adminNameField.setVisible(true);
                    ipField.setVisible(true);
                    portField.setVisible(true);
                    server.stop();
                }
            }
        });
        createServer.setOpaque(false);
        createServer.setContentAreaFilled(false);
        createServer.setBorderPainted(true);
        createServer.setBounds(775,25,175,25);

        //Visiblity
        loginAsLabel.setVisible(false);
        ipLabel.setVisible(false);
        portLabel.setVisible(false);


        scrollPaneTextbox = new JScrollPane(textboxArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPaneTextbox.setBounds(25, 100, 700, 650);

        scrollPaneInfobox = new JScrollPane(infoboxArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPaneInfobox.setBounds(750, 100, 225, 650);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        panel.add(sendButton);
        panel.add(textField);
        panel.add(scrollPaneTextbox);
        panel.add(scrollPaneInfobox);
        panel.add(loginAsLabel);
        panel.add(ipLabel);
        panel.add(portLabel);
        panel.add(usersOnlineLabel);
        panel.add(adminNameField);
        panel.add(ipField);
        panel.add(portField);
        panel.add(createServer);
        panel.add(enterAdminName);
        panel.add(enterIpAdress);
        panel.add(enterPort);
        this.setContentPane(panel);
        this.setSize(FRAME_WIDTH, FRAME_HEIGHT);

    }

    private String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String time = sdf.format(new Date()) + " ";
        return time;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        server.broadcast(new Message(server.getServerUsername(), Message.DATA, textField.getText()));
        textField.setText("");
    }

    public void writeTextToGUI(String text){
        textboxArea.append(getTime() + text + "\n");
    }

    public void login(){
        int port = Integer.parseInt(portField.getText());
        String adminName = adminNameField.getText();
        server = new Server(port, this, adminName);

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                server.start();
            }
        }).start();
    }

}
