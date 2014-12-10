/**
 * Created by Michael on 13/11/2014.
 */

import javax.xml.bind.annotation.*;
import java.util.Collections;

@XmlRootElement(name="action")
public class Action extends Power  {

    public Action() {}

    public Action(String name, String desc) {
        super(name, desc);
    }

}
