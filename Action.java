/**
 * Class for Powers of the "Action" type, which exists mainly just to ensure that there is a distinction made
 * between actions and non-actions. Inherits virtually everything from the Power class.
 *
 * Created by Michael on 13/11/2014.
 */

import javax.xml.bind.annotation.*;
import java.util.Collections;

@XmlRootElement(name="action")
public class Action extends Power  {

    /**
     * Default constructor sets no values for attributes.
     */
    public Action() {}

    /**
     * Same as the equivalent constructor for the Power class; in fact, this just calls the super constructor method.
     * @param name Name of the Action
     * @param desc Description of the Action
     */
    public Action(String name, String desc) {
        super(name, desc);
    }

}
