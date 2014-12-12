import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 * Created by Michael on 07/12/2014.
 */
public class SpellcastingEditWindow {

    JDialog dialog;
    JTabbedPane tabbedPane = new JTabbedPane();
    ArrayList<JTextField> tabNames = new ArrayList<JTextField>();
    ArrayList<SpellRef> spellRefList = new ArrayList<SpellRef>();

    public void open(Window parent) {
        dialog = new JDialog(parent, "Edit Player Spells", Dialog.ModalityType.APPLICATION_MODAL);
        JPanel topPanel = new JPanel();
        JPanel bottomPanel = new JPanel();
        JButton saveButton = new JButton("Save Spells");
        JButton cancelButton = new JButton("Cancel");
        saveButton.addActionListener(new SaveSpellsListener());
        //cancel button listener goes here
        bottomPanel.add(saveButton);
        bottomPanel.add(cancelButton);

        JButton newClassButton = new JButton("New Class");
        newClassButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel newClassPanel = newSpellClass();
                tabbedPane.add("New Class", newClassPanel);
                tabbedPane.setSelectedIndex(tabbedPane.indexOfComponent(newClassPanel));
            }
        });
        JButton delClassButton = new JButton("Remove Class");
        delClassButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelection();
            }
        });
        topPanel.add(newClassButton);
        topPanel.add(delClassButton);

        if (tabbedPane.getComponents().length == 0) {
            tabbedPane.add("New Class", newSpellClass());
        }

        dialog.add(BorderLayout.CENTER, tabbedPane);
        dialog.add(BorderLayout.SOUTH, bottomPanel);
        dialog.add(BorderLayout.NORTH, topPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }

    private JPanel newSpellClass() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        JTextArea[] spellLists = new JTextArea[10];
        JScrollPane[] spellScrollers = new JScrollPane[10];
        JSpinner[] slotSpinners = new JSpinner[9];
        JTextField classNameField = new JTextField(12);
        tabNames.add(classNameField);
        classNameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                updateTabNames();
            }
        });
        classNameField.setText("New Class");


        for (int i = 0; i < 10; i++) {
            spellLists[i] = new JTextArea(10,20);
            spellLists[i].setLineWrap(true);
            spellLists[i].setWrapStyleWord(true);
            spellScrollers[i] = new JScrollPane(spellLists[i]);
            if (i < 9) slotSpinners[i] = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
            spellScrollers[i].setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        }
        c.gridx=0;
        c.gridy=0;
        c.anchor=GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Class: "),c);
        c.gridx++;
        panel.add(classNameField,c);
        c.gridx--;
        c.gridy++;
        panel.add(new JLabel("Casting Ability: "),c);
        c.gridx++;
        panel.add(new JComboBox(ManagerConstants.SHORT_ABILITY_NAMES),c);
        c.gridx--;
        c.gridy++;
        panel.add(new JLabel("Cantrips"),c);
        c.gridy+=2;
        c.gridwidth = 2;
        panel.add(spellScrollers[0],c);
        c.gridwidth = 1;
        c.gridy-=2;
        for (int i = 0; i < 4; i++) {
            c.gridx+=2;
            panel.add(new JLabel("Level " + (i + 1)),c);
            c.gridy++;
            panel.add(new JLabel("Slots: "),c);
            c.gridx++;
            panel.add(slotSpinners[i], c);
            c.gridx--;
            c.gridy++;
            c.gridwidth=2;
            panel.add(spellScrollers[i+1],c);
            c.gridwidth=1;
            c.gridy-=2;
        }
        c.gridy+=3;
        c.gridx=0;
        for (int i = 5; i < 10; i++) {
            panel.add(new JLabel("Level " + (i)),c);
            c.gridy++;
            panel.add(new JLabel("Slots: "),c);
            c.gridx++;
            panel.add(slotSpinners[i-1], c);
            c.gridx--;
            c.gridy++;
            c.gridwidth=2;
            panel.add(spellScrollers[i],c);
            c.gridwidth=1;
            c.gridy-=2;
            c.gridx+=2;
        }
        return panel;
    }

    private void updateTabNames() {
        for (int i = 0; i < tabNames.size(); i ++) {
            tabbedPane.setTitleAt(i, tabNames.get(i).getText());
        }
    }

    private void deleteSelection() {
        if (tabbedPane.getSelectedIndex() >= 0) {
            Object[] options = {"Delete", "Cancel"};
            int choice = JOptionPane.showOptionDialog(dialog, "Delete the selected class?", "Confirm deletion",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
            if (choice == 0) { // if the user selects "Yes" to deleting the selected class
                tabNames.remove(tabbedPane.getSelectedIndex());
                tabbedPane.remove(tabbedPane.getSelectedComponent());
            }
        }
    }

    public ArrayList<SpellRef> getSpellRefList() {
        return spellRefList;
    }

    public class SaveSpellsListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String[] spells = new String[10];
            int[] slots = new int[9];

            for (Component comp : tabbedPane.getComponents()) {
                SpellRef newSpellRef = new SpellRef();
                JPanel panel = (JPanel) comp;
                int spellsCount = 0;
                int slotCount = 0;
                int spellAbility = 0;
                String className = "";

                for (Component subComp : panel.getComponents()) {
                    if (subComp instanceof JSpinner) {
                        slots[slotCount] = (Integer) ((JSpinner) subComp).getValue();
                        slotCount++;
                    }
                    else if (subComp instanceof JScrollPane) {
                        JScrollPane subCompScroll = (JScrollPane) subComp;
                        JTextArea subText = (JTextArea) (subCompScroll.getViewport().getComponent(0));
                        spells[spellsCount] = subText.getText();
                        spellsCount++;
                    }
                    else if (subComp instanceof JComboBox) {
                        spellAbility = ((JComboBox) subComp).getSelectedIndex();
                    }
                    else if (subComp instanceof JTextField) {
                        className = ((JTextField) subComp).getText();
                    }
                }

                newSpellRef.setSlots(slots);
                newSpellRef.setSpells(spells);
                newSpellRef.setCastAbility(spellAbility);
                newSpellRef.setCasterClass(className);
                newSpellRef.testPrint();
                spellRefList.add(newSpellRef);
            }
        }
    }

}
