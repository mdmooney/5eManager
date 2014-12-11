import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by Michael on 13/11/2014.
 */

@XmlType(propOrder = {"name","description"})
//@XmlAccessorType(XmlAccessType.FIELD)

public class Power implements Comparable<Power> {
    private String name;
    private String description;

    public Power() {
    }

    public Power(String name, String desc) {
        this.name = name;
        this.description = desc;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String toString() {
        return this.getName();
    }
}
