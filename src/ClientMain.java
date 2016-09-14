import javax.swing.*;

/**
 * Created by Mathias on 12-09-2016.
 */
public class ClientMain {

    public static void main(String[] args) {
        JFrame frame = new ClientGUI();
        frame.setTitle("Client Frame");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);

        /*
        //JFrame med navn og størrelse - og luk på kryds
        JFrame chatFrame = new JFrame("Login Screen");
        chatFrame.setSize(1000,900);
        chatFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel chatPanel = new JPanel();
        chatFrame.add(chatPanel);
        placeComponents(chatPanel);

        chatFrame.setVisible(true); */
    }
}