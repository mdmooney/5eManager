import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Michael on 27/11/2014.
 */

@XmlRootElement(name="legendaryAction")
public class LegendaryAction extends Action {
    private int cost;

    public LegendaryAction(String name, String desc) {
        super(name, desc);
        this.cost = 1;
    }

    public LegendaryAction(String name, String desc, int cost) {
        super(name, desc);
        this.cost = cost;
    }

    @XmlElement
    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public LegendaryAction() {}
}
