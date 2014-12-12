import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 * Created by Michael on 11/12/2014.
 */
public class PickSpell {
    private JTextArea blankTextArea = new JTextArea(20, 20);
    private LibraryManager libMan = new LibraryManager(LibraryManager.LibType.SPELL);
    private ArrayList<LibEntry> entryList;
    private JList libList;
    private JDialog dialog;
    private JTextField searchField = new JTextField(18);
    private final Dimension LIST_DIMENSION = new Dimension(280, 280);
    private JPanel spellInfoPanel = new JPanel();
    private String refName;


    public void open (Window parent) {
        entryList = libMan.getEntryList();
        blankTextArea.setEditable(false);

        JPanel superPanel = new JPanel(new GridBagLayout());

        dialog = new JDialog(parent, "Spell Library", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        spellInfoPanel.setLayout(new BoxLayout(spellInfoPanel, BoxLayout.X_AXIS)); //this panel works well with a box layout since it's really just a big block of text
        JPanel libPanel = new JPanel(new GridBagLayout());
        JPanel menuPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints(); //constraints for the library panel
        GridBagConstraints c2 = new GridBagConstraints(); //constraints for the menu (button) panel

        //create buttons for the library menu panel, add listeners, put them in the panel
        JButton newButton = new JButton("New");
        JButton editButton = new JButton("Edit");
        JButton copyButton = new JButton("Copy");
        JButton delButton = new JButton("Delete");
        menuPanel.add(newButton, c2);
        menuPanel.add(editButton, c2);
        menuPanel.add(copyButton, c2);
        menuPanel.add(delButton, c2);
        newButton.addActionListener(new NewSpellListener());
        editButton.addActionListener(new EditSpellListener());
        copyButton.addActionListener(new CopySpellListener());
        delButton.addActionListener(new DelSpellListener());

        //set up list and scroller for library JList
        libList = new JList();
        libList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                fetchBlockPanel();
            }
        });
        JScrollPane libScroller = new JScrollPane(libList);
        libScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        libScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        libScroller.setPreferredSize(LIST_DIMENSION);

        //refresh the library list
        refreshLibraryList();

        //set up search field
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                searchSort();
            }
        });

        //add stuff to the library panel
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weighty = 0;
        libPanel.add(menuPanel, c);
        c.gridy++;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.BOTH;
        c.gridheight = GridBagConstraints.RELATIVE;
        c.weighty = 1;
        libPanel.add(libScroller, c);
        c.gridwidth = 2;
        c.gridy++;
        c.weighty = 0;
        c.gridheight = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.HORIZONTAL;
        libPanel.add(searchField, c);

        //add panels to the dialog
//        dialog.add(BorderLayout.WEST, libPanel);
//        dialog.add(BorderLayout.EAST, spellInfoPanel);

        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0;
        c.weighty = 1;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.VERTICAL;
        c.anchor = GridBagConstraints.NORTHWEST;
        superPanel.add(libPanel, c);
        c.gridx++;
        c.weightx = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.BOTH;
        //c.anchor=GridBagConstraints.NORTHEAST;
        superPanel.add(spellInfoPanel, c);

        dialog.add(BorderLayout.CENTER, superPanel);

        //pack and display the dialog
        fetchBlockPanel();
        blankTextArea.setPreferredSize(new Dimension(350, 120));
        dialog.pack();
        dialog.setMinimumSize(dialog.getSize());

        if (refName != null) {
            boolean foundEntry = false;
            for (LibEntry entry : entryList) {
                if (entry.getName().equalsIgnoreCase(refName)) {
                    libList.setSelectedValue(entry, true);
                    spellInfoPanel.requestFocus();
                    dialog.setLocationRelativeTo(parent);
                    dialog.setVisible(true);
                    foundEntry = true;
                    break;
                }
            }
            if (!foundEntry) {
                Object[] options = {"Open Library", "Cancel"};
                int choice = JOptionPane.showOptionDialog(dialog, "Spell not found in the library. Open the library anyway?", "Spell not found",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                if (choice == 0) { // if the user chooses to open the library anyway
                    dialog.setLocationRelativeTo(parent);
                    dialog.setVisible(true);
                }
            }
        }

        else {
            dialog.setLocationRelativeTo(parent);
            dialog.setVisible(true);
        }

    }

    private void refreshLibraryList() {
        entryList = libMan.getEntryList();
        searchField.setText("");
        libList.setListData(entryList.toArray());
        libList.revalidate();
    }

    private void searchSort() {
        if (!searchField.getText().equals("")) {
            ArrayList<LibEntry> searchList = new ArrayList<LibEntry>();
            String searchCheck = searchField.getText().toLowerCase();
            for (LibEntry entry : entryList) {
                if (entry.getName().toLowerCase().contains(searchCheck)) searchList.add(entry);
            }
            libList.setListData(searchList.toArray());
            libList.revalidate();
        }
        else refreshLibraryList();
    }

    private void fetchBlockPanel() {
        try {
            Spell selectSpell = (Spell) libMan.loadMember(((LibEntry) libList.getSelectedValue()).getIndex());
            spellInfoPanel.removeAll();
            spellInfoPanel.add(selectSpell.getBlockPanel());
            spellInfoPanel.revalidate();
        }
        catch(NullPointerException npex) {
            spellInfoPanel.removeAll();
            spellInfoPanel.add(blankTextArea);
        }
    }

    public void jumpTo(String name, Window parent) {
        this.refName = name;
        open(parent);
    }

    class NewSpellListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            SpellEditWindow sew = new SpellEditWindow(dialog);
            sew.open();
            Spell newSpell = sew.getSpell();
            if (newSpell != null) {
                libMan.addToLibrary(newSpell);
                refreshLibraryList();
                spellInfoPanel.revalidate();
            }
        }
    }

    class EditSpellListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int editSpellIndex = ((LibEntry) libList.getSelectedValue()).getIndex();
            ArrayList<Integer> deleteIndex = new ArrayList<Integer>();
            deleteIndex.add(editSpellIndex);
            Spell editSpell = (Spell) libMan.loadMember(editSpellIndex);
            SpellEditWindow sew = new SpellEditWindow(dialog, editSpell);
            sew.open();
            Spell newSpell = sew.getSpell();
            if (newSpell != null) {
                int saveIndex = libList.getSelectedIndex();
                libMan.removeFromLibrary(deleteIndex);
                entryList = libMan.getEntryList();
                libMan.addToLibrary(newSpell);
                refreshLibraryList();
                spellInfoPanel.revalidate();
                libList.setSelectedIndex(saveIndex);
            }
        }
    }

    class CopySpellListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Spell copySpell = (Spell) libMan.loadMember(((LibEntry) libList.getSelectedValue()).getIndex());
            SpellEditWindow sew = new SpellEditWindow(dialog, copySpell);
            sew.open();
            Spell newSpell= sew.getSpell();
            if (newSpell != null) {
                libMan.addToLibrary(newSpell);
                refreshLibraryList();
                spellInfoPanel.revalidate();
            }
        }
    }

    class DelSpellListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            ArrayList<Integer> delIndexList = new ArrayList<Integer>();
            for (int i = 0; i < libList.getSelectedValuesList().size(); i++) {
                delIndexList.add(((LibEntry) libList.getSelectedValuesList().get(i)).getIndex());
            }
            if (delIndexList.size() > 0) { //ensure the range to be deleted actually includes something to be deleted
                Object[] options = {"Delete", "Cancel"};
                int choice = JOptionPane.showOptionDialog(dialog, "Permanently delete the selected spell(s)?", "Confirm deletion",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
                if (choice == 0) { // if the user selects "Yes" to deleting the selected monsters
                    libMan.removeFromLibrary(delIndexList);
                    refreshLibraryList();
                    libList.setSelectedIndex(0);
                    spellInfoPanel.removeAll();
                }
            }
        }
    }

}
