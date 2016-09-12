import javax.swing.*;

/**
 * Created by mikkel on 12-09-2016.
 */
public class DoubleMain implements Runnable{

    JFrame theFrame;


    public DoubleMain(JFrame f) {
        this.theFrame = f;
    }

    public static void main(String[] arguments) {
        JFrame serverFrame = new ServerGUI();
        serverFrame.setTitle("ServerFrame");

        JFrame clientFrame = new ClientGUI();
        clientFrame.setTitle("ClientFrame");

        Thread t1 = new Thread(new DoubleMain(serverFrame));
        Thread t2 = new Thread(new DoubleMain(clientFrame));

        t1.start();
        t2.start();
    }

    @Override
    public void run() {
        theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        theFrame.setVisible(true);
    }

}