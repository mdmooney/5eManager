import javax.swing.*;

/**
 * Created by Michael on 01/12/2014.
 */
public class PlayerEditWindowTester {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Player Edit Window Tester");
        PlayerEditWindow pew = new PlayerEditWindow();

        frame.setSize(50, 50);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
        pew.open(frame);
        frame.dispose();
    }
}
