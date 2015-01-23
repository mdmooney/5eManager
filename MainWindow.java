import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.UIManager.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 * Created by Michael on 25/11/2014.
 */
public class MainWindow {
    private JFrame frame;
    private ArrayList<Participant> participants = new ArrayList<Participant>();
    private JTable encounterMembers;
    private DefaultTableModel model = new DefaultTableModel() {
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return String.class;
        }
    };
    private TableRowSorter sorter = new TableRowSorter(model);
    private JSpinner initSpin = new JSpinner(new SpinnerNumberModel(0, 0, 99, 1));
    private JSpinner hpChangeSpin = new JSpinner(new SpinnerNumberModel(0, 0, 999, 1));
    private JPanel blockPanel;
    private JTextArea blankTextArea = new JTextArea(20, 20);
    private JTextArea notesArea = new JTextArea(10,10);
    private JScrollPane notesScroller = new JScrollPane(notesArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    private JLabel currentHpLabel = new JLabel(" / ");
    private HashMap<Participant, Integer> addedPlayers;
    private boolean noInitRefresh;
    private final Dimension LIST_DIMENSION = new Dimension(290, 400);
    private final String[] COLUMN_NAMES = {"Name", "AC", "HP", "R"};

    public static void main(String[] args) {
     MainWindow mw = new MainWindow();
     mw.go();
    }

    /**
     * Provides the setup for the main window, which displays and manages the encounter
     * and also connects to other windows, such as the libraries and the generators.
     * Called when the main method runs.
     */
    public void go() {
        //set up Java to use the Nimbus Look and Feel if it is available.
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException e) { //thrown if Nimbus look and feel is not available.
            System.err.println("Couldn't find class for Nimbus look and feel.");
            System.err.println("Did you include the L&F library in the class path?");
            System.err.println("Using the default look and feel.");
        }

        catch (UnsupportedLookAndFeelException e) { //thrown if the platform can't use the look and feel
            System.err.println("Can't use the specified look and feel ('Nimbus') on this platform.");
            System.err.println("Using the default look and feel.");
        }

        catch (Exception e) { //generic exception for all other cases
            System.err.println("Couldn't get specified look and feel ('Nimbus'), for some reason.");
            System.err.println("Using the default look and feel.");
            e.printStackTrace();
        }

        addedPlayers = new HashMap<Participant, Integer>(); //HashMap for controlling players (as players must be kept consistent)
        //create frame, panels
        frame = new JFrame("5e Manager");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JPanel centPanel = new JPanel(new GridBagLayout());
        JPanel controlPanel = new JPanel(new GridBagLayout());
        blockPanel = new JPanel();
        blockPanel.setLayout(new BoxLayout(blockPanel, BoxLayout.X_AXIS));


        //add a blank text area with a scrollbar to the block panel on startup. This is purely aesthetic.
        blankTextArea.setEnabled(false);
        JScrollPane blankScroll = new JScrollPane(blankTextArea);
        blankScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        blockPanel.add(blankScroll);

        //create buttons for changing HP
        JButton randomHpButton = new JButton("Roll");
        randomHpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!encounterMembers.getSelectionModel().isSelectionEmpty()) {
                    getSelectedParticipant().getRandomHp();
                    encounterMembers.setValueAt(getSelectedParticipant().getHpString(), encounterMembers.getSelectedRow(), 2);
                    refreshHpLabel();
                }
            }
        });
        JButton hurtButton = new JButton("Damage");
        hurtButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!encounterMembers.getSelectionModel().isSelectionEmpty()) {
                    modHp(- (Integer) hpChangeSpin.getValue());

                }
            }
        });

        JButton healButton = new JButton("Heal");
        healButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!encounterMembers.getSelectionModel().isSelectionEmpty()) {
                    modHp((Integer) hpChangeSpin.getValue());
                }
            }
        });


        //create button to advance the encounter to the next Participant in the initiative order
        JButton nextTurnButton = new JButton("Next Turn");
        nextTurnButton.addActionListener(new NextTurnListener());

        GridBagConstraints c = new GridBagConstraints();

        //set up menu bar and menu headers
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu encMenu = new JMenu("Encounter");
        JMenu libMenu = new JMenu("Libraries");
        JMenu generatorMenu = new JMenu("Generators");

        //create File menu options
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });
        JMenuItem libMenuItem = new JMenuItem("Monster Library");
        libMenuItem.addActionListener(new LibButtonListener());

        //create Libraries menu options
        JMenuItem pcLibMenuItem = new JMenuItem("Player Library");
        pcLibMenuItem.addActionListener(new PcLibButtonListener());

        JMenuItem spellLibMenuItem = new JMenuItem("Spell Library");
        spellLibMenuItem.addActionListener(new SpellLibButtonListener());

        //create Encounter menu options
        JMenuItem rollInitItem = new JMenuItem("Roll initiative for all"); //rolls initiative for all Participants
        rollInitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for(Participant part : participants) {
                    part.setInitiative(part.rollInitiative());
                }
                refreshInitiative();
            }
        });

        JMenuItem rollInitMonItem = new JMenuItem("Roll initiative for monsters"); //rolls intitiative only for the Monsters
        rollInitMonItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (Participant part : participants) {
                    if (part.getFightableClass().equals(Monster.class)) {
                        part.setInitiative(part.rollInitiative());
                    }
                }
                refreshInitiative();
            }
        });


        JMenuItem rollHpItem = new JMenuItem("Roll HP for all"); //rolls HP for all (though this does not apply for Players)
        rollHpItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for(Participant part : participants) {
                    part.getRandomHp();
                    if (!encounterMembers.getSelectionModel().isSelectionEmpty()) refreshHpLabel();
                }
                updateAllHp();
            }
        });

        JMenuItem endEncItem = new JMenuItem("End encounter"); //end encounter by setting player initiatives to 0 and remove monsters
        endEncItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<Participant> newParts = new ArrayList<Participant>();
                for (Participant part : participants) {
                    if (part.getFightableClass().equals(Player.class)) {
                        part.setCurrentRound(1);
                        part.setInitiative(0);
                        newParts.add(part);
                    }
                }
                participants.clear();
                participants.addAll(newParts);
                updateParticipants();
                blockPanel.removeAll();
                blockPanel.add(blankTextArea);
            }
        });

        JMenuItem clearEncItem = new JMenuItem("Clear encounter"); //remove all Participants from encounter
        clearEncItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                participants.clear();
                addedPlayers.clear();
                updateParticipants();
                blockPanel.removeAll();
                blockPanel.add(blankTextArea);
            }
        });

        //add items to the File menu
        fileMenu.add(exitMenuItem);

        //add items to the Encounter menu
        encMenu.add(rollInitItem);
        encMenu.add(rollInitMonItem);
        encMenu.add(rollHpItem);
        encMenu.add(new JSeparator(SwingConstants.HORIZONTAL));
        encMenu.add(endEncItem);
        encMenu.add(clearEncItem);

        //add items to the Libraries menu
        libMenu.add(libMenuItem);
        libMenu.add(pcLibMenuItem);
        libMenu.add(spellLibMenuItem);

        //add items to the Generator menu
         // (nothing here yet)

        //add menus to the menu bar
        menuBar.add(fileMenu);
        menuBar.add(encMenu);
        menuBar.add(libMenu);
        menuBar.add(generatorMenu);

        //create initiative button to roll initiative for specific Participants
        JButton rollInitButton = new JButton("Roll");
        rollInitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!encounterMembers.getSelectionModel().isSelectionEmpty()) {
                    initSpin.setValue((getSelectedParticipant()).rollInitiative());
                }
            }
        });

        //create the control panel
        c.gridy=0;
        c.gridx=0;
        controlPanel.add(new JLabel("Initiative: "),c);
        initSpin.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (!encounterMembers.getSelectionModel().isSelectionEmpty() && !noInitRefresh) {
                    Participant part = getSelectedParticipant();
                    (getSelectedParticipant()).setInitiative((Integer) initSpin.getValue());
                    refreshInitiative();
                    for (int i = 0; i < encounterMembers.getRowCount(); i++) {
                        if (encounterMembers.getValueAt(i, 0) == part) {
                            encounterMembers.setRowSelectionInterval(i,i);
                            break;
                        }
                    }
                }
            }
        });
        c.gridx++;
        controlPanel.add(initSpin,c);
        c.gridx++;
        controlPanel.add(rollInitButton, c);

        c.gridy++;
        c.gridx=0;
        controlPanel.add(new JLabel("HP: "), c);
        c.gridx++;
        controlPanel.add(currentHpLabel, c);
        c.gridx++;
        controlPanel.add(randomHpButton, c);

        c.gridy++;
        c.gridwidth=1;
        c.gridx=0;
        controlPanel.add(hpChangeSpin, c);

        //c.gridy++;
        //c.gridx=0;
        c.gridx++;
        c.gridwidth = 1;
        controlPanel.add(hurtButton,c);
        c.gridx++;
        controlPanel.add(healButton,c);
        c.gridy++;
        c.gridx=0;
        c.gridwidth=3;
        c.fill = GridBagConstraints.HORIZONTAL;
        controlPanel.add(nextTurnButton,c);
        c.gridy++;
        c.gridwidth=1;
        controlPanel.add(new JLabel("Notes: "),c);
        c.gridy++;
        c.gridwidth=3;
        controlPanel.add(notesScroller,c);

        //set up participant table and its model & sorting model
        for (String str : COLUMN_NAMES) {
            model.addColumn(str);
        }

        encounterMembers = new JTable(model) { //create the table
            private static final long serialVersionUID = 1L;

            public boolean isCellEditable(int row, int column) {
                return false;
            }; //prevent the user from manually editing cell contents.
        };
        sorter.setComparator(0, new TurnCompare());
        sorter.toggleSortOrder(0); //set to sort by pre-determined initiative/round sorting
        //sorter.setSortsOnUpdates(true);  re-sort any time the table is updated, no longer useful
        //prevent sorting by another other column
        sorter.setSortable(1, false);
        sorter.setSortable(2, false);
        sorter.setSortable(0, false);
        encounterMembers.setRowSorter(sorter);
        encounterMembers.getTableHeader().setReorderingAllowed(false); //prevent user from shuffling column order
        encounterMembers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); //allow only one row to be selected at a time
        encounterMembers.getSelectionModel().addListSelectionListener(new EncSelectionListener());
        encounterMembers.getColumnModel().getColumn(0).setPreferredWidth(250);
        encounterMembers.setDefaultRenderer(String.class, new BoardTableCellRenderer()); //use custom renderer to account for unique highlighting style

        //set up scroller and encounter panel
        JScrollPane encScroller = new JScrollPane(encounterMembers, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        JPanel encPanel = new JPanel(new GridBagLayout());

        c.gridx=0;
        c.gridy=0;
        c.gridwidth=1;
        c.weightx=1;
        c.weighty=1;
        c.fill=GridBagConstraints.BOTH;
        c.anchor=GridBagConstraints.NORTHWEST;
        encPanel.add(encScroller,c);

        //set preferred sizes for the left and right panels so that scaling works correctly
        encScroller.setPreferredSize(LIST_DIMENSION);
        blockPanel.setPreferredSize(LIST_DIMENSION);

        //fill center panel (basically the entirety of the frame) by adding the the three separate panels
        c.gridx = 0;
        c.gridy=0;
        c.weightx=0;
        c.weighty=1;
        c.fill = GridBagConstraints.BOTH;
        c.anchor=GridBagConstraints.WEST;
        centPanel.add(encPanel,c);
        c.gridx++;
        c.weightx=0;
        c.gridwidth=GridBagConstraints.RELATIVE;
        centPanel.add(controlPanel,c);
        c.gridx++;
        c.weightx=1;
        c.gridwidth=GridBagConstraints.REMAINDER;
        centPanel.add(blockPanel,c);

        //set up the notes area (set by participant on a per-encounter basis)
        notesArea.getDocument().addDocumentListener(new DocumentListener() {
            //in all three cases, the listener saves whatever is stored in the document to the Participant's individual notes
            //this way any time the user adds, removes, or changes anything in the notes area, the information is saved
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (getSelectedParticipant() != null) {
                    getSelectedParticipant().setNotes(notesArea.getText());
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (getSelectedParticipant() != null) {
                    getSelectedParticipant().setNotes(notesArea.getText());
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (getSelectedParticipant() != null) {
                    getSelectedParticipant().setNotes(notesArea.getText());
                }
            }
        });
        notesArea.setEnabled(false); //disable notes area before any Participants are in the encounter because otherwise it won't work

        //add the center panel and the menu bar to the frame
        frame.add(BorderLayout.CENTER, centPanel);
        frame.setJMenuBar(menuBar);

        //pack it, size it, show it
        frame.pack();
        frame.setMinimumSize(frame.getSize());
        frame.setVisible(true);
    }

    /**
     * Updates and sorts the list of Participants in the encounter.
     * This also includes renaming monsters with the same base-name so that they have numbers.
     * This numbering makes differentiating between different monsters of the same kind easier.
     * Also determines whether the Participant notes area is active.
     */

    private void updateParticipants() {
        Collections.sort(participants);

        //rename participants appropriately so that monsters don't have the same name
        ArrayList<Participant> participantsByNum = new ArrayList<Participant>(); //use a new arraylist sorted by number
        participantsByNum.addAll(participants);
        participantsByNum.sort(new ParticipantNumberCompare());

        for (int i = 0; i < participantsByNum.size(); i++) {
            Participant part = participantsByNum.get(i);
            if (!part.getFightableClass().equals(Player.class)) { //exclude players from the numbering, because they should already be uniquely named.
                if (part.getNumber() == 0) { //get given number of participant, which defaults to 0 (no numbering needed)
                    int highestNum = 0;
                    for (int j = 0; j < participantsByNum.size(); j++) { //count how many participants exist with the same name
                        Participant compare = participantsByNum.get(j);
                        if (j != i && part.getBaseName().equals(compare.getBaseName()) && compare.getNumber() > highestNum) {
                            highestNum = compare.getNumber();
                        }
                    }
                    part.setNumber(highestNum + 1); //set the given participant to be numbered according to 1 higher than the highest numbered equivalent
                    part.setName(part.getBaseName() + " #" + part.getNumber()); //name the participant according to their number
                }
            }
        }

        //end renaming

        //sort the actual participants list by turn comparison (sort by round, then by initiative)
        Collections.sort(participants, new TurnCompare());
        participantsToTable();

        //if there are actually participants in the encounter, make it possible to write Participant notes
        if (participants.size() > 0) {
            notesArea.setEnabled(true);
        }
        else {
            notesArea.setEnabled(false);
        }
    }

    /**
     * Places the encounter's Participants (stored in an ArrayList) in the encounter table.
     * Retrieves pertinent information (AC, HP, active round) as well to put in the columns after the first.
     * Also revalidates the table so the information is actually displayed/
     */
    private void participantsToTable() {
        for (int i = model.getRowCount() - 1; i >= 0; i--) {
            model.removeRow(i); //get rid of all existing rows to make way for a new table
        }

        for (Participant part : participants) {
            model.addRow(new Object[]{part, part.getAc(), part.getHpString(), part.getCurrentRound()}); //refill the table with each participant
        }
        encounterMembers.revalidate(); //refresh the table to display the information
    }

    /**
     * Method for getting the participant selected by the user (or programatically) in the encounter table.
     * @return the participant currently selected in the encounter table.
     */
    private Participant getSelectedParticipant() {
        if (encounterMembers.getSelectedRow() != -1) { //check to make sure something is actually selected
            return (Participant) encounterMembers.getValueAt(encounterMembers.getSelectedRow(), 0);
        }
        else return null;
    }


    /**
     * Refreshes the initiative by sorting the Participants list in order of the TurnCompare comparator.
     * This sorts Participants first in order of active round, then by order of initiative.
     * Then, the participantsToTable() method is called.
     */
    private void refreshInitiative() {
         //sorter.sort();
         //participants.sort(new InitiativeCompare());
        participants.sort(new TurnCompare());
         participantsToTable();
    }

    /**
     * Refreshes the label in the control panel to reflect the current and maximum HP of the selected participant.
     */
    private void refreshHpLabel() {
        Participant selected = getSelectedParticipant();
        currentHpLabel.setText(selected.getCurrentHp() + " / " + selected.getMaxHp());
    }

    /**
     * For every Participant in the encounter, the HP string is updated (i.e. x / y HP, where x is current HP and y is the max HP
     * for that Participant). The HP column of the encounter table is updated to reflect this.
     * This method is mainly to ensure that the table always displays the correct HP value for all Participants.
     */
    private void updateAllHp() {
        for (int i = 0; i < encounterMembers.getRowCount(); i++) {
            Participant part = (Participant)encounterMembers.getValueAt(i,0);
            part.updateHpString();
            encounterMembers.setValueAt(part.getHpString(), i, 2);
        }
    }

    /**
     * Updates the block panel to display the information of the currently selected Participant in the encounter table.
     * Also, sets the initiative spinner to the correct value and enables the note area (if not already enabled) and
     * puts the notes in appropriate to the given Participant.
     */
    private void refreshBlockPanel() {
        try {
            Participant selected = getSelectedParticipant();
            blockPanel.removeAll();
            blockPanel.add(selected.getBlock());
            blockPanel.revalidate();
            initSpin.setValue(selected.getInitiative());
            notesArea.setEnabled(true);
            notesArea.setText(selected.getNotes());
            refreshHpLabel();
        }
        catch(NullPointerException npex) {
            blockPanel.removeAll(); //clear out the block panel in the even that nothing is selected when it should be
        }
    }

    /**
     * Alter the current HP of the currently selected Participant on the encounter table.
     * Mainly used for "heal" and "damage" buttons.
     * Updates all necessary labels as well.
     * This DOES NOT call the updateAllHp() method but instead updates the Participant's HP entry in the table alone.
     * @param mod The amount by which the Participant's HP is to be changed.
     */
    private void modHp(int mod) {
        Participant selected = getSelectedParticipant();
        selected.setCurrentHp(selected.getCurrentHp() + mod);
        selected.updateHpString();
        encounterMembers.setValueAt(selected.getHpString(), encounterMembers.getSelectedRow(), 2);
        refreshHpLabel();
        encounterMembers.revalidate();
    }

    /**
     * Listener for the monster library menu button. The class name is an artifact from when the program had only one library.
     * Opens the library dialog and updates the participants if they have changed when the dialog is closed.
     */
    public class LibButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            PickMonster pm = new PickMonster();
            pm.open(frame);
            if (pm.getParticipants() != null) {
                ArrayList<Participant> newParts = new ArrayList<Participant>();
                newParts.addAll(pm.getParticipants());
                for (Participant part: newParts) {
                    if(!participants.isEmpty()) {
                        part.setCurrentRound(participants.get(0).getCurrentRound());
                    }
                }
                participants.addAll(newParts);
                updateParticipants();
            }
        }
    }

    /**
     * Listener for the spell library menu button.
     * Opens the spell library dialog. As the spell library dialog is purely informational (never returns anything),
     * this is all this listener does.
     */
    public class SpellLibButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            PickSpell ps = new PickSpell();
            ps.open(frame);
        }
    }

    /**
     * Listener for the PC library menu button.
     * Opens the PC library menu dialog. First makes a new list places all non-PC Participants into it, then
     * opens the dialog with the current HashMap of players supplied to it.
     * When the dialog is closed, retrieves all participants and puts them into the encounter.
     * New Players added to the encounter are placed at the end of the round which is current for the Participant whose turn it
     * currently is (i.e. is on the top of the initiative table, or row 0). Otherwise, they enter at Round 1.
     */
    public class PcLibButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int range = participants.size(); //defined for loop control
            ArrayList<Participant> newParticipants = new ArrayList<Participant>();
            for (int i = 0; i < range; i ++) { //iterate over all existing participants
                if (!participants.get(i).getFightableClass().equals(Player.class)) {
                    newParticipants.add(participants.get(i)); //add all non-Player Participants to the new list
                }
            }
            participants = newParticipants; //set the encoutner Participant list to the new Player-free list
            PickPlayer pp = new PickPlayer(addedPlayers); //create the dialog class with the player HashMap supplied
            pp.open(frame); //open the dialog
            if (pp.getParticipants() != null) { //after it closes, check if there are any players available.
                ArrayList<Participant> newParts = new ArrayList<Participant>();
                newParts.addAll(pp.getParticipants());
                for (Participant part: newParts) {
                    if(!participants.isEmpty()) { //if the encounter list isn't empty
                        part.setCurrentRound(participants.get(0).getCurrentRound()); //set the new participants to come in at the end of the current round (top Participant's initiative round)
                    }
                }
                participants.addAll(newParts); //add all players from the player dialog to the encounter
                updateParticipants(); //update all participants
            }
        }
    }

    public class InitiativeCompare implements Comparator<Participant> {
        public int compare(Participant pOne, Participant pTwo) {
            return Integer.compare(pOne.getInitiative(), pTwo.getInitiative()) * -1;
        }
    }

    public class TurnCompare implements Comparator<Participant> {
        public int compare(Participant pOne, Participant pTwo) {
            if (pOne.getCurrentRound() >pTwo.getCurrentRound()) return 1;
            else if (pTwo.getCurrentRound() > pOne.getCurrentRound()) return -1;
            else return new InitiativeCompare().compare(pOne,pTwo);
        }
    }

    public class ParticipantNumberCompare implements Comparator<Participant> {
        public int compare(Participant pOne, Participant pTwo) {
            if (pOne.getBaseName().compareTo(pTwo.getBaseName()) == 0) {
                return Integer.compare(pOne.getNumber(), pTwo.getNumber());
            }
            else return pOne.getBaseName().compareTo(pTwo.getBaseName());
        }
    }

    class EncSelectionListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent lse) {
            noInitRefresh = true;
            refreshBlockPanel();
            noInitRefresh = false;
        }
    }

    class NextTurnListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (model.getRowCount() > 0) {
                Participant temp = participants.get(0);
                temp.setCurrentRound(temp.getCurrentRound()+1);
                participants.remove(temp);
                participants.sort(new TurnCompare());
                participants.add(temp);
                //sorter.sort();
                participantsToTable();
            }
        }
    }

    class BoardTableCellRenderer extends DefaultTableCellRenderer {

        private static final long serialVersionUID = 1L;

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int col) {

            Component c = super.getTableCellRendererComponent(table, value,
                    isSelected, hasFocus, row, col);
            Object valueAt = table.getModel().getValueAt(row, col);
            Participant part = (Participant) table.getModel().getValueAt(row, 0);
            final Color ALT_BG = new Color(230, 230, 230);
            final Color SELECT_BG = new Color(57, 105, 138);

            String s = "";

            if (part != null && valueAt instanceof Participant) {
                s = valueAt.toString();
            }

            if (row == table.getSelectedRow()) {
                c.setForeground(Color.WHITE);
                c.setBackground(SELECT_BG);
            }

            else if (part.getCurrentHp() <= 0) {
                c.setForeground(Color.WHITE);
                c.setBackground(Color.BLACK);
            }

            else if (row % 2 == 0) {
                c.setForeground(Color.BLACK);
                c.setBackground(ALT_BG);
            }

            else {
                c.setForeground(Color.black);
                c.setBackground(Color.WHITE);
            }

            return c;
        }
    }
}
