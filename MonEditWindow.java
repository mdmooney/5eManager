/**
 * Created by Michael on 03/11/2014.
 */

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;

public class MonEditWindow {
    private Monster monster;
    private JTextField nameField;
    private JTextField typeField;
    private JTextField speedField;
    private JTextField savesField;
    private JTextField skillsField;
    private JTextField vulnField;
    private JTextField resistField;
    private JTextField immuField;
    private JTextField conImmuField;
    private JTextField senseField;
    private JTextField langField;
    private JSpinner hpSpin;
    private JSpinner hdNumSpin;
    private JSpinner hdBonusSpin;
    private JSpinner acSpin;
    private JSpinner strSpin;
    private JSpinner dexSpin;
    private JSpinner conSpin;
    private JSpinner intSpin;
    private JSpinner wisSpin;
    private JSpinner chaSpin;
    private JSpinner crSpin;
    private JSpinner legNumSpin;
    private JComboBox hdTypeBox = new JComboBox(ManagerConstants.DICE_TYPES);
    private SpinnerNumberModel crSpinModel = new SpinnerNumberModel(0, 0, 30, 0.125);
    private JList traitList;
    private JList actionList;
    private ArrayList<Trait> traitArrayList = new ArrayList<Trait>();
    private ArrayList<Action> actionArrayList = new ArrayList<Action>();
    private ArrayList<LegendaryAction> legendaryActionArrayList = new ArrayList<LegendaryAction>();

    int[] modifiers = new int[6]; //ability score modifiers are stored in this array. they default to 0 because the abilities default to 10
    JDialog frame;

    private final Dimension LIST_DIMENSION = new Dimension(280, 180);

    public MonEditWindow() {
        //nope, nothing here
    }

    public MonEditWindow(Monster monster) {
        this.monster = monster;
    }

    public void open(JDialog parent) {
        frame = new JDialog(parent, "Create New Monster", Dialog.ModalityType.APPLICATION_MODAL);
        frame.addWindowListener(new WindowAdapter() {
                                    public void windowClosing(WindowEvent e) {
                                        monster = null; //make the monster null on exiting so it isn't added to the library if it's undesired
                                    }
                                });
        JPanel editPanel = new JPanel(new GridBagLayout());
        JPanel buttonPanel = new JPanel();
        JPanel abilPanel = new JPanel(new GridLayout(3,6));
        JPanel powerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.weighty = 1;
        c.anchor = GridBagConstraints.NORTHWEST;

        //set up text fields
        nameField = new JTextField(18);
        typeField = new JTextField(18);
        speedField = new JTextField(18);
        savesField = new JTextField(24);
        skillsField = new JTextField(24);
        vulnField = new JTextField(24);
        resistField = new JTextField(24);
        immuField = new JTextField(24);
        conImmuField = new JTextField(24);
        senseField = new JTextField(24);
        langField = new JTextField(24);

        //set up spinners for non-ability score properties, and add the model-changing listener to the challenge spinner
        hpSpin = new JSpinner(new SpinnerNumberModel(0, 0, 9999, 1));
        addNumberListener(hpSpin);
        hdNumSpin = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
        addNumberListener(hdNumSpin);
        hdBonusSpin = new JSpinner(new SpinnerNumberModel(0, 0, 999, 1));
        addNumberListener(hdBonusSpin);
        acSpin = new JSpinner(new SpinnerNumberModel(0, 0, 99, 1));
        addNumberListener(acSpin);
        legNumSpin = new JSpinner(new SpinnerNumberModel(0,0, 10, 1));
        addNumberListener(legNumSpin);
        crSpin = new JSpinner(crSpinModel);
        addNumberListener(crSpin);
        crSpin.addChangeListener(new ChangeListener() {
           public void stateChanged(ChangeEvent e) {
               double value = (Double) crSpin.getValue();
               if(value > 1.0) {
                   crSpinModel.setStepSize(1.0);
                   crSpin.setValue(Math.ceil((Double) crSpin.getValue()));
               }

               else if (value > 0.5) {
                   crSpinModel.setStepSize(0.5);
                   if (value == 0.75 ){
                       crSpin.setValue(1.0);
                   }
                   else if (value % 0.5 != 0) {
                       crSpin.setValue(value - value % 0.5);
                   }
               }

               else if (value > 0.25) {
                   crSpinModel.setStepSize(0.25);
                   if (value == 0.375) {
                       crSpin.setValue(0.5);
                   }
                   else if (value % 0.25 != 0) {
                       crSpin.setValue(value - value % 0.25);
                   }
               }
               else {
                   crSpinModel.setStepSize(0.125);
                   if (value % 0.125 != 0) {
                       crSpin.setValue(value - value % 0.125);
                   }
               }
           }
        });


        //prepare the "add to library", "cancel" buttons
        JButton addButton = new JButton("Add to Library");
        addButton.addActionListener(new AddButtonListener());

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                monster = null;
                frame.dispose();
            }
        });

        //prepare legendary action edit button
        JButton legEditButton = new JButton("Edit Legendary Actions");
        legEditButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               editLegendaryWindow();
           }
        });

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

        //add ability score labels, spinners, and modifier labels to ability score panel
        abilPanel.add(new JLabel("STR"));
        abilPanel.add(new JLabel("DEX"));
        abilPanel.add(new JLabel("CON"));
        abilPanel.add(new JLabel("INT"));
        abilPanel.add(new JLabel("WIS"));
        abilPanel.add(new JLabel("CHA"));
        abilPanel.add(strSpin);
        abilPanel.add(dexSpin);
        abilPanel.add(conSpin);
        abilPanel.add(intSpin);
        abilPanel.add(wisSpin);
        abilPanel.add(chaSpin);
        abilPanel.add(strModLabel);
        abilPanel.add(dexModLabel);
        abilPanel.add(conModLabel);
        abilPanel.add(intModLabel);
        abilPanel.add(wisModLabel);
        abilPanel.add(chaModLabel);

        //create Hit Dice panel and add appropriate spinners + combo box
        JPanel hdPanel = new JPanel(new GridBagLayout());
        c.gridx=0;
        hdPanel.add(hdNumSpin,c);
        c.gridx++;
        hdPanel.add(hdTypeBox,c);
        c.gridx++;
        hdPanel.add(new JLabel(" + "),c);
        c.gridx++;
        hdPanel.add(hdBonusSpin,c);

        //add first set of labels to the window (above ability scores)
        c.weightx=0;
        c.gridx=0;
        c.gridy=0;
        c.weighty=0;
        editPanel.add(new JLabel("Name: "), c);
        c.gridy++;
        editPanel.add(new JLabel("Type: "),c);
        c.gridy++;
        editPanel.add(new JLabel("AC: "),c);
        c.gridy++;
        editPanel.add(new JLabel("HP: "),c);
        c.gridy++;
        editPanel.add(new JLabel("Hit Dice: "), c);
        c.gridy++;
        editPanel.add(new JLabel("Speed: "),c);
        c.gridy++;
        editPanel.add(new JLabel("Challenge: "),c);

        //add first set of entry boxes to the window (above ability scores)
        c.gridx=1;
        c.weightx=1;
        c.gridy=0;
        c.gridwidth=GridBagConstraints.REMAINDER;
        editPanel.add(nameField, c);
        c.gridy=1;
        editPanel.add(typeField, c);
        c.gridy=2;
        editPanel.add(acSpin,c);
        c.gridy=3;
        editPanel.add(hpSpin,c);
        c.gridy++;
        editPanel.add(hdPanel,c);
        c.gridy++;
        c.gridx=1;
        c.gridwidth=GridBagConstraints.REMAINDER;
        editPanel.add(speedField,c);
        c.gridy++;
        editPanel.add(crSpin, c);

        //add ability score panel to the main panel
        c.gridy++;
        c.gridx=0;
        c.gridwidth=GridBagConstraints.REMAINDER;
        editPanel.add(abilPanel,c);

        //add next set of (single-column) fields to the main panel
        c.gridy++;
        editPanel.add(new JLabel("Saving Throws: "), c);
        c.gridy++;
        editPanel.add(savesField,c);
        c.gridy++;
        editPanel.add(new JLabel("Skills: "), c);
        c.gridy++;
        editPanel.add(skillsField,c);
        c.gridy++;
        editPanel.add(new JLabel("Damage Vulnerabilities: "), c);
        c.gridy++;
        editPanel.add(vulnField,c);
        c.gridy++;
        editPanel.add(new JLabel("Damage Resistances: "), c);
        c.gridy++;
        editPanel.add(resistField,c);
        c.gridy++;
        editPanel.add(new JLabel("Damage Immunities: "), c);
        c.gridy++;
        editPanel.add(immuField,c);
        c.gridy++;
        editPanel.add(new JLabel("Condition Immunities: "), c);
        c.gridy++;
        editPanel.add(conImmuField,c);
        c.gridy++;
        editPanel.add(new JLabel("Senses: "), c);
        c.gridy++;
        editPanel.add(senseField,c);
        c.gridy++;
        editPanel.add(new JLabel("Languages: "), c);
        c.gridy++;
        c.weighty=1; //todo: move this constraint assignment to just above the last component as the panel gets bigger
        editPanel.add(langField,c);

        //set up power panel
        c.gridy=0;
        c.weightx=0;
        c.weighty=0;
        traitList = new JList();
        traitList.addKeyListener(new TraitListListener());
        traitList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane traitScroller = new JScrollPane(traitList);
        traitScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        traitScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        traitScroller.setPreferredSize(LIST_DIMENSION);
        actionList = new JList();
        actionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane actionScroller = new JScrollPane(actionList);
        actionScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        actionScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        actionScroller.setPreferredSize(LIST_DIMENSION);
        powerPanel.add(new JLabel("Traits"), c);
        c.gridy++;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = GridBagConstraints.REMAINDER;
        powerPanel.add(traitScroller,c);
        c.gridy++;
        c.gridwidth = 1;
        powerPanel.add(newTraitButton,c);
        c.gridx++;
        powerPanel.add(editTraitButton,c);
        c.gridx++;
        powerPanel.add(deleteTraitButton, c);
        c.gridy+=2;
        c.gridx=0;
        powerPanel.add(new JLabel("Actions"),c);
        c.gridy++;
        c.gridwidth = GridBagConstraints.REMAINDER;
        powerPanel.add(actionScroller,c);
        c.gridy++;
        c.weighty=1;
        c.weightx=1;
        c.gridwidth = 1;
        powerPanel.add(newActionButton,c);
        c.gridx++;
        powerPanel.add(editActionButton,c);
        c.gridx++;
        powerPanel.add(deleteActionButton, c);
        c.gridx=0;
        c.gridy++;
        powerPanel.add(new JLabel("Legendary Actions : "),c);
        c.gridx++;
        c.fill=GridBagConstraints.NONE;
        powerPanel.add(legNumSpin, c);
        c.gridy++;
        c.gridx=0;
        c.fill=GridBagConstraints.HORIZONTAL;
        c.gridwidth=2;
        powerPanel.add(legEditButton, c);



        //include the button panel buttons
        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        //if there's a monster reference during setup (i.e. if we're in "edit mode"), pull their stats and display them, and change text to reflect "edit mode"
        if (monster != null) {
            try {
                traitArrayList.addAll(monster.getTraitList());
                actionArrayList.addAll(monster.getActionList());
                if (monster.getLegActionList() != null) {legendaryActionArrayList.addAll(monster.getLegActionList());}
            }
            catch (NullPointerException nex) {
                nex.printStackTrace();
            }
            fillFields();
            addButton.setText("Save Changes");
            frame.setTitle("Edit Monster");
        }


        //add a listener to each field so the "Enter" key can be pressed to save, instead of clicking the button at the bottom
        for (Component jc : editPanel.getComponents()) {
            jc.addKeyListener(new SaveKeyListener());
        }

        //add panels to the frame, pack it, set default size, show it
        frame.add(BorderLayout.WEST, editPanel);
        frame.add(BorderLayout.EAST, powerPanel);
        frame.add(BorderLayout.SOUTH, buttonPanel);
        frame.setSize(800, 600);
        frame.pack();
        frame.setLocationRelativeTo(parent);
        frame.setResizable(false);
        frame.setVisible(true);
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

    private void setMonsterScores() {
        monster.setScore(0, (Integer) strSpin.getValue()); //set Str
        monster.setScore(1, (Integer) dexSpin.getValue()); // set Dex
        monster.setScore(2, (Integer) conSpin.getValue()); // set Con
        monster.setScore(3, (Integer) intSpin.getValue()); // set Int
        monster.setScore(4, (Integer) wisSpin.getValue()); // set Wis
        monster.setScore(5, (Integer) chaSpin.getValue()); // set Cha
    }

    private void setMonsterHd() {
        int[] hdArray = new int[3];
        String diceValString = (String) hdTypeBox.getSelectedItem();
        hdArray[0] = (Integer) hdNumSpin.getValue();
        hdArray[1] = Integer.parseInt(diceValString.substring(1, diceValString.length()));
        hdArray[2] = (Integer) hdBonusSpin.getValue();
        monster.setHitDiceArray(hdArray);
    }

    private void fillFields() {
        nameField.setText(monster.getName());
        nameField.setCaretPosition(0);
        typeField.setText(monster.getType());
        typeField.setCaretPosition(0);
        hpSpin.setValue(monster.getHp());
        acSpin.setValue(monster.getAc());
        speedField.setText(monster.getSpeed());
        crSpin.setValue(monster.getChallenge());
        legNumSpin.setValue(monster.getLegActionCount());
        strSpin.setValue(monster.getScore(0));
        dexSpin.setValue(monster.getScore(1));
        conSpin.setValue(monster.getScore(2));
        intSpin.setValue(monster.getScore(3));
        wisSpin.setValue(monster.getScore(4));
        chaSpin.setValue(monster.getScore(5));
        savesField.setText(monster.getSaves());
        savesField.setCaretPosition(0);
        skillsField.setText(monster.getSkills());
        skillsField.setCaretPosition(0);
        vulnField.setText(monster.getVuln());
        vulnField.setCaretPosition(0);
        resistField.setText(monster.getResist());
        resistField.setCaretPosition(0);
        immuField.setText(monster.getImmu());
        immuField.setCaretPosition(0);
        conImmuField.setText(monster.getConImmu());
        langField.setText(monster.getLang());
        langField.setCaretPosition(0);
        senseField.setText(monster.getSense());
        senseField.setCaretPosition(0);
        hdNumSpin.setValue(monster.getHitDiceArray()[0]);
        hdBonusSpin.setValue(monster.getHitDiceArray()[2]);
        switch(monster.getHitDiceArray()[1]) {
            case 4: hdTypeBox.setSelectedIndex(0);
                    break;
            case 6: hdTypeBox.setSelectedIndex(1);
                    break;
            case 8: hdTypeBox.setSelectedIndex(2);
                    break;
            case 10: hdTypeBox.setSelectedIndex(3);
                    break;
            case 12: hdTypeBox.setSelectedIndex(4);
                    break;
        }
        updateTraitList();
        updateActionList();
        updateLegActionList();
    }

    private void deleteSelectedTrait() {
        if (!traitList.isSelectionEmpty()) {
            Object[] options = {"Delete", "Cancel"};
            int choice = JOptionPane.showOptionDialog(frame, "Permanently delete the selected trait?", "Confirm deletion",
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

    private void deleteSelectedAction() {
        if (!actionList.isSelectionEmpty()) {
            Object[] options = {"Delete", "Cancel"};
            int choice = JOptionPane.showOptionDialog(frame, "Permanently delete the selected action?", "Confirm deletion",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
            if (choice == 0) { // if the user selects "Yes" to deleting the selected action
                actionArrayList.remove(actionList.getSelectedValue());
                updateActionList();
            }
        }
    }

    private void updateActionList() {
        Collections.sort(actionArrayList);
        actionList.setListData(actionArrayList.toArray());
        actionList.revalidate();
    }

    private void updateLegActionList() {
        Collections.sort(legendaryActionArrayList);
    }

    private void saveData() {
        if (!nameField.getText().equals("")) {
            monster = new Monster();
            monster.setName(nameField.getText());
            monster.setHp((Integer) hpSpin.getValue());
            setMonsterHd();
            monster.setAc((Integer) acSpin.getValue());
            monster.setType(fieldCheck(typeField));
            monster.setSpeed(speedField.getText());
            monster.setChallenge((Double) crSpin.getValue());
            monster.setLegActionCount((Integer) legNumSpin.getValue());
            monster.setSkills(fieldCheck(skillsField));
            setMonsterScores();
            monster.setSaves(fieldCheck(savesField));
            monster.setVuln(fieldCheck(vulnField));
            monster.setResist(fieldCheck(resistField));
            monster.setImmu(fieldCheck(immuField));
            monster.setConImmu(fieldCheck(conImmuField));
            monster.setSense(fieldCheck(senseField));
            monster.setLang(fieldCheck(langField));
            monster.setTraitList(traitArrayList);
            monster.setActionList(actionArrayList);
            monster.setLegActionList(legendaryActionArrayList);
            frame.dispose();
        }
        else {
            JOptionPane.showMessageDialog(frame, "At a minimum, you must enter a name to save a monster.");
        }
    }

    private void newTraitWindow() {
        TraitEditWindow tew = new TraitEditWindow(frame);
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
        TraitEditWindow tew = new TraitEditWindow(frame, trait);
        tew.open();
        Trait tewTrait = tew.getTrait();
        if (tewTrait != null) {
            traitArrayList.remove(trait);
            traitArrayList.add(tewTrait);
        }
        updateTraitList();
    }

    private void editLegendaryWindow() {
        if (this.legendaryActionArrayList == null) this.legendaryActionArrayList = new ArrayList<LegendaryAction>();
        LegActionEditWindow lew = new LegActionEditWindow(frame, legendaryActionArrayList);
        lew.open();
        if (lew.getLegendaryActionArrayList() != null) {
            this.legendaryActionArrayList = lew.getLegendaryActionArrayList();
        }
        updateLegActionList();
    }

    private void newActionWindow() {
        ActionEditWindow aew = new ActionEditWindow(frame);
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
        ActionEditWindow aew = new ActionEditWindow(frame, action);
        aew.open();
        Action aewAction = aew.getAction();
        if (aewAction != null) {
            actionArrayList.remove(action);
            actionArrayList.add(aewAction);
        }
        updateActionList();
    }

    private void addNumberListener(JSpinner spin) {
        ((JSpinner.NumberEditor) spin.getEditor()).getTextField().addFocusListener(new NumberFocusListener());
    }
    /**
     * A method for retrieving the most recently edited monster from this dialog.
     * @return The monster edited or created in this window.
     */

    public Monster getMon() {
        return monster;
    }

    //all listeners past this point

    /**
     * Listener for the "Add" button. Takes filled in values, assigns them to the new monster, and adds it to the monster list.
     */

    class AddButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            saveData();
        }
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
        }
    }

    class SaveKeyListener implements KeyListener {
        public void keyPressed(KeyEvent kev) {
            int keyId = kev.getKeyCode();
            if (keyId == KeyEvent.VK_ENTER) {
                saveData();
            }
        }
        public void keyTyped(KeyEvent kev) {}
        public void keyReleased(KeyEvent kev) {}
    }

    class TraitListListener implements KeyListener {
        public void keyPressed(KeyEvent kev) {
            int keyId = kev.getKeyCode();
            if (keyId == KeyEvent.VK_DELETE) {
                deleteSelectedTrait();
            }
            if (keyId == KeyEvent.VK_ENTER && !traitList.isSelectionEmpty()) {
                editTraitWindow((Trait) traitList.getSelectedValue());
            }
        }
        public void keyTyped(KeyEvent kev) {}
        public void keyReleased(KeyEvent kev) {}
    }
}
