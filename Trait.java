/**
 * Traits are a particular type of Power that are not (normally) used dynamically in battle, but instead describe a
 * sort of "always-on" effect, usually some sort of unique but passive ability. Though this is not practically different
 * from the generic Power (no unique attributes of its own), the distinction is necessary so that traits are properly
 * displayed in the information block panels.
 * Created by Michael on 13/11/2014.
 */

public class Trait extends Power {

    /**
     * Default constructor. Sets no values for any attribute.
     */
    public Trait() {
    }

    /**
     * Constructor identical to the constructor with the same parameters of the Power class.
     * @param name The name of the Trait.
     * @param desc The description of the Trait.
     */
    public Trait(String name, String desc) {
        super(name, desc);
    }

}
