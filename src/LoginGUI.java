import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Tanja on 12/09/2016.
 */
public class LoginGUI extends JFrame {


    public static void main(String[] args) {

        //JFrame med navn og størrelse - og luk på kryds
        JFrame loginFrame = new JFrame("Login Screen");
        loginFrame.setSize(400,300);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel loginFanel = new JPanel();
        loginFanel.setLayout(new FlowLayout());
        loginFrame.add(loginFanel);
        placeComponents(loginFanel);

        loginFrame.setLocationRelativeTo(null);
        loginFrame.setVisible(true);
    }

    private static void placeComponents(JPanel loginPanel) {
        loginPanel.setLayout(null);

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

        JTextField ipAddressField = new JTextField(20);
        ipAddressField.setBounds(110,80,165,25);
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


}
