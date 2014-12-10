import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

class NumberFocusListener implements FocusListener {
    public void focusGained(FocusEvent e) {
        final JTextField component = ((JTextField)e.getComponent());
        Runnable doSelectFocus = new Runnable() {
            @Override
            public void run() {
                component.selectAll();
            }
        };
        SwingUtilities.invokeLater(doSelectFocus);
    }

    public void focusLost(FocusEvent e) {
        //
    }
}