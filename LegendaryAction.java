import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Legendary Actions are a unique type of Action used only by particularly powerful creatures, such as dragons or liches.
 * They are used outside of normal initiative order.
 * They are distinct from normal Actions in that, because they can be used outside of the Participant's initiative, they
 * are associated with a cost. A monster that can use legendary actions has a certain allotment of "points" to be spent on
 * them per round, and each one they can use has an associated cost.
 */

@XmlRootElement(name="legendaryAction")
public class LegendaryAction extends Action {
    private int cost;

    /**
     * Constructor that creates a Legendary Action but does not define a cost.
     * Defaults the cost of any action to 1 (by far the most common).
     * @param name The name of the Legendary Action.
     * @param desc The description of the Legendary Action.
     */
    public LegendaryAction(String name, String desc) {
        super(name, desc);
        this.cost = 1;
    }

    /**
     * Full constructor for the Legendary Action, setting all parameters.
     * @param name The name of the Legendary Action.
     * @param desc The description of the Legendary Action.
     * @param cost The cost of the Legendary Action in terms of how many "points" it takes from the monster using it.
     */
    public LegendaryAction(String name, String desc, int cost) {
        super(name, desc);
        this.cost = cost;
    }

    /**
     * Returns the cost of the Legendary Action.
     * @return The cost, in terms of how many "points" it takes from the monster using it.
     */
    @XmlElement
    public int getCost() {
        return cost;
    }

    /**
     * Sets the cost of the Legendary Action.
     * @param cost The cost of the Legendary Action in terms of how many "points" it takes from the monster using it.
     */
    public void setCost(int cost) {
        this.cost = cost;
    }

}
