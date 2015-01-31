/**
 * Class for Powers of the "Attack" type, to be used primarily (solely?) for Monster attacks.
 *
 * In addition to attributes inherited from the Power class, it includes attack type (defining whether the attack is
 * melee, ranged, or both, and whether it is a weapon or spell attack) and an attack bonus.
 * Created by Michael on 20/11/2014.
 */

import javax.xml.bind.annotation.*;

@XmlRootElement(name="attack")
public class Attack extends Action {
    private String type;
    private int bonus;

    /**
     * Default constructor. Sets no values for any attributes.
     */
    public Attack() {}

    /**
     * Constructor that does not account for any attributes specific to the Attack class (only those found in Power).
     * Uses super constructor to begin with, and sets default values for Attack-specific attributes (type is set to "Melee Weapon"
     * and attack bonus is set to 0)
     * @param name The name of the Attack.
     * @param desc The description of the Attack.
     */
    public Attack(String name, String desc) {
        super(name, desc);
        this.type = "Melee Weapon";
        this.bonus = 0;
    }

    /**
     * Full constructor, setting all Attack-specific attributes.
     * Still calls the super constructor for name and description but also sets specific values for the attack type and the
     * attack bonus.
     * @param name The name of the Attack.
     * @param desc The description of the Attack.
     * @param type The attack's type, in ordinary circumstances chosen from "Melee/Ranged/Melee or Ranged" and "Weapon/Spell" for possible options.
     * @param bonus The attack bonus (i.e. the bonus to a d20 roll made to determine hit)
     */
    public Attack(String name, String desc, String type, int bonus) {
        super(name, desc);
        this.type = type;
        this.bonus = bonus;
    }

    /**
     * Gets the attack bonus.
     * @return The attack bonus (i.e. the bonus to a d20 roll made to determine hit)
     */
    @XmlElement
    public int getBonus() {
        return bonus;
    }

    /**
     * Gets the type of the Attack.
     * @return The attack's type, in ordinary circumstances chosen from "Melee/Ranged/Melee or Ranged" and "Weapon/Spell" for possible options.
     */
    @XmlElement
    public String getType() {
        return TextFormat.pruneText(type);
    }

    /**
     * Sets the attack bonus, which is how much a roll to attack (d20) is modified.
     * @param bonus The attack bonus (i.e. the bonus to a d20 roll made to determine hit)
     */
    public void setBonus(int bonus) {
        this.bonus = bonus;
    }

    /**
     * Sets the type of attack, determining its range (from melee, ranged, or both) and how it is performed (by weapon or spell).
     * Weapons include natural weapons, like claws and fists.
     * @param type The attack's type, in ordinary circumstances chosen from "Melee/Ranged/Melee or Ranged" and "Weapon/Spell" for possible options.
     */
    public void setType(String type) {
        this.type = TextFormat.pruneText(type);
    }

}
