/**
 * Created by Michael on 09/11/2014.
 */


import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class PickMonster {
    private LibraryManager libMan = new LibraryManager(LibraryManager.LibType.MONSTER);
    private ArrayList<Monster> addingList;
    private ArrayList<Participant> participants;
    private ArrayList<LibEntry> entryList;
    private JList libList;
    private JList encList;
    private JDialog frame;
    private JPanel panel;
    private JTextField searchField = new JTextField(18);
    private final Dimension LIST_DIMENSION = new Dimension(280, 180);

/**
 * This is the monster editing window's primary method. It creates the GUI dialog.
 * Using lists, the user can select from monsters in the external XML file (handled by the LibraryManager class).
 * The monster creation dialog is also accessed through this dialog.
 **/
    public void open(Window parent) {
        entryList = libMan.getEntryList();

        addingList = new ArrayList<Monster>();
        frame = new JDialog(parent, "Monster Library", Dialog.ModalityType.APPLICATION_MODAL);
        frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        panel = new JPanel();
        JPanel menuPanel = new JPanel(new GridBagLayout());
        JPanel encMenuPanel = new JPanel(new GridBagLayout());
        JPanel encBottomPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints(); // constraints for the "menu" layout (buttons)
        GridBagConstraints c2 = new GridBagConstraints(); // constraints for the "pane" layouts (monster lists)

        //initialize GridBag constraints for the menu layout
        c.weightx = 1;
        c.weighty = 1;
        c.anchor = GridBagConstraints.NORTHWEST;

        //create the list panels and the text (center) panel, assign layouts
        JPanel libPanel = new JPanel(new GridBagLayout());
        JPanel encPanel = new JPanel(new GridBagLayout());
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS)); //this panel works well with a box layout since it's really just a big block of text

        //create buttons for the library menu panel, add listeners, put them in the panel
        JButton newButton = new JButton("New");
        JButton editButton = new JButton("Edit");
        JButton copyButton = new JButton("Copy");
        JButton delButton = new JButton("Delete");
        menuPanel.add(newButton, c);
        menuPanel.add(editButton, c);
        menuPanel.add(copyButton, c);
        menuPanel.add(delButton, c);
        newButton.addActionListener(new NewMonListener());
        editButton.addActionListener(new EditMonListener());
        copyButton.addActionListener(new CopyMonListener());
        delButton.addActionListener(new DelMonListener());

        //create buttons for the "add to encounter" menu panel, add listeners, put them in the panel
        JButton addButton = new JButton("Add");
        JButton removeButton = new JButton("Remove");
        addButton.addActionListener(new AddButtonListener());
        removeButton.addActionListener(new RemoveButtonListener());
        encMenuPanel.add(addButton, c);
        encMenuPanel.add(removeButton, c);

        //create a button for the encounter bottom panel ("add to encounter" button)
        JButton addToEncButton = new JButton("Add to Encounter");
        addToEncButton.addActionListener(new AddToEncButtonListener());
        encBottomPanel.add(addToEncButton, c);


        //create list components to hold the library of monsters and monters to add to an encounter.
        //also put them inside scrollers so they can be, well, scrolled through.
        libList = new JList();
        encList = new JList();
        JScrollPane libScroller = new JScrollPane(libList);
        libScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        libScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        libScroller.setPreferredSize(LIST_DIMENSION);
        JScrollPane encScroller = new JScrollPane(encList);
        encScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        encScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        encScroller.setPreferredSize(LIST_DIMENSION);


        //refresh the library list by getting a list of monsters in the database
        refreshLibraryList();

        //add the listener for the search bar (Field)
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                searchSort();
            }
        });

        //add passive listeners (on selecting an item in the library list, and for pushing Delete or Enter where appropriate)
        libList.addListSelectionListener(new LibrarySelectionListener());
        libList.addKeyListener(new ListKeyListener());
        encList.addKeyListener(new RemoveKeyListener());

        //add menu bars to the left and right panels (library and encounter, respectively)
        c2.anchor = GridBagConstraints.NORTHWEST;
        c2.weightx = 1;
        c2.weighty = 0;
        c2.gridy = 0;
        c2.gridx = 0;
        libPanel.add(menuPanel, c2);
        encPanel.add(encMenuPanel, c2);

        //redefine GridBag constants and add the lists to the left and right panels
        c2.weighty=1;
        c2.fill = GridBagConstraints.VERTICAL;
        c2.anchor = GridBagConstraints.SOUTHEAST;
        c2.gridy = 1;
        libPanel.add(libScroller, c2);
        encPanel.add(encScroller, c2);

        //add the lower menu bar to the right (encounter) panel and the search field to the left (library) panel
        c2.anchor = GridBagConstraints.SOUTHEAST;
        c2.gridy=2;
        c2.weighty=0;
        encPanel.add(encBottomPanel, c2);
        c2.anchor = GridBagConstraints.SOUTHWEST;
        libPanel.add(searchField, c2);

        //add the left, right, and center panels to the frame
        frame.add(BorderLayout.CENTER, panel);
        frame.add(BorderLayout.WEST, libPanel);
        frame.add(BorderLayout.EAST, encPanel);

        //set the frame's size, prevent resizing, pack it down and show it
        frame.setMinimumSize(new Dimension(860, 450));
        frame.pack();
        frame.setLocationRelativeTo(parent);
        frame.setVisible(true);
    }

    /**
     * Called when the "add to encounter" list needs to be updated.
     * Loads the list of monsters to be added and puts them into the JList so they can be displayed.
     * Afterwards, the component is revalidated (so the changes will actually show up).
     */

    private void updateAddList() {
        encList.setListData(addingList.toArray());
        encList.revalidate();
    }


    /**
     * Add the selected monsters from the library into the "add to encounter" list.
     * When this is finished, the updateAddList() method runs.
     */

    private void addSelection() {
        List<Monster> addMon = new ArrayList<Monster>();
        for(int i = 0; i < libList.getSelectedValuesList().size(); i++) {
            addMon.add((Monster) libMan.loadMember(((LibEntry) libList.getSelectedValuesList().get(i)).getIndex()));
        }
        for (Monster mon : addMon) {
            addingList.add(mon);
        }
        updateAddList();
    }

    /**
     * Removes the selected monsters from the "add to encounter" list.
     * When this is finished, the updateAddList() method runs.
     */

    private void removeSelection() {
        List<Monster> remMon = encList.getSelectedValuesList();

        for (Monster mon : remMon) {
            addingList.remove(mon);
        }
        updateAddList();
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

    private void refreshLibraryList() {
        entryList = libMan.getEntryList();
        searchField.setText("");
        libList.setListData(entryList.toArray());
        libList.revalidate();
    }

    /**
     * Method for getting the participants list, which is the list of monsters to be added to an encounter.
     * @return The participants list, which is generated by clicking the "Add to Encounter" button.
     */
    public ArrayList<Participant> getParticipants() {
        return participants;
    }

    // everything below this point is a listener inner class for the various Swing components.

    class LibrarySelectionListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent lse) {
            try {
                Monster selectMonster = (Monster) libMan.loadMember(((LibEntry) libList.getSelectedValue()).getIndex());
                panel.removeAll();
                panel.add(selectMonster.getBlockPanel());
                panel.revalidate();
            }
            catch(NullPointerException npex) {
                panel.removeAll();
            }
        }
    }

    class NewMonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            MonEditWindow mew = new MonEditWindow();
            mew.open(frame);
            Monster newMon = mew.getMon();
            if (newMon != null) {
                libMan.addToLibrary(newMon);
                refreshLibraryList();
                panel.revalidate();
            }
        }
    }

    class EditMonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int editMonIndex = ((LibEntry) libList.getSelectedValue()).getIndex();
            ArrayList<Integer> deleteIndex = new ArrayList<Integer>();
            deleteIndex.add(editMonIndex);
            Monster editMon = (Monster) libMan.loadMember(editMonIndex);
            MonEditWindow mew = new MonEditWindow(editMon);
            mew.open(frame);
            Monster newMon = mew.getMon();
            if (newMon != null) {
                int saveIndex = libList.getSelectedIndex();
                libMan.removeFromLibrary(deleteIndex);
                entryList = libMan.getEntryList();
                libMan.addToLibrary(newMon);
                refreshLibraryList();
                panel.revalidate();
                libList.setSelectedIndex(saveIndex);
            }
        }
    }

    class CopyMonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Monster copyMon = (Monster) libMan.loadMember(((LibEntry) libList.getSelectedValue()).getIndex());
            MonEditWindow mew = new MonEditWindow(copyMon);
            mew.open(frame);
            Monster newMon = mew.getMon();
            if (newMon != null) {
                libMan.addToLibrary(newMon);
                refreshLibraryList();
                panel.revalidate();
            }
        }
    }

    class DelMonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            ArrayList<Integer> delIndexList = new ArrayList<Integer>();
            for (int i = 0; i < libList.getSelectedValuesList().size(); i++) {
                delIndexList.add(((LibEntry) libList.getSelectedValuesList().get(i)).getIndex());
            }
            if (delIndexList.size() > 0) { //ensure the range to be deleted actually includes something to be deleted
                Object[] options = {"Delete", "Cancel"};
                int choice = JOptionPane.showOptionDialog(frame, "Permanently delete the selected monster(s)?", "Confirm deletion",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
                if (choice == 0) { // if the user selects "Yes" to deleting the selected monsters
                    libMan.removeFromLibrary(delIndexList);
                    refreshLibraryList();
                    libList.setSelectedIndex(0);
                    panel.removeAll();
                }
            }
        }

    }

    class AddButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            addSelection();
        }
    }

    class AddToEncButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            participants = new ArrayList<Participant>();
            Collections.sort(addingList);
            for (int i = 0; i < addingList.size(); i++) {
                Participant p = new Participant(addingList.get(i));
                participants.add(p);
            }
            frame.dispose();
        }
    }

    class RemoveButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            removeSelection();
        }
    }

    class ListKeyListener implements KeyListener {
        public void keyPressed(KeyEvent kev) {
            int keyId = kev.getKeyCode();
            if (keyId == KeyEvent.VK_ENTER) {
                addSelection();
            }
        }

        public void keyTyped(KeyEvent kev) {
        }

        public void keyReleased(KeyEvent kev) {
        }
    }

    class RemoveKeyListener implements KeyListener {
        public void keyPressed(KeyEvent kev) {
            int keyId = kev.getKeyCode();
            if (keyId == KeyEvent.VK_DELETE) {
                removeSelection();
            }
        }

        public void keyTyped(KeyEvent kev) {
        }

        public void keyReleased(KeyEvent kev) {
        }
    }
}