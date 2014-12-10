import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
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

    public ActionEditWindow(JDialog parent) {
        super(parent);
        windowTitle = "New Action";
        setPower(this.action);
    }

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

    public Action getAction() {
        return this.action;
    }

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

    protected void layoutGui() {
        super.layoutGui();
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
        c.gridy=1;
        c.gridx=0;
        c.fill=GridBagConstraints.HORIZONTAL;
        c.gridwidth=GridBagConstraints.REMAINDER;
        editPanel.add(actPanel, c);

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

    void saveData() {
        if (!name.getText().equals("")) {
            if (atkCheck.isSelected()) {
                this.action = new Attack(name.getText(), description.getText(), comboTypeA.getSelectedItem() + " " + comboTypeB.getSelectedItem(), (Integer) atkBonus.getValue());
            }
            else if (reactCheck.isSelected()) {
                this.action = new Reaction(name.getText(), description.getText());
            }
            else {
                this.action = new Action(name.getText(), description.getText());
            }
            dialog.dispose();
        }
        else JOptionPane.showMessageDialog(dialog, "At a minimum, you must enter a name to save an action.");
    }

    protected void nullPower() {
        this.action = null;
        this.power = null;
    }

}
