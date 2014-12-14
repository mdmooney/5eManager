/**
 * Created by Michael on 13/12/2014.
 */

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public abstract class PickFightable {
    protected LibraryManager libMan;
    //protected ArrayList<Fightable> addingList;
    protected ArrayList<Participant> addingList;
    protected ArrayList<Participant> participants;
    protected ArrayList<LibEntry> entryList;
    protected JList libList;
    protected JList encList;
    protected JDialog frame;
    protected JPanel panel;
    protected JTextField searchField = new JTextField(18);
    protected String windowName;
    private final Dimension LIST_DIMENSION = new Dimension(280, 180);


    /**
     * This is the editing window's primary method. It creates the GUI dialog.
     * Using lists, the user can select from members in the external XML file (handled by the LibraryManager class).
     * The member creation dialog is also accessed through this dialog.
     **/
    public void open(Window parent) {
        pickLibrary();
        entryList = libMan.getEntryList();
        //addingList = new ArrayList<Fightable>();
        addingList = new ArrayList<Participant>();
        frame = new JDialog(parent, windowName, Dialog.ModalityType.APPLICATION_MODAL);
        frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        panel = new JPanel();
        JPanel menuPanel = new JPanel(new GridBagLayout());
        JPanel encMenuPanel = new JPanel(new GridBagLayout());
        JPanel encBottomPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints(); // constraints for the "menu" layout (buttons)
        GridBagConstraints c2 = new GridBagConstraints(); // constraints for the "pane" layouts (lists)

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
        newButton.addActionListener(new NewListener());
        editButton.addActionListener(new EditListener());
        copyButton.addActionListener(new CopyListener());
        delButton.addActionListener(new DelListener());

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


        //create list components to hold the library of members and members to add to an encounter.
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


        //refresh the library list by getting a list of members in the database, and update the add list in case anything is there
        refreshLibraryList();
        updateAddList();

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
     * This method is used to make sure the right library manager is used for the given fightable.
     * It also should include setting a value for the protected String windowName, which is the title of the library's
     * window.
     */

    protected abstract void pickLibrary();

    /**
     * Called when the "add to encounter" list needs to be updated.
     * Loads the list of members to be added and puts them into the JList so they can be displayed.
     * Afterwards, the component is revalidated (so the changes will actually show up).
     */

    protected void updateAddList() {
        encList.setListData(addingList.toArray());
        encList.revalidate();
    }


    /**
     * Add the selected fightables from the library into the "add to encounter" list.
     * When this is finished, the updateAddList() method runs.
     */

    protected void addSelection() {
        List<Fightable> addFightable = new ArrayList<Fightable>();
        for(int i = 0; i < libList.getSelectedValuesList().size(); i++) {
            addFightable.add((Fightable) libMan.loadMember(((LibEntry) libList.getSelectedValuesList().get(i)).getIndex()));
        }
        for (Fightable fight : addFightable) {
            //addingList.add(fight);
            addingList.add(new Participant(fight));
        }
        updateAddList();
    }

    /**
     * Removes the selected fightables from the "add to encounter" list.
     * When this is finished, the updateAddList() method runs.
     */

    protected void removeSelection() {
//        List<Fightable> remFightable = encList.getSelectedValuesList();
//        for (Fightable fight : remFightable) {
//            addingList.remove(fight);
//        }
        List<Participant> remParts = encList.getSelectedValuesList();
        for (Participant parts : remParts) {
            addingList.remove(parts);
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

    protected void refreshLibraryList() {
        entryList = libMan.getEntryList();
        searchField.setText("");
        libList.setListData(entryList.toArray());
        libList.revalidate();
    }

    protected abstract void newEntry();

    protected abstract void editEntry();

    private void delEntry() {
        ArrayList<Integer> delIndexList = new ArrayList<Integer>();
        for (int i = 0; i < libList.getSelectedValuesList().size(); i++) {
            delIndexList.add(((LibEntry) libList.getSelectedValuesList().get(i)).getIndex());
        }
        if (delIndexList.size() > 0) { //ensure the range to be deleted actually includes something to be deleted
            Object[] options = {"Delete", "Cancel"};
            int choice = JOptionPane.showOptionDialog(frame, "Permanently delete the selected value(s)?", "Confirm deletion",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
            if (choice == 0) { // if the user selects "Yes" to deleting the selected values
                libMan.removeFromLibrary(delIndexList);
                refreshLibraryList();
                libList.setSelectedIndex(0);
                panel.removeAll();
            }
        }
    }

    protected abstract void copyEntry();

    protected void addToEncounter() {
        participants = new ArrayList<Participant>();
        //Collections.sort(addingList, new FightableCompare());
        Collections.sort(addingList);
        participants = addingList;
//        for (int i = 0; i < addingList.size(); i++) {
//            Participant p = new Participant(addingList.get(i));
//            participants.add(p);
//        }
        frame.dispose();
    }

    /**
     * Method for getting the participants list, which is the list of members to be added to an encounter.
     * @return The participants list, which is generated by clicking the "Add to Encounter" button.
     */
    public ArrayList<Participant> getParticipants() {
        return participants;
    }

    // everything below this point is a listener inner class for the various Swing components.

    class LibrarySelectionListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent lse) {
            try {
                Fightable selectFightable = (Fightable) libMan.loadMember(((LibEntry) libList.getSelectedValue()).getIndex());
                panel.removeAll();
                panel.add(selectFightable.getBlockPanel());
                panel.revalidate();
            }
            catch(NullPointerException npex) {
                panel.removeAll();
            }
        }
    }

    class NewListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            newEntry();
        }
    }

    class EditListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            editEntry();
        }
    }

    class CopyListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            copyEntry();
        }
    }

    class DelListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            delEntry();
        }

    }

    class AddButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            addSelection();
        }
    }

    class AddToEncButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            addToEncounter();
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

    private class FightableCompare implements Comparator<Fightable> {
        public int compare(Fightable one, Fightable two) {
            return one.getName().compareToIgnoreCase(two.getName());
        }
    }
}

