import javax.swing.*;
import java.awt.*;

/**
 * Created by Michael on 09/12/2014.
 */
public class SpellEditWindow extends PowerEditWindow {
    private Spell spell;
    private JSpinner levelSpin = new JSpinner(new SpinnerNumberModel(0, 0, 9, 1));
    private JTextField schoolField = new JTextField(10);
    private JTextField castTimeField = new JTextField(10);
    private JTextField rangeField = new JTextField(10);
    private JTextField componentsField = new JTextField(13);
    private JTextField durationField = new JTextField(10);
    private JComboBox schoolBox = new JComboBox(ManagerConstants.SPELL_SCHOOLS);

    public SpellEditWindow(JDialog parent) {
        super(parent);
        windowTitle = "New Spell";
        setPower(this.spell);
    }

    public SpellEditWindow(JDialog parent, Spell spell) {
        super(parent);
        windowTitle = "Edit Spell";
        this.spell = new Spell();
        this.setPower(spell);
        this.spell.setName(spell.getName());
        this.spell.setDescription(spell.getDescription());
        this.spell.setLevel(spell.getLevel());

        this.spell.setCastTime(spell.getCastTime());
        this.spell.setComponents(spell.getComponents());
        this.spell.setDuration(spell.getDuration());
        this.spell.setRange(spell.getRange());
        this.spell.setSchool(spell.getSchool());
    }

    public Spell getSpell() {
        return spell;
    }

    protected void layoutGui() {
        super.layoutGui();
        //if there's a spell reference during setup (i.e. if we're in "edit mode"), pull its stats and display them, and change text to reflect "edit mode"
        if (spell != null) {
            fillFields();
            saveButton.setText("Save Changes");
            dialog.setTitle("Edit Spell");
        }
    }

    protected JPanel getInsertPanel() {
        GridBagConstraints cb = new GridBagConstraints();
        JPanel spellPanel = new JPanel(new GridBagLayout());
        cb.gridx=0;
        cb.anchor=GridBagConstraints.WEST;
        cb.gridy=0;
        cb.weightx=0;
        spellPanel.add(new JLabel("Level: "), cb);
        cb.gridx++;
        spellPanel.add(levelSpin, cb);
        ((JSpinner.NumberEditor) levelSpin.getEditor()).getTextField().addFocusListener(new NumberFocusListener());
        cb.gridx--;
        cb.gridy++;
        spellPanel.add(new JLabel("School: "), cb);
        cb.gridx++;
        //spellPanel.add(schoolField, cb);
        spellPanel.add(schoolBox, cb);
        cb.gridx--;
        cb.gridy++;
        cb.weightx=0;
        spellPanel.add(new JLabel("Cast Time: "),cb);
        cb.gridx++;
        spellPanel.add(castTimeField, cb);
        cb.gridx--;
        cb.gridy++;
        spellPanel.add(new JLabel("Range: "),cb);
        cb.gridx++;
        spellPanel.add(rangeField, cb);
        cb.gridx--;
        cb.gridy++;
        spellPanel.add(new JLabel("Components: "), cb);
        cb.gridx++;
        spellPanel.add(componentsField, cb);
        cb.gridx--;
        cb.gridy++;
        spellPanel.add(new JLabel("Duration: "),cb);
        cb.gridx++;
        cb.weightx=1;
        spellPanel.add(durationField, cb);
        cb.gridx--;
        return spellPanel;
    }

    void saveData() {
        if (!name.getText().equals("")) {
            this.spell = new Spell(name.getText(), description.getText(), (String) schoolBox.getSelectedItem(), castTimeField.getText(), rangeField.getText(),
                   componentsField.getText(), durationField.getText(), (Integer) levelSpin.getValue());
            dialog.dispose();
        }
        else JOptionPane.showMessageDialog(dialog, "At a minimum, you must enter a name to save a spell.");
    }

    protected void nullPower() {
        this.spell = null;
        this.power = null;
    }

    private void fillFields() {
        levelSpin.setValue(spell.getLevel());
        name.setText(spell.getName());
        description.setText(spell.getDescription());
        //schoolField.setText(spell.getSchool());
        schoolBox.setSelectedItem(spell.getSchool());
        castTimeField.setText(spell.getCastTime());
        rangeField.setText(spell.getRange());
        componentsField.setText(spell.getComponents());
        durationField.setText(spell.getDuration());
    }
}
