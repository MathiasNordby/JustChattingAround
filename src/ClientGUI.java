import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Mathias on 12-09-2016.
 */
public class ClientGUI extends JFrame implements ActionListener {

    private JButton sendButton;
    private JButton loginButton;
    private JTextArea chatBoxArea;
    private JTextArea onlineUsersArea;
    private JTextField writingTextField;
    private JScrollPane scrollPaneTextbox;
    private JScrollPane scrollPaneInfobox;
    private JLabel loginAsLabel;
    private JLabel enterUsername;
    private JLabel yourIPiS;
    private JLabel portLoggedInOn;
    private JLabel usersOnlineLabel;
    private JLabel enterIP;
    private JLabel enterPort;
    private int usersOnline;
    private JTextField usernameField;
    private JTextField ipField;
    private JTextField portField;
    private String ip;
    private int port;

    private static final int FRAME_WIDTH = 1000;
    private static final int FRAME_HEIGHT = 900;

    private Client client;

    public ClientGUI() {
        createGUI();
    }

    private void createGUI() {

        //Login as: Username, IP and Port
        loginAsLabel = new JLabel();
        loginAsLabel.setBounds(25, 25, 300, 100);

        yourIPiS = new JLabel("IP: " + ip);
        yourIPiS.setBounds(275, 25, 300, 100);
        try {
            ip = InetAddress.getLocalHost().toString();
        } catch(UnknownHostException e) {
            System.out.println("Couldn't get IP-adress" + e);
        }

        portLoggedInOn = new JLabel("Port: " + port);
        portLoggedInOn.setBounds(555, 25, 300, 100);


        //Username
        enterUsername = new JLabel("Username:");
        enterUsername.setBounds(25, 10, 165, 25);

        usernameField = new JTextField("Insert Name");
        usernameField.setBounds(25,30,165,25);
        usernameField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                if(usernameField.getText().equals("Insert Name")) {
                    usernameField.setText("");
                    repaint();
                    revalidate();
                }
            }
        });


        //IP: Label and Field
        enterIP = new JLabel("IP-Adress:");
        enterIP.setBounds(275, 10, 165, 25);

        try {
            ip = InetAddress.getLocalHost().toString();
        } catch(UnknownHostException e) {
            System.out.println("Couldn't get IP-adress" + e);
        }
        ipField = new JTextField(20);
        ipField.setBounds(275,30,165,25);
        ipField.setText(ip);
        ipField.setEnabled(false);


        //Port: Label and Field
        enterPort = new JLabel("Port-Number:");
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

        //Login Button
        loginButton = new JButton(new AbstractAction("Login") {
            public void actionPerformed(ActionEvent e) {
                if (loginButton.getText().equals("Login")) {
                    loginButton.setText("Logout");
                    loginAsLabel.setText("Logged in as: " + usernameField.getText());

                    loginAsLabel.setVisible(true);
                    yourIPiS.setVisible(true);
                    portLoggedInOn.setVisible(true);
                    enterUsername.setVisible(false);
                    enterIP.setVisible(false);
                    enterPort.setVisible(false);
                    usernameField.setVisible(false);
                    ipField.setVisible(false);
                    portField.setVisible(false);

                    login();
                } else {

                    loginButton.setText("Login");

                    loginAsLabel.setVisible(false);
                    yourIPiS.setVisible(false);
                    portLoggedInOn.setVisible(false);
                    enterUsername.setVisible(true);
                    enterIP.setVisible(true);
                    enterPort.setVisible(true);
                    usernameField.setVisible(true);
                    ipField.setVisible(true);
                    portField.setVisible(true);

                    client.disconnect();
                }
            }
        });
        loginButton.setOpaque(false);
        loginButton.setContentAreaFilled(false);
        loginButton.setBorderPainted(true);
        loginButton.setBounds(800,25,125,25);

        //Visiblity for logged in info
        loginAsLabel.setVisible(false);
        yourIPiS.setVisible(false);
        portLoggedInOn.setVisible(false);

        //Users Online: Label, and TextArea
        usersOnlineLabel = new JLabel();
        usersOnline = 3204;
        usersOnlineLabel.setText("Online Users: " + usersOnline);
        usersOnlineLabel.setBounds(800, 25, 300, 100);

        onlineUsersArea = new JTextArea();
        onlineUsersArea.setEnabled(false);

        //Chat Area
        chatBoxArea = new JTextArea();
        chatBoxArea.setEnabled(false);

        scrollPaneTextbox = new JScrollPane(chatBoxArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPaneTextbox.setBounds(25, 100, 700, 650);

        // Write to chat Field
        writingTextField = new JTextField();
        writingTextField.setBounds(25, 775, 700, 70);

        scrollPaneInfobox = new JScrollPane(onlineUsersArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPaneInfobox.setBounds(750, 100, 225, 650);

        //Send Button
        sendButton = new JButton();
        sendButton.setText("Send");
        sendButton.setOpaque(false);
        sendButton.setContentAreaFilled(false);
        sendButton.setBorderPainted(true);
        sendButton.setBounds(750, 775, 225, 70);
        sendButton.addActionListener(this);

        //Add all the stuff to the Panel
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        panel.add(sendButton);
        panel.add(writingTextField);
        panel.add(scrollPaneTextbox);
        panel.add(scrollPaneInfobox);
        panel.add(loginAsLabel);
        panel.add(yourIPiS);
        panel.add(portLoggedInOn);
        panel.add(loginButton);
        panel.add(usernameField);
        panel.add(enterUsername);
        panel.add(enterPort);
        panel.add(portField);
        panel.add(enterIP);
        panel.add(ipField);
        panel.add(usersOnlineLabel);
        this.setContentPane(panel);
        this.setSize(FRAME_WIDTH, FRAME_HEIGHT);

    }
    //Show timestamp
    private String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String time = sdf.format(new Date()) + " ";
        return time;
    }

    //Sending the message - Action Performed
    @Override
    public void actionPerformed(ActionEvent e) {
        client.sendMessage(new Message(client.getUsername(), Message.DATA, writingTextField.getText()));
        writingTextField.setText("");
    }

    public void writeTextToGUI(String text){
        chatBoxArea.append(getTime() + text + "\n");
    }


    public void login(){
        client = new Client("localhost", "THIS_IS_USER" , port, this);
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                client.start();
            }
        }).start();
    }

    public void listClients(ArrayList<ActiveClient> clients){
        for(ActiveClient client: clients){
            System.out.println("OMG!!");
            onlineUsersArea.append(client.toString());
            System.out.println(client.toString());
        }
    }
}

