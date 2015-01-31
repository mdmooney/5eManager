import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Window in which actions are edited. Essentially a fillable form for defining all pertinent attributes of a new Action,
 * Creates a new Action when the "Save Data" button is pressed which can be used by anything calling this window (i.e.
 * a Monster for whom the Action is being created).
 * Created by Michael on 19/11/2014.
 */
public class ActionEditWindow extends PowerEditWindow  {

    private Action action;
    private JCheckBox atkCheck;
    private JSpinner atkBonus;
    private JCheckBox reactCheck;

    private String[] typeOptionsA = { "Melee", "Ranged", "Melee or Ranged"};
    private String[] typeOptionsB = {"Weapon", "Spell"};
    private JComboBox comboTypeA = new JComboBox();
    private JComboBox comboTypeB = new JComboBox();

    /**
     * Constructor for which no existing Action is supplied.
     * Sets the window title to "New Action" (because a brand-new action is being created).
     * @param parent The window that acts as a parent to this one. Mainly for modality purposes.
     */
    public ActionEditWindow(JDialog parent) {
        super(parent);
        windowTitle = "New Action";
        setPower(this.action);
    }

    /**
     * Constructor for which an existing Action is supplied ("Edit Mode").
     * Sets the window title to "Edit Action".
     * Then, sets the Window's Action to effectively be a copy of the Action to be edited.
     * @param parent The window that acts as a parent to this one. Mainly for modality purposes.
     * @param action The action to be edited.
     */
    public ActionEditWindow(JDialog parent, Action action) {
        super(parent);
        windowTitle = "Edit Action";
        if (action instanceof Attack) {
            this.action = new Attack();
            ((Attack) this.action).setBonus(((Attack) action).getBonus());
            ((Attack) this.action).setType(((Attack) action).getType());
        }
        else if (action instanceof Reaction) {
            this.action = new Reaction();
        }
        else this.action = new Action();
        this.setPower(this.action);
        this.getAction().setName(action.getName());
        this.getAction().setDescription(action.getDescription());
    }

    /**
     * Returns the Action edited/created by this window.
     * @return The Action edited/created by this window.
     */
    public Action getAction() {
        return this.action;
    }

    /**
     * Sets up the Swing GUI specific to the Action Edit Window.
     * Creates all text fields, spinners, boxes, etc.
     */
    protected void setupGui() {

        atkCheck = new JCheckBox();
        atkCheck.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                atkBonus.setEnabled(atkCheck.isSelected());
                comboTypeA.setEnabled(atkCheck.isSelected());
                comboTypeB.setEnabled(atkCheck.isSelected());
            }
        });

        atkCheck.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if(reactCheck.isSelected()) reactCheck.setSelected(false);
            }
        });

        reactCheck = new JCheckBox();
        reactCheck.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (atkCheck.isSelected()) atkCheck.setSelected(false);
            }
        });


        atkBonus = new JSpinner(new SpinnerNumberModel(0, 0, 99, 1));
        atkBonus.setEnabled(false);

        for (String str : typeOptionsA) {
            comboTypeA.addItem(str);
        }
        comboTypeA.setEnabled(false);

        for (String str : typeOptionsB) {
            comboTypeB.addItem(str);
        }
        comboTypeB.setEnabled(false);

        super.setupGui();
    }

    /**
     * Lays out the GUI specific to the Action Edit Window.
     * Basically just follows the layout of the generic Power Edit Window, but also includes code for properly filling fields
     * specific to Actions when the Window is editing an Action, rather than creating a new one.
     */
    protected void layoutGui() {
        super.layoutGui();

        if (this.action != null) {
            if (this.action instanceof Attack) {
                Attack atk = (Attack) this.action;
                atkCheck.setSelected(true);
                atkBonus.setValue(atk.getBonus());
                if (atk.getType().split(" ").length > 2) {
                    comboTypeA.setSelectedIndex(2);
                    comboTypeB.setSelectedItem(atk.getType().split(" ")[3]);
                }

                else {
                    comboTypeA.setSelectedItem(atk.getType().split(" ")[0]);
                    comboTypeB.setSelectedItem(atk.getType().split(" ")[1]);
                }
            }
            if (this.action instanceof Reaction) {
                reactCheck.setSelected(true);
            }
        }
    }

    /**
     * Returns the "Insert Panel," the JPanel placed in between Swing elements of the generic Power Edit Window.
     * In this case, this includes all gui elements specific to the Action Edit Window, including check boxes to determine
     * Attack, Reaction, or generic Action, and attack bonuses, combo boxes for Attack types, and a few small things (like labels).
     * @return The panel to be inserted, with GUI elements that are specific to the Action Edit Window.
     */
    protected JPanel getInsertPanel() {
        JPanel actPanel = new JPanel(new GridBagLayout());
        GridBagConstraints cb = new GridBagConstraints();
        cb.gridx=0;
        cb.anchor=GridBagConstraints.WEST;
        cb.gridy=0;
        cb.weightx=0;
        actPanel.add(new JLabel("Attack: "), cb);
        cb.gridy++;
        actPanel.add(new JLabel("Reaction: "), cb);
        cb.gridy--;
        cb.gridx++;
        actPanel.add(atkCheck, cb);
        cb.gridy++;
        actPanel.add(reactCheck, cb);
        cb.gridy--;
        cb.gridx++;
        cb.gridwidth = 2;
        actPanel.add(comboTypeA, cb);
        cb.gridx+=2;
        cb.gridwidth = 1;
        actPanel.add(comboTypeB, cb);
        cb.gridx++;
        actPanel.add(new JLabel(" + "), cb);
        cb.gridx++;
        cb.weightx=1;
        actPanel.add(atkBonus, cb);
        return actPanel;
    }

    /**
     * "Saves" the created/edited Action. Effectively this creates a new Action by checking what type it is (generic, Attack,
     * or Reaction), and creates an object of the appropriate type by passing the field data to the appropriate constructor.
     * After this is done, the dialog is disposed.
     * This saving is not permitted unless a name is provided for the nascent Action. If this is attempted, an error message is displayed in a dialog.
     */
    void saveData() {
        if (!name.getText().equals("")) {
            if (atkCheck.isSelected()) {
                this.action = new Attack(name.getText(), description.getText(), comboTypeA.getSelectedItem() + " " + comboTypeB.getSelectedItem(), (Integer) atkBonus.getValue());
            }
            else if (reactCheck.isSelected()) {
                this.action = new Reaction(name.getText(), description.getText());
            }
            else {
                this.action = new Action(name.getText(),description.getText());
            }
            dialog.dispose();
        }
        else JOptionPane.showMessageDialog(dialog, "At a minimum, you must enter a name to save an action.");
    }

    /**
     * Nullifies the power by setting the action and the generic power (usually a reference to the action) to null.
     */
    protected void nullPower() {
        this.action = null;
        this.power = null;
    }

}
