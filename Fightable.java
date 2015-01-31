/**
 * Interface for anything that can be put into a combat. For now, this includes players and monsters, but may be expanded later
 * to include things like traps and hazards. It includes methods needed for the Participant class to facilitate
 * combat handling for anything that should be in combat.
 */
public interface Fightable extends LibraryMember {

    /**
     * Returns a number for the maximum HP of the Fightable object.
     * @return The maximum HP of the Fightable object.
     */
    public abstract int getHp();

    /**
     * Returns the Fightable object's armour class, or AC.
     * @return The Fightable object's armour class, or AC.
     */
    public abstract int getAc();

    /**
     * Returns the Fightable object's initiative modifier, which is its bonus to rolls made to determine initiative
     * in combat.
     * @return The Fightable object's initiative modifer.
     */
    public abstract int getInitiative();
}
