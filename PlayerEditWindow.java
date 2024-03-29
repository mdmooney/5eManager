import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Michael on 01/12/2014.
 */
public class PlayerEditWindow {
    private JDialog dialog;
    private Player player;
    private JTextField nameField = new JTextField (15);
    private JTextField classField = new JTextField(12);
    private JTextField raceField = new JTextField(12);
    private JTextField bgField = new JTextField(12);
    private JTextField alignField = new JTextField(12);
    private JTextField langField = new JTextField(14);
    private JTextField profField = new JTextField(14);
    private JTextField speedField = new JTextField(14);
    private JTextArea otherNotes = new JTextArea(25, 20);
    private JSpinner strSpin;
    private JSpinner dexSpin;
    private JSpinner conSpin;
    private JSpinner intSpin;
    private JSpinner wisSpin;
    private JSpinner chaSpin;
    private JSpinner levSpin = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
    private JSpinner profSpin = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
    private JSpinner hpSpin = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
    private JSpinner acSpin = new JSpinner(new SpinnerNumberModel(0, 0, 99, 1));
    private JSpinner initSpin = new JSpinner(new SpinnerNumberModel(0, -10, 99, 1));
    private JList traitList = new JList();
    private JList actionList = new JList();
    private ArrayList<Trait> traitArrayList = new ArrayList<Trait>();
    private ArrayList<Action> actionArrayList = new ArrayList<Action>();
    private ArrayList<SpellRef> spellRefArrayList = new ArrayList<SpellRef>();

    ArrayList<JCheckBox> savesBoxes = new ArrayList<JCheckBox>();
    ArrayList<JLabel> savesLabels = new ArrayList<JLabel>();
    ArrayList<JCheckBox> skillsBoxes = new ArrayList<JCheckBox>();
    ArrayList<JLabel> skillsLabels = new ArrayList<JLabel>();

    private int[] modifiers = new int[6];
    private int[] saves = new int[6];
    private int[] skills = new int[18];

    private final int[] STR_SKILLS = {3};
    private final int[] DEX_SKILLS = {0,15,16};
    private final int[] CON_SKILLS = {};
    private final int[] INT_SKILLS = {2,5,8,10,14};
    private final int[] WIS_SKILLS = {1,6,9,11,17};
    private final int[] CHA_SKILLS = {4,7,12,13};

    private final int[][] SKILL_ARRAYS = {STR_SKILLS,DEX_SKILLS,CON_SKILLS,INT_SKILLS,WIS_SKILLS,CHA_SKILLS};

    public PlayerEditWindow() {
        //nope, nothing here
    }

    public PlayerEditWindow(Player player) {
        this.player = player;
    }

    public void open(Window parent) {
        dialog = new JDialog(parent, "Edit Player", Dialog.ModalityType.APPLICATION_MODAL);
        JPanel bigPanel = new JPanel(new GridBagLayout());
        JPanel topPanel = new JPanel(new GridBagLayout());
        JPanel bottomPanel = new JPanel();
        JPanel westPanel = new JPanel(new GridBagLayout());
        JPanel eastPanel = new JPanel(new GridBagLayout());
        JPanel notesPanel = new JPanel(new GridBagLayout());
        JPanel abilPanel = new JPanel(new GridBagLayout());
        JPanel savePanel = new JPanel(new GridBagLayout());
        JPanel skillPanel = new JPanel(new GridBagLayout());
        JPanel miscStatsPanel = new JPanel(new GridBagLayout());

        JScrollPane skillScroller = new JScrollPane(skillPanel);
        JScrollPane actionScroller = new JScrollPane(actionList);
        JScrollPane traitScroller = new JScrollPane(traitList);
        JScrollPane notesScroller = new JScrollPane(otherNotes);

        otherNotes.setWrapStyleWord(true);
        otherNotes.setLineWrap(true);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Abilities, Saves, and Skills", westPanel);

        GridBagConstraints c = new GridBagConstraints();

        //prepare Trait list buttons
        JButton newTraitButton = new JButton("New Trait");
        newTraitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                newTraitWindow();
            }
        });
        JButton editTraitButton = new JButton("Edit Trait");
        editTraitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!traitList.isSelectionEmpty()) editTraitWindow((Trait) traitList.getSelectedValue());
            }
        });
        JButton deleteTraitButton = new JButton("Delete Trait");
        deleteTraitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteSelectedTrait();
            }
        });

        //prepare Action list buttons
        JButton newActionButton = new JButton("New Action");
        newActionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                newActionWindow();
            }
        });
        JButton editActionButton = new JButton("Edit Action");
        editActionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!actionList.isSelectionEmpty()) editActionWindow((Action) actionList.getSelectedValue());
            }
        });
        JButton deleteActionButton = new JButton("Delete Action");
        deleteActionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteSelectedAction();
            }
        });

        //prepare Spellcasting button
        JButton editSpellsButton = new JButton("Edit Spells...");
        editSpellsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                spellcastingWindow();
            }
        });

       //prepare save & cancel buttons, add them to bottom button panel
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveData();
                dialog.dispose();
            }
        });
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                player = null;
                dialog.dispose();
            }
        });
        bottomPanel.add(saveButton);
        bottomPanel.add(cancelButton);

        //create ability score spinners and modifier labels
        strSpin = new JSpinner(new SpinnerNumberModel(10, 0, 99, 1));
        dexSpin = new JSpinner(new SpinnerNumberModel(10, 0, 99, 1));
        conSpin = new JSpinner(new SpinnerNumberModel(10, 0, 99, 1));
        intSpin = new JSpinner(new SpinnerNumberModel(10, 0, 99, 1));
        wisSpin = new JSpinner(new SpinnerNumberModel(10, 0, 99, 1));
        chaSpin = new JSpinner(new SpinnerNumberModel(10, 0, 99, 1));
        ModifierLabel strModLabel = new ModifierLabel();
        ModifierLabel conModLabel = new ModifierLabel();
        ModifierLabel dexModLabel = new ModifierLabel();
        ModifierLabel intModLabel = new ModifierLabel();
        ModifierLabel wisModLabel = new ModifierLabel();
        ModifierLabel chaModLabel = new ModifierLabel();
        strSpin.addChangeListener(new AbilSpinnerListener(strModLabel, 0));
        dexSpin.addChangeListener(new AbilSpinnerListener(dexModLabel, 1));
        conSpin.addChangeListener(new AbilSpinnerListener(conModLabel, 2));
        intSpin.addChangeListener(new AbilSpinnerListener(intModLabel, 3));
        wisSpin.addChangeListener(new AbilSpinnerListener(wisModLabel, 4));
        chaSpin.addChangeListener(new AbilSpinnerListener(chaModLabel, 5));
        addNumberListener(strSpin);
        addNumberListener(dexSpin);
        addNumberListener(conSpin);
        addNumberListener(intSpin);
        addNumberListener(wisSpin);
        addNumberListener(chaSpin);

        addNumberListener(profSpin);
        profSpin.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                updateSkillModifiers();
                updateLabels();
            }
        });

        //set up save check boxes and labels
        for (int i = 0; i < 6; i ++) {
            JCheckBox check = new JCheckBox();
            savesBoxes.add(check);
            check.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int index = savesBoxes.indexOf(e.getSource());
                    updateSkillModifiers();
                    updateLabels();
                }
            });

            JLabel label = new JLabel("+0");
            savesLabels.add(label);
        }

        for (int i = 0; i < 18; i ++) {
            JCheckBox check = new JCheckBox();
            skillsBoxes.add(check);
            check.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int index = skillsBoxes.indexOf(e.getSource());
                    updateSkillModifiers();
                    updateLabels();
                }
            });

            JLabel label = new JLabel("+0");
            //label.setFont(new Font(label.getFont().getName(), Font.PLAIN, 10));
            skillsLabels.add(label);
        }

        //add components to top panel
        c.gridx=0;
        c.gridy=0;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx=0;
        topPanel.add(new JLabel("Name: "),c);
        c.gridx++;
        topPanel.add(nameField,c);
        c.gridx++;
        topPanel.add(new JLabel("Class: "), c);
        c.gridy++;
        topPanel.add(new JLabel("Race: "), c);
        c.gridy--;
        c.gridx++;
        topPanel.add(classField,c);
        c.gridy++;
        topPanel.add(raceField,c);
        c.gridy--;
        c.gridx++;
        topPanel.add(new JLabel(" Background: "), c);
        c.gridy++;
        topPanel.add(new JLabel(" Alignment: "), c);
        c.gridy--;
        c.gridx++;
        topPanel.add(bgField,c);
        c.gridy++;
        topPanel.add(alignField,c);

        //add ability score spinners and labels to panel, and set up border
        abilPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        c.gridx=0;
        c.gridy=0;
        c.anchor=GridBagConstraints.CENTER;
        c.insets = (new Insets(3,3,3,3));
        abilPanel.add(new JLabel("STR"));
        c.gridy++;
        abilPanel.add(strSpin, c);
        c.gridy++;
        abilPanel.add(strModLabel, c);
        c.gridy-=2;
        c.gridx++;
        abilPanel.add(new JLabel("DEX"),c);
        c.gridy++;
        abilPanel.add(dexSpin, c);
        c.gridy++;
        abilPanel.add(dexModLabel, c);
        c.gridy-=2;
        c.gridx++;
        abilPanel.add(new JLabel("CON"),c);
        c.gridy++;
        abilPanel.add(conSpin, c);
        c.gridy++;
        abilPanel.add(conModLabel, c);
        c.gridy-=2;
        c.gridx++;
        abilPanel.add(new JLabel("INT"),c);
        c.gridy++;
        abilPanel.add(intSpin, c);
        c.gridy++;
        abilPanel.add(intModLabel, c);
        c.gridy-=2;
        c.gridx++;
        abilPanel.add(new JLabel("WIS"),c);
        c.gridy++;
        abilPanel.add(wisSpin, c);
        c.gridy++;
        abilPanel.add(wisModLabel, c);
        c.gridy-=2;
        c.gridx++;
        abilPanel.add(new JLabel("CHA"),c);
        c.gridy++;
        abilPanel.add(chaSpin, c);
        c.gridy++;
        abilPanel.add(chaModLabel, c);

        //setup saving throw panel
        c.gridy=0;
        c.gridx=0;
        for (JCheckBox check : savesBoxes) {
            c.gridy++;
            savePanel.add(check, c);
        }
        c.gridx=2;
        c.gridy-=6;
        c.gridwidth=1;
        for (JLabel label : savesLabels) {
            c.gridy++;
            savePanel.add(label, c);
        }
        c.gridy-=5;
        c.gridx++;
        savePanel.add(new JLabel("Strength"),c);
        c.gridy++;
        savePanel.add(new JLabel("Dexterity"),c);
        c.gridy++;
        savePanel.add(new JLabel("Constitution"),c);
        c.gridy++;
        savePanel.add(new JLabel("Intelligence"),c);
        c.gridy++;
        savePanel.add(new JLabel("Wisdom"),c);
        c.gridy++;
        savePanel.add(new JLabel("Charisma"),c);

        //setup skills panel
        c.gridwidth=1;
        c.gridy=0;
        for (JCheckBox check : skillsBoxes) {
            c.gridy++;
            skillPanel.add(check, c);
        }
        c.gridy-=18;
        c.gridx+=2;
        for (JLabel label : skillsLabels) {
            c.gridy++;
            skillPanel.add(label, c);
        }
        c.gridy-=17;
        c.gridx++;
        skillPanel.add(new JLabel("Acrobatics (Dex)"), c);
        c.gridy++;
        skillPanel.add(new JLabel("Animal Handling (Wis)"),c);
        c.gridy++;
        skillPanel.add(new JLabel("Arcana (Int)"),c);
        c.gridy++;
        skillPanel.add(new JLabel("Athletics (Str)"),c);
        c.gridy++;
        skillPanel.add(new JLabel("Deception (Cha)"),c);
        c.gridy++;
        skillPanel.add(new JLabel("History (Int)"),c);
        c.gridy++;
        skillPanel.add(new JLabel("Insight (Wis)"),c);
        c.gridy++;
        skillPanel.add(new JLabel("Intimidation (Cha)"),c);
        c.gridy++;
        skillPanel.add(new JLabel("Investigation (Int)"),c);
        c.gridy++;
        skillPanel.add(new JLabel("Medicine (Wis)"),c);
        c.gridy++;
        skillPanel.add(new JLabel("Nature (Int)"),c);
        c.gridy++;
        skillPanel.add(new JLabel("Perception (Wis)"),c);
        c.gridy++;
        skillPanel.add(new JLabel("Performance (Cha)"),c);
        c.gridy++;
        skillPanel.add(new JLabel("Persuasion (Cha)"),c);
        c.gridy++;
        skillPanel.add(new JLabel("Religion (Int)"),c);
        c.gridy++;
        skillPanel.add(new JLabel("Sleight of Hand (Dex)"),c);
        c.gridy++;
        skillPanel.add(new JLabel("Stealth (Dex)"),c);
        c.gridy++;
        skillPanel.add(new JLabel("Survival (Wis)"),c);


        //add components to West panel
        c.insets = (new Insets(0,5,0,0));
        c.anchor = GridBagConstraints.NORTHWEST;
        c.gridx=0;
        c.gridy=0;
        miscStatsPanel.add(new JLabel("Max HP: "),c);
        c.gridx++;
        miscStatsPanel.add(hpSpin, c);
        c.gridx--;
        c.gridy++;
        miscStatsPanel.add(new JLabel("Level: "),c);
        c.gridx++;
        miscStatsPanel.add(levSpin, c);
        c.gridx--;
        c.gridy++;
        miscStatsPanel.add(new JLabel("AC: "),c);
        c.gridx++;
        miscStatsPanel.add(acSpin, c);
        c.gridx--;
        c.gridy++;
        miscStatsPanel.add(new JLabel("Initiative: "),c);
        c.gridx++;
        miscStatsPanel.add(initSpin, c);
        c.gridx--;
        c.gridy++;
        miscStatsPanel.add(new JLabel("Speed: "),c);
        c.gridx++;
        miscStatsPanel.add(speedField, c);
        c.gridx=0;
        c.gridwidth= GridBagConstraints.REMAINDER;
        c.gridy=0;
        westPanel.add(miscStatsPanel, c);
        c.gridy++;
        westPanel.add(abilPanel, c);
        c.gridwidth = 1;
        c.gridy++;
        westPanel.add(new JLabel("Proficiency Bonus: "),c);
        c.gridx++;
        westPanel.add(profSpin, c);
        c.gridy++;
        c.gridx=0;
        westPanel.add(new JLabel("—SAVES—"),c);
        c.gridy++;
        westPanel.add(savePanel, c);
        c.gridx++;
        c.gridheight=1;
        c.gridy--;
        westPanel.add(new JLabel("—SKILLS—"),c);
        c.gridy++;
        westPanel.add(skillScroller, c);
        skillScroller.setPreferredSize(new Dimension(200, 200));
        c.gridx=0;
        c.gridy++;
        c.fill=GridBagConstraints.HORIZONTAL;
        c.gridwidth=GridBagConstraints.REMAINDER;
        westPanel.add(new JSeparator(SwingConstants.HORIZONTAL),c);
        c.gridy++;
        c.gridwidth=1;
        c.fill=GridBagConstraints.NONE;
        westPanel.add(new JLabel("Languages: "),c);
        c.gridx++;
        westPanel.add(langField, c);
        c.gridx--;
        c.gridy++;
        westPanel.add(new JLabel("Other Profiencies: "),c);
        c.gridx++;
        westPanel.add(profField, c);

        //add components to east panel
        c.gridx=0;
        c.insets = new Insets(0,0,0,0);
        c.fill=GridBagConstraints.HORIZONTAL;
        c.anchor=GridBagConstraints.NORTH;
        c.gridwidth=3;
        c.gridy=0;
        eastPanel.add(new JLabel("Traits and Features"),c);
        c.gridy++;
        eastPanel.add(traitScroller,c);
        c.gridy++;
        c.gridwidth=1;
        eastPanel.add(newTraitButton, c);
        c.gridx++;
        eastPanel.add(editTraitButton, c);
        c.gridx++;
        eastPanel.add(deleteTraitButton, c);
        c.gridx=0;
        c.gridy++;
        c.gridwidth=GridBagConstraints.REMAINDER;
        eastPanel.add(new JLabel("Attacks and Actions"),c);
        c.gridy++;
        eastPanel.add(actionScroller,c);
        c.gridy++;
        c.gridwidth=1;
        eastPanel.add(newActionButton, c);
        c.gridx++;
        eastPanel.add(editActionButton, c);
        c.gridx++;
        eastPanel.add(deleteActionButton, c);
        c.gridy++;
        c.gridx=0;
        eastPanel.add(new JLabel("Spellcasting"),c);
        c.gridwidth=2;
        c.gridy++;
        eastPanel.add(editSpellsButton,c);

        //add components to south panel
        c.gridx=0;
        c.gridy=0;
        c.gridwidth=1;
        notesPanel.add(new JLabel("Other notes"), c);
        c.gridy++;
        notesPanel.add(notesScroller, c);


        //create big panel
        c.gridx = 0;
        c.gridwidth=1;
        c.gridy = 0;
        c.anchor = GridBagConstraints.NORTH;
        bigPanel.add(westPanel, c);
        c.gridx++;
        c.insets = new Insets(0,6,0,6);
        c.fill=GridBagConstraints.VERTICAL;
        bigPanel.add(new JSeparator(SwingConstants.VERTICAL),c);
        c.fill=GridBagConstraints.NONE;
        c.insets = new Insets(0,0,0,0);
        c.gridx++;
        bigPanel.add(eastPanel, c);
        c.gridx++;
        c.insets = new Insets(0,6,0,6);
        c.fill=GridBagConstraints.VERTICAL;
        bigPanel.add(new JSeparator(SwingConstants.VERTICAL), c);
        c.fill=GridBagConstraints.NONE;
        c.insets = new Insets(0,0,0,0);
        c.gridx++;
        bigPanel.add(notesPanel, c);

        savePanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

        dialog.add(BorderLayout.NORTH, topPanel);
        dialog.add(BorderLayout.CENTER, bigPanel);
        dialog.add(BorderLayout.SOUTH, bottomPanel);

        //if there's a player reference already in there (i.e. if we're in "Edit" or "Copy" mode), set it up
        if (this.player != null) {
            try {
                traitArrayList.addAll(player.getTraitList());

            }
            catch (NullPointerException nex) {
                traitArrayList = new ArrayList<Trait>();
            }
            try {
                actionArrayList.addAll(player.getActionList());
            }
            catch (NullPointerException nex) {
                actionArrayList = new ArrayList<Action>();
            }
            try {
                spellRefArrayList.addAll(player.getSpellRefs());
            }
            catch (NullPointerException nex) {
                spellRefArrayList = new ArrayList<SpellRef>();
            }
            fillFields();
        }

        dialog.setSize(800, 600);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }

    private void fillFields() {
        nameField.setText(player.getName());
        classField.setText(player.getClasses());
        raceField.setText(player.getRace());
        bgField.setText(player.getBackground());
        alignField.setText(player.getAlignment());
        langField.setText(player.getLang());
        profField.setText(player.getProfs());
        speedField.setText(player.getSpeed());
        otherNotes.setText(player.getNotes());
        strSpin.setValue(player.getScore(0));
        dexSpin.setValue(player.getScore(1));
        conSpin.setValue(player.getScore(2));
        intSpin.setValue(player.getScore(3));
        wisSpin.setValue(player.getScore(4));
        chaSpin.setValue(player.getScore(5));
        hpSpin.setValue(player.getHp());
        acSpin.setValue(player.getAc());
        levSpin.setValue(player.getLevel());
        initSpin.setValue(player.getInitiative());
        profSpin.setValue(player.getProficiency());
        for (int i = 0; i < savesBoxes.size(); i++) {
            if (player.getSaves()[i] >= modifiers[i] + player.getProficiency()) {
                savesBoxes.get(i).setSelected(true);
            }
        }
        for (int i = 0; i < SKILL_ARRAYS.length; i++) {
            for (int j = 0; j < SKILL_ARRAYS[i].length; j++) {
                if (player.getSkillMods()[SKILL_ARRAYS[i][j]] >= modifiers[i] + player.getProficiency()) {
                    skillsBoxes.get(SKILL_ARRAYS[i][j]).setSelected(true);
                }
            }
        }
        updateSkillModifiers();
        updateLabels();
        updateTraitList();
        updateActionList();
        //updateSpellcasting();
    }

    private void updateSkillModifiers() {
        for (int i = 0; i < 6 ; i++) {
            saves[i] = modifiers[i] + (savesBoxes.get(i).isSelected() ? (Integer) profSpin.getValue() : 0);
            for (int k : SKILL_ARRAYS[i]) {
                skills[k] = modifiers[i] + (skillsBoxes.get(k).isSelected() ? (Integer) profSpin.getValue() : 0);
            }
        }
    }

    private void updateLabels() {
        for (int i = 0; i < 6; i ++) {
            savesLabels.get(i).setText("" + (saves[i] >= 0 ? "+" + saves[i] : saves[i]));
            for (int j : SKILL_ARRAYS[i]) {
                skillsLabels.get(j).setText("" + (skills[j] >= 0 ? "+" + skills[j] : skills[j]));
            }
        }
    }

    private void addNumberListener(JSpinner spin) {
        ((JSpinner.NumberEditor) spin.getEditor()).getTextField().addFocusListener(new NumberFocusListener());
    }

    public void newTraitWindow() {
        TraitEditWindow tew = new TraitEditWindow(dialog);
        tew.open();
        try {
            Trait tewTrait = tew.getTrait();
            if (tewTrait != null) {
                traitArrayList.add(tewTrait);
            }
        }
        catch(NullPointerException nex) {
            nex.printStackTrace();
        }
        updateTraitList();
    }

    private void editTraitWindow(Trait trait) {
        TraitEditWindow tew = new TraitEditWindow(dialog, trait);
        tew.open();
        Trait tewTrait = tew.getTrait();
        if (tewTrait != null) {
            traitArrayList.remove(trait);
            traitArrayList.add(tewTrait);
        }
        updateTraitList();
    }

    private void deleteSelectedTrait() {
        if (!traitList.isSelectionEmpty()) {
            Object[] options = {"Delete", "Cancel"};
            int choice = JOptionPane.showOptionDialog(dialog, "Permanently delete the selected trait?", "Confirm deletion",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
            if (choice == 0) { // if the user selects "Yes" to deleting the selected trait
                traitArrayList.remove(traitList.getSelectedValue());
                updateTraitList();
            }
        }
    }

    private void updateTraitList() {
        Collections.sort(traitArrayList);
        traitList.setListData(traitArrayList.toArray());
        traitList.revalidate();
    }

    private void newActionWindow() {
        ActionEditWindow aew = new ActionEditWindow(dialog);
        aew.open();
        try {
            Action aewAction = aew.getAction();
            if (aewAction != null) {
                actionArrayList.add(aewAction);
            }
        }
        catch(NullPointerException nex) {
            nex.printStackTrace();
        }
        updateActionList();
    }



    private void editActionWindow(Action action) {
        ActionEditWindow aew = new ActionEditWindow(dialog, action);
        aew.open();
        Action aewAction = aew.getAction();
        if (aewAction != null) {
            actionArrayList.remove(action);
            actionArrayList.add(aewAction);
        }
        updateActionList();
    }

    private void updateActionList() {
        Collections.sort(actionArrayList);
        actionList.setListData(actionArrayList.toArray());
        actionList.revalidate();
    }

    private void deleteSelectedAction() {
        if (!actionList.isSelectionEmpty()) {
            Object[] options = {"Delete", "Cancel"};
            int choice = JOptionPane.showOptionDialog(dialog, "Permanently delete the selected action?", "Confirm deletion",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
            if (choice == 0) { // if the user selects "Yes" to deleting the selected action
                actionArrayList.remove(actionList.getSelectedValue());
                updateActionList();
            }
        }
    }

    private void spellcastingWindow() {
        SpellcastingEditWindow sew = new SpellcastingEditWindow(spellRefArrayList);
        sew.open(dialog);
        ArrayList<SpellRef> newSpellRefList = sew.getSpellRefList();
        if (newSpellRefList != null) {
            this.spellRefArrayList = newSpellRefList;
        }
    }

    private void saveData() {
        if (!nameField.getText().equals("")) {
            player = new Player();
            player.setName(nameField.getText());
            player.setHp((Integer) hpSpin.getValue());
            player.setAc((Integer) acSpin.getValue());
            player.setClasses(classField.getText());
            player.setRace(raceField.getText());
            player.setBackground(bgField.getText());
            player.setAlignment(alignField.getText());
            player.setSpeed(speedField.getText());
            player.setInitiative((Integer) initSpin.getValue());
            player.setLevel((Integer) levSpin.getValue());
            player.setSkillMods(skills);
            setPlayerScores();
            player.setSaves(saves);
            player.setLang(fieldCheck(langField));
            player.setTraitList(traitArrayList);
            player.setActionList(actionArrayList);
            player.setSpellRefs(spellRefArrayList);
            player.setNotes(otherNotes.getText());
            player.setProfs(profField.getText());
            player.setProficiency((Integer) profSpin.getValue());
            dialog.dispose();
        }
        else {
            JOptionPane.showMessageDialog(dialog, "At a minimum, you must enter a name to save a player character.");
        }
    }

    public Player getPlayer() {
        return player;
    }

    private void setPlayerScores() {
        player.setScore(0, (Integer) strSpin.getValue()); //set Str
        player.setScore(1, (Integer) dexSpin.getValue()); // set Dex
        player.setScore(2, (Integer) conSpin.getValue()); // set Con
        player.setScore(3, (Integer) intSpin.getValue()); // set Int
        player.setScore(4, (Integer) wisSpin.getValue()); // set Wis
        player.setScore(5, (Integer) chaSpin.getValue()); // set Cha
    }


    /**
     * returns the content of a text field if it is not empty.
     * @param field The text field to be checked.
     * @return Text contained in the field, or null.
     */
    private String fieldCheck(JTextField field) {
        if (!field.getText().equals("")) {
            return field.getText();
        }
        return null;
    }


    /**
     * A listener for the ability score spinners.
     */

    class AbilSpinnerListener implements ChangeListener {
        ModifierLabel modLabel;
        int modIndex;

        /**
         * @param modIndex Index for the modifier in the modifier array (0 = Str, 1 = Dex...).
         * @param modLabel ModifierLabel object associated with the spinner.
         */

        public AbilSpinnerListener(ModifierLabel modLabel, int modIndex) {
            this.modLabel = modLabel;
            this.modIndex = modIndex;
        }

        public void stateChanged(ChangeEvent event) {
            JSpinner source = (JSpinner) event.getSource();
            int value = (Integer) source.getValue();
            int newMod =  value/2 - 5;
            modLabel.updateLabel(newMod); //tell the corresponding label to update itself.
            modifiers[modIndex] = newMod;
            updateSkillModifiers();
            updateLabels();
        }
    }
}
