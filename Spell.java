/**
 * Class for spells, including all details covered for spells in the PHB.
 */
public class Spell extends Power {
    //private String name;
    private String school;
    private String castTime;
    private String range;
    private String components;
    private String duration;
    private int level;
    //private String description;

    public Spell() {}

    public Spell(String name, String desc) {
        super(name, desc);
    }
}
