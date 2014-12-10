/**
 * Created by Michael on 20/11/2014.
 */

import javax.xml.bind.annotation.*;

@XmlRootElement(name="attack")
public class Attack extends Action {
    private String type;
    private int bonus;

    @XmlElement
    public int getBonus() {
        return bonus;
    }

    @XmlElement
    public String getType() {
        return type;
    }

    public void setBonus(int bonus) {
        this.bonus = bonus;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Attack() {}

    public Attack(String name, String desc) {
        super(name, desc);
        this.type = "Melee Weapon";
        this.bonus = 0;
    }

    public Attack(String name, String desc, String type, int bonus) {
        super(name, desc);
        this.type = type;
        this.bonus = bonus;
    }
}
