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

    public void go() {
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Couldn't find class for Nimbus look and feel.");
            System.err.println("Did you include the L&F library in the class path?");
            System.err.println("Using the default look and feel.");
        }

        catch (UnsupportedLookAndFeelException e) {
            System.err.println("Can't use the specified look and feel ('Nimbus') on this platform.");
            System.err.println("Using the default look and feel.");
        }

        catch (Exception e) {
            System.err.println("Couldn't get specified look and feel ('Nimbus'), for some reason.");
            System.err.println("Using the default look and feel.");
            e.printStackTrace();
        }

        addedPlayers = new HashMap<Participant, Integer>();
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


        JButton nextTurnButton = new JButton("Next Turn");
        nextTurnButton.addActionListener(new NextTurnListener());

        GridBagConstraints c = new GridBagConstraints();

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu encMenu = new JMenu("Encounter");
        JMenu libMenu = new JMenu("Libraries");

        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });
        JMenuItem libMenuItem = new JMenuItem("Monster Library");
        libMenuItem.addActionListener(new LibButtonListener());

        JMenuItem pcLibMenuItem = new JMenuItem("Player Library");
        pcLibMenuItem.addActionListener(new PcLibButtonListener());

        JMenuItem spellLibMenuItem = new JMenuItem("Spell Library");
        spellLibMenuItem.addActionListener(new SpellLibButtonListener());

        JMenuItem rollInitItem = new JMenuItem("Roll initiative for all");
        rollInitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for(Participant part : participants) {
                    part.setInitiative(part.rollInitiative());
                }
                refreshInitiative();
            }
        });


        JMenuItem rollHpItem = new JMenuItem("Roll HP for all");
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

        JMenuItem endEncItem = new JMenuItem("End encounter");
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

        JMenuItem clearEncItem = new JMenuItem("Clear encounter");
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

        fileMenu.add(exitMenuItem);

        encMenu.add(rollInitItem);
        encMenu.add(rollHpItem);
        encMenu.add(endEncItem);
        encMenu.add(clearEncItem);

        libMenu.add(libMenuItem);
        libMenu.add(pcLibMenuItem);
        libMenu.add(spellLibMenuItem);

        menuBar.add(fileMenu);
        menuBar.add(encMenu);
        menuBar.add(libMenu);


        JButton rollInitButton = new JButton("Roll");
        rollInitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!encounterMembers.getSelectionModel().isSelectionEmpty()) {
                    initSpin.setValue((getSelectedParticipant()).rollInitiative());
                }
            }
        });


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

        for (String str : COLUMN_NAMES) {
            model.addColumn(str);
        }

        encounterMembers = new JTable(model) {
            private static final long serialVersionUID = 1L;

            public boolean isCellEditable(int row, int column) {
                return false;
            };
        };
        sorter.setComparator(0, new TurnCompare());
        sorter.toggleSortOrder(0);
        //sorter.setSortsOnUpdates(true);
        sorter.setSortable(1, false);
        sorter.setSortable(2, false);
        sorter.setSortable(0, false);
        encounterMembers.setRowSorter(sorter);
        encounterMembers.getTableHeader().setReorderingAllowed(false);
        encounterMembers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        encounterMembers.getSelectionModel().addListSelectionListener(new EncSelectionListener());
        encounterMembers.getColumnModel().getColumn(0).setPreferredWidth(250);
        encounterMembers.setDefaultRenderer(String.class, new BoardTableCellRenderer());
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

        notesArea.getDocument().addDocumentListener(new DocumentListener() {
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
        notesArea.setEnabled(false);

        frame.add(BorderLayout.CENTER, centPanel);
        frame.setJMenuBar(menuBar);

        frame.pack();
        frame.setMinimumSize(frame.getSize());

        frame.setVisible(true);
    }

    private void updateParticipants() {
        Collections.sort(participants);

        //rename participants appropriately
        ArrayList<Participant> participantsByNum = new ArrayList<Participant>();
        participantsByNum.addAll(participants);
        participantsByNum.sort(new ParticipantNumberCompare());

        for (int i = 0; i < participantsByNum.size(); i++) {
            Participant part = participantsByNum.get(i);
            if (!part.getFightableClass().equals(Player.class)) {
                if (part.getNumber() == 0) {
                    int highestNum = 0;
                    for (int j = 0; j < participantsByNum.size(); j++) {
                        Participant compare = participantsByNum.get(j);
                        if (j != i && part.getBaseName().equals(compare.getBaseName()) && compare.getNumber() > highestNum) {
                            highestNum = compare.getNumber();
                        }
                    }
                    part.setNumber(highestNum + 1);
                    part.setName(part.getBaseName() + " #" + part.getNumber());
                }
            }
        }

        //end renaming
        Collections.sort(participants, new TurnCompare());
        participantsToTable();

        if (participants.size() > 0) {
            notesArea.setEnabled(true);
        }
        else {
            notesArea.setEnabled(false);
        }
    }

    private void participantsToTable() {
        for (int i = model.getRowCount() - 1; i >= 0; i--) {
            model.removeRow(i);
        }

        for (Participant part : participants) {
            model.addRow(new Object[]{part, part.getAc(), part.getHpString(), part.getCurrentRound()});
        }
        encounterMembers.revalidate();
    }

    private Participant getSelectedParticipant() {
        if (encounterMembers.getSelectedRow() != -1) {
            return (Participant) encounterMembers.getValueAt(encounterMembers.getSelectedRow(), 0);
        }
        else return null;
    }


    private void refreshInitiative() {
         //sorter.sort();
         //participants.sort(new InitiativeCompare());
        participants.sort(new TurnCompare());
         participantsToTable();
    }

    private void refreshHpLabel() {
        Participant selected = getSelectedParticipant();
        currentHpLabel.setText(selected.getCurrentHp() + " / " + selected.getMaxHp());
    }

    private void updateAllHp() {
        for (int i = 0; i < encounterMembers.getRowCount(); i++) {
            Participant part = (Participant)encounterMembers.getValueAt(i,0);
            part.updateHpString();
            encounterMembers.setValueAt(part.getHpString(), i, 2);
        }
    }

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
            blockPanel.removeAll();
        }
    }

    private void modHp(int mod) {
        Participant selected = getSelectedParticipant();
        selected.setCurrentHp(selected.getCurrentHp() + mod);
        selected.updateHpString();
        encounterMembers.setValueAt(selected.getHpString(), encounterMembers.getSelectedRow(), 2);
        refreshHpLabel();
        encounterMembers.revalidate();
    }

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

    public class SpellLibButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            PickSpell ps = new PickSpell();
            ps.open(frame);
        }
    }

    public class PcLibButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int range = participants.size();
            ArrayList<Participant> newParticipants = new ArrayList<Participant>();
            for (int i = 0; i < range; i ++) {
                if (!participants.get(i).getFightableClass().equals(Player.class)) {
                    newParticipants.add(participants.get(i));
                }
            }
            participants = newParticipants;
            PickPlayer pp = new PickPlayer(addedPlayers);
            pp.open(frame);
            if (pp.getParticipants() != null) {
                ArrayList<Participant> newParts = new ArrayList<Participant>();
                newParts.addAll(pp.getParticipants());
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
