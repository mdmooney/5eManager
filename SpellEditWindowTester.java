import javax.swing.*;

/**
 * Created by Michael on 10/12/2014.
 */
public class SpellEditWindowTester {
    public static void main(String[] args) {
        JDialog frame = new JDialog();
        SpellEditWindow sew = new SpellEditWindow(frame);
        frame.setVisible(true);
        sew.open();
    }
}
