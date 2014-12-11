import javax.swing.*;

/**
 * Created by Michael on 11/12/2014.
 */
public class PickSpellTester {
    public static void main(String[] args) {
        JDialog frame = new JDialog();
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        PickSpell ps = new PickSpell();
        frame.setVisible(true);
        ps.open(frame);
    }
}
