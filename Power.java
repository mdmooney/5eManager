import javax.xml.bind.annotation.XmlType;

/**
 * Class for "powers," which include virtually any sort of action or ability a Participant can have that would
 * distinguish it from other Participants. Powers include things like Attacks, Traits, etc. which are covered by
 * subclasses of the Power class.
 *
 * Created by Michael on 13/11/2014.
 */

@XmlType(propOrder = {"name","description"})

public class Power implements Comparable<Power> {
    private String name;
    private String description;

    /**
     * Default constructor. Sets no values for name or description.
     */
    public Power() {
    }

    /**
     * Creates a generic power with a name and a description of some sort. All text is stripped of HTML tags.
     * @param name String for the power's name.
     * @param desc String for the power's description.
     */
    public Power(String name, String desc) {
        this.name = TextFormat.pruneText(name);
        this.description = TextFormat.pruneText(desc);
    }

    /**
     * Override to provide comparison between two powers. Provides for some of the subclasses (Action and Attack) and
     * defaults to comparison by name if it's some other type not covered here.
     * For Actions, Actions with the name "Multiattack" are given first priority (as in the 5e Monster Manual). Then,
     * Attacks are given priority, followed by non-Attack actions. Any equal types are just sorted by names.
     * @param pow The power to be compared.
     * @return A positive, negative, or 0 integer for greater, lower, or equal comparison, respectively.
     */
    @Override
    public int compareTo(Power pow) {
        if (this instanceof Action) {
            if (this.name.contains("Multiattack")) return -1;
            else if (pow.getName().contains("Multiattack")) return 1;
            if (this instanceof Attack && !(pow instanceof Attack)) {return -1;}
            else if (pow instanceof Attack && !(this instanceof Attack)) {return 1;}
        }
        if (this instanceof Attack) {
            if (!((Attack) this).getType().equals(((Attack) pow).getType())) {
                return ((Attack) this).getType().compareToIgnoreCase(((Attack) pow).getType());
            }
        }
        return name.compareToIgnoreCase(pow.getName());
    }

    /**
     * Returns the Power's name, stripped of all HTML tags.
     * @return The Power's name, stripped of all HTML tags.
     */
    public String getName() {
        return TextFormat.pruneText(name);
    }

    /**
     * Sets the Power's name. Strips all HTML tags before setting.
     * @param name The new name to use for the Power.
     */
    public void setName(String name) {
        this.name = TextFormat.pruneText(name);
    }

    /**
     * Retrieves the Power's description string, stripped of all HTML tags.
     * @return The Power's description string, stripped of all HTML tags.
     */
    public String getDescription() {
        return TextFormat.pruneText(description);
    }

    /**
     * Sets the Power's description. Strips all HTML tags before setting.
     * @param description The new description to use for the Power.
     */
    public void setDescription(String description) {
        this.description = TextFormat.pruneText(description);
    }

    /**
     * Method for displaying the Power as a String. Simply returns the name.
     * @return The name of the Power.
     */
    @Override
    public String toString() {
        return this.getName();
    }

}
