import javax.swing.*;

/**
 * Created by Michael on 10/12/2014.
 */
public class SpellEditWindowTester {
    public static void main(String[] args) {
        JDialog frame = new JDialog();
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        SpellEditWindow sew = new SpellEditWindow(frame);
        frame.setVisible(true);
        sew.open();
    }
}
