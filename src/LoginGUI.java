import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Tanja on 12/09/2016.
 */
public class LoginGUI extends JFrame implements ActionListener {

    private String ip;

    public LoginGUI() {
        placeComponents();
    }

    private void placeComponents() {
        JFrame loginFrame = new JFrame("Login Screen");
        loginFrame.setSize(400,300);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(null);
        loginFrame.add(loginPanel);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setVisible(true);

        //Username
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setBounds(20,20,80,25);
        loginPanel.add(usernameLabel);

        JTextField usernameField = new JTextField(20);
        usernameField.setBounds(110,20,165,25);
        loginPanel.add(usernameField);

        //IP
        JLabel ipAddressLabel = new JLabel("IP Address");
        ipAddressLabel.setBounds(20,80,80,25);
        loginPanel.add(ipAddressLabel);
        try {
            ip = InetAddress.getLocalHost().toString();
        } catch(UnknownHostException e) {
            System.out.println("Couldn't get IP-adress" + e);
        }
        JTextField ipAddressField = new JTextField(20);
        ipAddressField.setBounds(110,80,165,25);
        ipAddressField.setText(ip);
        ipAddressField.setEnabled(false);
        loginPanel.add(ipAddressField);

        //Port
        JLabel portLabel = new JLabel("Port Number");
        portLabel.setBounds(20,140,80,25);
        loginPanel.add(portLabel);

        JTextField portField = new JTextField(20);
        portField.setBounds(110,140,165,25);
        loginPanel.add(portField);

        //Login button
        JButton loginButton = new JButton("Login");
        loginButton.setBounds(20,210,80,25);
        loginPanel.add(loginButton);

    }


    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        this.dispose();
        JFrame serverGUI = new ServerGUI();
        serverGUI.setVisible(true);
    }
}
