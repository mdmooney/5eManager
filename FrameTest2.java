/**
 * Created by Michael on 19/11/2014.
 */

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;

public class FrameTest2 {
    JTextField testField;
    JCheckBox checkBox;
    JSpinner testSpin;
    SpinnerNumberModel model1;
    SpinnerNumberModel model2;

    public static void main(String[] args) {
        FrameTest2 ft2 = new FrameTest2();
        ft2.go();
    }

    public void go() {
        JFrame frame = new JFrame("Frame Test 2");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        JTabbedPane tabbedPane = new JTabbedPane();
        JTextPane textPane = new JTextPane();
        JTextPane textPane2 = new JTextPane();
        textPane.setEditable(false);
        HTMLEditorKit kit = new HTMLEditorKit();
        HTMLDocument doc = new HTMLDocument();
        textPane.setEditorKit(kit);
        textPane.setDocument(doc);

        String testSpell = "Scorching Ray";
        textPane.setText("<a href='" + testSpell + "'>"+ testSpell + "</a>");
        textPane.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    System.out.println(e.getDescription());
                }
            }
        });

        textPane2.setText("Test pane 2");


        //panel.add(textPane);
        tabbedPane.add("Text Pane 1", textPane);
        tabbedPane.add("Text Pane 2", textPane2);
        tabbedPane.setPreferredSize(new Dimension(300,300));
        panel.add(tabbedPane);

        frame.add(BorderLayout.CENTER, panel);
        frame.pack();
        frame.setVisible(true);
    }

}