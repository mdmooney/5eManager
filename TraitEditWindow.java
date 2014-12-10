/**
 * Created by Michael on 19/11/2014.
 */

import javax.swing.*;

public class TraitEditWindow extends PowerEditWindow {
    Trait trait;

    public TraitEditWindow(JDialog parent) {
        super(parent);
        windowTitle = "New Trait";
        //this.trait = new Trait();
        setPower(this.trait);
    }

    public TraitEditWindow(JDialog parent, Trait trait) {
        super(parent);
        windowTitle = "Edit Trait";
        this.trait = new Trait();
        this.setPower(trait);
        this.getTrait().setName(trait.getName());
        this.getTrait().setDescription(trait.getDescription());
    }

    public Trait getTrait() {
        return trait;
    }

    void saveData() {
        if (!name.getText().equals("")) {
            this.trait = new Trait(name.getText(), description.getText());
            dialog.dispose();
        }
        else JOptionPane.showMessageDialog(dialog, "At a minimum, you must enter a name to save a trait.");
    }

    protected void nullPower() {
        this.trait = null;
        this.power = null;
    }
}
