import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private JTextField usernameField;
    private JTextField ipField;
    private JTextField portField;
    private boolean success;

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

        yourIPiS = new JLabel("IP: ");
        yourIPiS.setBounds(275, 25, 300, 100);

        portLoggedInOn = new JLabel("Port: ");
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

        ipField = new JTextField(20);
        ipField.setBounds(275,30,165,25);
        ipField.setText("Insert Ip");
        ipField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                if(ipField.getText().equals("Insert Ip")) {
                    ipField.setText("");
                    repaint();
                    revalidate();
                }
            }
        });


        //Port: Label and Field
        enterPort = new JLabel("Port-Number:");
        enterPort.setBounds(555, 10, 165, 25);

        portField = new JTextField(5);
        portField.setBounds(555,30,165,25);
        portField.setText("Insert Port");
        portField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                if( portField.getText().equals("Insert Port")) {
                    portField.setText("");
                    repaint();
                    revalidate();
                }
            }
        });
        portField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                if(!Character.isDigit(e.getKeyChar())){
                    e.consume();
                }
            }
        });

        //Login Button
        loginButton = new JButton(new AbstractAction("Login") {
            public void actionPerformed(ActionEvent e) {
                if (loginButton.getText().equals("Login")) {
                    if(loginCheck()){
                        login();
                        if(success){
                            loginButton.setText("Logout");
                            loginAsLabel.setText("Logged in as: " + usernameField.getText());
                            yourIPiS.setText("Connected ip: " + ipField.getText());
                            portLoggedInOn.setText("Port :" + portLoggedInOn.getText());
                            loginAsLabel.setVisible(true);
                            yourIPiS.setVisible(true);
                            portLoggedInOn.setVisible(true);
                            enterUsername.setVisible(false);
                            enterIP.setVisible(false);
                            enterPort.setVisible(false);
                            usernameField.setVisible(false);
                            ipField.setVisible(false);
                            portField.setVisible(false);
                            usersOnlineLabel.setVisible(true);
                        }
                    }
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
                    client.sendMessage(new Message(Message.QUIT));
                    client.disconnect();
                    usersOnlineLabel.setVisible(false);
                    onlineUsersArea.setText("");
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
        int port = Integer.parseInt(portField.getText());
        String username = usernameField.getText();
        String ip = ipField.getText();

        success = true;

        client = new Client(ip, username , port, this);

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                if(!client.start()){
                    failed();
                }

            }
        }).start();
    }
    public void failed (){
        success = false;
    }

    public void listClients(ArrayList<ActiveClient> clients){
        onlineUsersArea.setText("");
        usersOnlineLabel.setText("Online Users: " + clients.size());
        for(ActiveClient client: clients){
            onlineUsersArea.append(client.toString());
        }
    }

    public boolean loginCheck(){
        if(portField.getText().equals("Insert Port") || portField.getText().equals("")){
            JOptionPane.showMessageDialog(this, "Insert valid port number. Note only numbers allowed.\nAnd numbers between 0-65535.");
            portField.setText("");
            return false;
        }
        if(!(Integer.parseInt(portField.getText()) <= 65535 && Integer.parseInt(portField.getText()) >= 0 )){
            JOptionPane.showMessageDialog(this, "Insert valid port number. Note only numbers allowed.\nAnd numbers between 0-65535.");
            portField.setText("");
            return false;
        }
        if(ipField.getText().equals("Insert Ip") || ipField.getText().equals("")){
            JOptionPane.showMessageDialog(this, "Insert ip. Like 192.168.46.34 or localhost");
            ipField.setText("");
            return false;
        }
        if(usernameField.getText().equals("Insert Name") || usernameField.getText().equals("")){
            JOptionPane.showMessageDialog(this, "Insert valid username");
            usernameField.setText("");
            return false;
        }

        if(!ipVerify(ipField.getText())){
            JOptionPane.showMessageDialog(this, "Insert ip. Like 192.168.46.34 or localhost");
            ipField.setText("");
            return false;
        }
        return true;
    }

    public boolean ipVerify(String ip){
        String IPADDRESS_PATTERN = "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
        Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
        Matcher matcher = pattern.matcher(ip);
        if (matcher.matches() || ip.equals("localhost")) {

            return true;
        } else{
            return false;
        }
    }
}

