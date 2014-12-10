import javax.swing.*;

/**
 * A label for ability modifiers.
 */
class ModifierLabel extends JLabel {
    public ModifierLabel() {
        super();
        this.setText("(0)"); //constructor defaults to 0, because all ability scores default to 10
    }
    /**
     * Updates the ability modifier label text based on input from an event that should change the modifier's value.
     * @param newMod The new modifier to update the label with.
     */
    public void updateLabel(int newMod) {
        this.setText(newMod>0? "(+" + newMod + ")" : "(" + newMod + ")"); //check if there should be a + sign before the modifier and assign the appropriate text
    }
}