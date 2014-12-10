import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Michael on 30/11/2014.
 */
@XmlRootElement(name="reaction")

public class Reaction extends Action {
    public Reaction() {}

    public Reaction(String name, String desc) {
        super(name, desc);
    }

}
