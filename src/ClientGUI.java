import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Mathias on 12-09-2016.
 */
public class ClientGUI extends JFrame implements ActionListener {

    private JButton sendButton;
    private JTextArea textboxArea;
    private JTextArea infoboxArea;
    private JTextField textField;
    private JToolBar toolBar;
    private JScrollPane scrollPaneTextbox;
    private JScrollPane scrollPaneInfobox;
    private JLabel loginAsLabel;
    private JLabel ipLabel;
    private JLabel portLabel;
    private String loggedAsName;
    private String ip;
    private int port;

    private static final int FRAME_WIDTH = 1000;
    private static final int FRAME_HEIGHT = 900;

    private Client client;

    public ClientGUI() {
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
        loggedAsName = "<NameExample>";
        loginAsLabel.setText("Logged in as: " + loggedAsName);
        loginAsLabel.setBounds(25, 25, 300, 100);

        ipLabel = new JLabel();
        ip = "22.155.139";
        ipLabel.setText("IP: " + ip);
        ipLabel.setBounds(275, 25, 100, 100);

        portLabel = new JLabel();
        port = 80;
        portLabel.setText("Port: " + port);
        portLabel.setBounds(400, 25, 100, 100);

        textboxArea = new JTextArea();
        textboxArea.setEnabled(false);

        infoboxArea = new JTextArea();
        infoboxArea.setEnabled(false);

        textField = new JTextField();
        textField.setBounds(25, 775, 700, 70);

        toolBar = new JToolBar();
        toolBar.setBounds(0, 0, 1000, 30);
        toolBar.add(new JButton(new AbstractAction("Login") {
            public void actionPerformed(ActionEvent e) {
                login();
            }
        }));
        toolBar.add(new JButton(new AbstractAction("LogOut") {
            public void actionPerformed(ActionEvent e) {
                if(client != null){
                    client.sendMessage(new Message(Message.QUIT));
                    client.disconnect();
                }
            }
        }));
        toolBar.add(new JButton(new AbstractAction("Settings") {
            public void actionPerformed(ActionEvent e) {
                System.out.println("test");
            }
        }));

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
        panel.add(toolBar);
        panel.add(scrollPaneTextbox);
        panel.add(scrollPaneInfobox);
        panel.add(loginAsLabel);
        panel.add(ipLabel);
        panel.add(portLabel);
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
        client.sendMessage(new Message(client.getUsername(), Message.DATA,textField.getText()));
        textField.setText("");
    }

    public void writeTextToGUI(String text){
        textboxArea.append(getTime() + text + "\n");
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
}

