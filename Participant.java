import javax.swing.*;
import java.util.Random;

/**
 * Class for participants in an encounter (i.e. the combatants). This includes players and monsters. It exists because there
 * is no need for every instance variable of monster or player objects to be editable in combat, or really any at all; all that
 * needs to be tracked in combat is current HP, initiative, and current conditions. These are handled the same for players and
 * monsters. To display the appropriate statblock as needed, Participant objects keep references to the monster or player they
 * are meant to represent, but do not manipulate these objects.
 */

public class Participant implements Comparable<Participant> {
    private int currentHp;
    private int maxHp;
    private int initiative;
    private Fightable fightable;
    private String name;
    private String notes;

    public Participant(Fightable fightable) {
        this.fightable = fightable;
        this.currentHp = fightable.getHp();
        this.maxHp = fightable.getHp();
        this.name = fightable.getName();
    }

    public int compareTo(Participant p) {
        return this.name.compareToIgnoreCase(p.getName());
    }

    public String toString() {
        return this.getName();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getBaseName() {return "" + fightable.getName();}

    public JPanel getBlock() {
        return fightable.getBlockPanel();
    }

    public void getRandomHp() {
        if (fightable.getClass() == Monster.class) {
            this.maxHp = ((Monster) fightable).getRandomHp();
            this.currentHp = this.maxHp;
        }
    }

    public int rollInitiative() {
        return new Random().nextInt(19) + 1 + (fightable.getInitiative());
    }

    public int getInitiative() {
        return initiative;
    }

    public void setInitiative(int initiative) {
        this.initiative = initiative;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public int getMaxHp() {
        return maxHp;
    }
    public void setCurrentHp(int currentHp) {
        this.currentHp = currentHp;
    }

    public java.lang.Class getFightableClass() {
        return this.fightable.getClass();
    }
}
