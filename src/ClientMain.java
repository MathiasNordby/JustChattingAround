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
    }
}