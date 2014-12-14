import javax.swing.*;

/**
 * Interface for anything that can be put into a combat. For now, this includes players and monsters, but may be expanded later
 * to include things like traps and hazards, thus the interface. It includes methods needed for the Participant class to facilitate
 * combat handling for anything that should be in combat.
 */
public interface Fightable extends LibraryMember {

    public abstract int getHp();

    public abstract int getScore(int position);

    public abstract int getInitiative();
}
