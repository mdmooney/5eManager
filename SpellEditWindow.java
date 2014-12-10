import javax.swing.*;

/**
 * Created by Michael on 09/12/2014.
 */
public class SpellEditWindow extends PowerEditWindow {
    private Spell spell;

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
