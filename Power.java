import javax.xml.bind.annotation.XmlType;

/**
 * Class for "powers," which include virtually any sort of action or ability a Participant can have that would
 * distinguish it from other Participants. Powers include things like Attacks, Traits, etc. which are covered by
 * subclasses of the Power class.
 *
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
        this.name = pruneText(name);
        this.description = pruneText(desc);
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
        return pruneText(name);
    }

    public void setName(String name) {
        this.name = pruneText(name);
    }

    public String getDescription() {
        return pruneText(description);
    }

    public void setDescription(String description) {
        this.description = pruneText(description);
    }

    public String toString() {
        return this.getName();
    }

    /** Cleans simple HTML tags from a string.
     *  Used to ensure the user doesn't break everything by using HTML formatting, as without this, virtually any HTML would function just fine.
     *  @param entryString The String to be pruned.
     *  @return The String without HTML tags.
     */

    protected String pruneText(String entryString) {
        return TextFormat.pruneText(entryString);
    }
}
