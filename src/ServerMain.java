import javax.swing.*;

/**
 * Created by mikkel on 12-09-2016.
 */
public class ServerMain {

    public static void main(String[] args) {
        JFrame frame = new ServerGUI();
        frame.setTitle("ServerFrame");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}
