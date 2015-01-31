import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class for Reactions, a special type of Action almost always performed out-of-turn and always in response to a particular
 * trigger. This is functionally the same as an Action (contains no unique attributes of its own), but the distinction is
 * necessary so that the Block Panel can display Reactions appropriately, to make it more clear to the user what is a normal
 * Action and what is a Reaction.
 *
 * Created by Michael on 30/11/2014.
 */
@XmlRootElement(name="reaction")

public class Reaction extends Action {
    /**
     * Default constructor. Sets no attributes.
     */
    public Reaction() {}

    /**
     * Same as the constructor for its parent class (Action). Sets name and description for new Reaction object.
     * @param name The name of the Reaction.
     * @param desc The description of the Reaction.
     */
    public Reaction(String name, String desc) {
        super(name, desc);
    }

}
