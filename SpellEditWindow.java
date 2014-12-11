import javax.swing.*;
import java.awt.*;

/**
 * Created by Michael on 09/12/2014.
 */
public class SpellEditWindow extends PowerEditWindow {
    private Spell spell;
    private JTextField schoolField = new JTextField(10);

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
        this.getSpell().setName(spell.getName());
        this.getSpell().setDescription(spell.getDescription());
    }

    public Spell getSpell() {
        return spell;
    }

    protected void layoutGui() {
        super.layoutGui();
    }

    protected JPanel getInsertPanel() {
        GridBagConstraints cb = new GridBagConstraints();
        JPanel spellPanel = new JPanel(new GridBagLayout());
        cb.gridx=0;
        cb.anchor=GridBagConstraints.WEST;
        cb.gridy=0;
        cb.weightx=0;
        spellPanel.add(new JLabel("School: "), cb);
        cb.gridx++;
        cb.weightx=1;
        spellPanel.add(schoolField, cb);
        cb.gridx--;
        cb.gridy++;
        cb.weightx=0;
        spellPanel.add(new JLabel("Cast Time: "),cb);
        cb.gridy++;
        spellPanel.add(new JLabel("Range: "),cb);
        cb.gridy++;
        spellPanel.add(new JLabel("Components: "), cb);
        cb.gridy++;
        spellPanel.add(new JLabel("Duration: "),cb);
        return spellPanel;
    }

    void saveData() {
        if (!name.getText().equals("")) {
            this.spell = new Spell(name.getText(), description.getText());
            dialog.dispose();
        }
        else JOptionPane.showMessageDialog(dialog, "At a minimum, you must enter a name to save a trait.");
    }

    protected void nullPower() {
        this.spell = null;
        this.power = null;
    }
}
