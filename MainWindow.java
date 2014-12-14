import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import javax.swing.UIManager.*;

/**
 * Created by Michael on 25/11/2014.
 */
public class MainWindow {
    private JFrame frame;
    private ArrayList<Participant> participants = new ArrayList<Participant>();
    private JList encounterMembers;
    private JSpinner initSpin = new JSpinner(new SpinnerNumberModel(0, 0, 99, 1));
    private JSpinner hpChangeSpin = new JSpinner(new SpinnerNumberModel(0, 0, 999, 1));
    private JPanel blockPanel;
    private JTextArea blankTextArea = new JTextArea(20, 20);
    private JLabel currentHpLabel = new JLabel(" / ");
    private HashMap<Participant, Integer> addedPlayers;

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
                if (!encounterMembers.isSelectionEmpty()) {
                    ((Participant) encounterMembers.getSelectedValue()).getRandomHp();
                    refreshHpLabel();
                }
            }
        });
        JButton hurtButton = new JButton("Damage");
        hurtButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!encounterMembers.isSelectionEmpty()) {
                    modHp(- (Integer) hpChangeSpin.getValue());
                }
            }
        });

        JButton healButton = new JButton("Heal");
        healButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!encounterMembers.isSelectionEmpty()) {
                    modHp((Integer) hpChangeSpin.getValue());
                }
            }
        });

        GridBagConstraints c = new GridBagConstraints();

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu encMenu = new JMenu("Encounter");
        JMenu libMenu = new JMenu("Libraries");

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
                    if (!encounterMembers.isSelectionEmpty()) refreshHpLabel();
                }
            }
        });

        JMenuItem clearEncItem = new JMenuItem("Clear encounter");
        clearEncItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                participants.clear();
                updateParticipants();
                blockPanel.removeAll();
                blockPanel.add(blankTextArea);
            }
        });

        encMenu.add(rollInitItem);
        encMenu.add(rollHpItem);
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
                if (!encounterMembers.isSelectionEmpty()) {
                    initSpin.setValue(((Participant) encounterMembers.getSelectedValue()).rollInitiative());
                }
            }
        });


        c.gridy=0;
        c.gridx=0;
        controlPanel.add(new JLabel("Initiative: "),c);
        initSpin.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (!encounterMembers.isSelectionEmpty()) {
                    ((Participant) encounterMembers.getSelectedValue()).setInitiative((Integer) initSpin.getValue());
                    refreshInitiative();
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

        encounterMembers = new JList();
        encounterMembers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        encounterMembers.addListSelectionListener(new EncSelectionListener());
        JScrollPane encScroller = new JScrollPane(encounterMembers);
        encScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        encScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        c.gridx = 0;
        c.gridy=0;
        c.weightx=1;
        c.weighty=1;
        c.fill = GridBagConstraints.BOTH;
        c.anchor=GridBagConstraints.WEST;
        centPanel.add(encScroller,c);
        c.gridx++;
        c.weightx=0;
        c.anchor=GridBagConstraints.CENTER;
        centPanel.add(controlPanel,c);
        c.gridx++;
        c.weightx=1;
        c.anchor=GridBagConstraints.EAST;
        centPanel.add(blockPanel,c);

        frame.add(BorderLayout.CENTER, centPanel);
        frame.setJMenuBar(menuBar);

        frame.pack();
        frame.setMinimumSize(frame.getSize());
        frame.setVisible(true);
    }

    private void updateParticipants() {
        Collections.sort(participants);
        //rename participants appropriately
        for (Participant p : participants) {
            p.setName(p.getBaseName());
        }
        int sameCount = 1;
        for (int i = 0; i < participants.size(); i++) {
            if (!participants.get(i).getFightableClass().equals(Player.class)) {
                if (i > 0 && participants.get(i).getBaseName().equals(participants.get(i - 1).getBaseName())) {
                    sameCount++;
                } else sameCount = 1;
                participants.get(i).setName(participants.get(i).getBaseName() + " #" + sameCount);
            }
        }
        //end renaming
        Collections.sort(participants, new InitiativeCompare());
        encounterMembers.setListData(participants.toArray());
        encounterMembers.revalidate();
    }

    private void refreshInitiative() {
        Object selection = encounterMembers.getSelectedValue();
        Collections.sort(participants, new InitiativeCompare());
        encounterMembers.setListData(participants.toArray());
        encounterMembers.revalidate();
        encounterMembers.setSelectedValue(selection, true);
    }

    private void refreshHpLabel() {
        Participant selected = (Participant) encounterMembers.getSelectedValue();
        currentHpLabel.setText(selected.getCurrentHp() + " / " + selected.getMaxHp());

    }

    private void modHp(int mod) {
        Participant selected = (Participant) encounterMembers.getSelectedValue();
        selected.setCurrentHp(selected.getCurrentHp() + mod);
        refreshHpLabel();
    }

    public class LibButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            PickMonster pm = new PickMonster();
            pm.open(frame);
            if (pm.getParticipants() != null) {
                participants.addAll(pm.getParticipants());
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
                participants.addAll(pp.getParticipants());
            }
            updateParticipants();
        }
    }

    public class InitiativeCompare implements Comparator<Participant> {
        public int compare(Participant pOne, Participant pTwo) {
            return Integer.compare(pOne.getInitiative(), pTwo.getInitiative()) * -1;
        }
    }

    class EncSelectionListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent lse) {
            try {
                Participant selected = (Participant) encounterMembers.getSelectedValue();
                blockPanel.removeAll();
                blockPanel.add(selected.getBlock());
                blockPanel.revalidate();
                initSpin.setValue(selected.getInitiative());
                refreshHpLabel();
            }
            catch(NullPointerException npex) {
                blockPanel.removeAll();
            }

        }
    }
}
