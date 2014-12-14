/**
 * Created by Michael on 19/11/2014.
 */

 //changing something else for git tests for a change
 
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import javax.swing.UIManager.*;

public class FrameTest2 {
    JTextField testField;
    JCheckBox checkBox;
    JSpinner testSpin;
    SpinnerNumberModel model1;
    SpinnerNumberModel model2;
    JFrame frame;
    JTable table;

    public static void main(String[] args) {
        FrameTest2 ft2 = new FrameTest2();
        ft2.go();
    }

    public void go() {
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, you can set the GUI to another look and feel.
        }
        frame = new JFrame("Frame Test 2");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        JTabbedPane tabbedPane = new JTabbedPane();
        JTextPane textPane = new JTextPane();
        JTextPane textPane2 = new JTextPane();
        JPanel tablePanel = new JPanel();
        textPane.setEditable(false);
        HTMLEditorKit kit = new HTMLEditorKit();
        HTMLDocument doc = new HTMLDocument();
        textPane.setEditorKit(kit);
        textPane.setDocument(doc);


//        String[] columnNames = {"First Name",
//                "Last Name",
//                "Sport",
//                "# of Years",
//                "Vegetarian"};

        Object[] columnNames = {"Column1","Column2"};
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Col1");
        model.addColumn("Col2");
        model.addRow(new Object[]{"entry 1", "entry 2"});
        model.addRow(new Object[]{"entry a", "entry b"});

        table = new JTable(model);
        table.getTableHeader().setReorderingAllowed(false);
        JScrollPane tableScroll = new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        tablePanel.add(tableScroll);

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                System.out.println(table.getValueAt(table.getSelectedRow(), 0));
            }
        });

        String testSpell = "Disintegrate";
        textPane.setText("<a href='" + testSpell + "'>"+ testSpell + "</a>");
        textPane.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    spellLinkTest(e.getDescription());
                }
            }
        });

        textPane2.setText("Test pane 2");


        //panel.add(textPane);
        tabbedPane.add("Text Pane 1", textPane);
        tabbedPane.add("Text Pane 2", textPane2);

        tabbedPane.add("Table Test", tableScroll);
        tabbedPane.setPreferredSize(new Dimension(300,300));
        panel.add(tabbedPane);

        frame.add(BorderLayout.CENTER, panel);
        frame.pack();
        frame.setVisible(true);
    }

    private void spellLinkTest(String spellName) {
        try {
            PickSpell ps = new PickSpell();
            ps.jumpTo(spellName, frame);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
