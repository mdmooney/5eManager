import javax.xml.bind.annotation.*;

/**
 * Class for spells, including all details covered for spells in the PHB.
 */

@XmlRootElement(name="spellblock")
@XmlSeeAlso(Power.class)
@XmlAccessorType(XmlAccessType.FIELD)
//@XmlType(propOrder = {"name", "level", "school", "castTime", "range", "components", "duration", "description"})

public class Spell extends Power implements LibraryMember {
    private String school;
    private String castTime;
    private String range;
    private String components;
    private String duration;
    private int level;

    public Spell() {}

    public Spell(String name, String desc) {
        super(name, desc);
    }
    public Spell(String name, String desc, String school, String castTime, String range, String components, String duration, int level) {
        super(name, desc);
        this.school = school;
        this.castTime = castTime;
        this.range = range;
        this.components = components;
        this.duration = duration;
        this.level = level;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getCastTime() {
        return castTime;
    }

    public void setCastTime(String castTime) {
        this.castTime = castTime;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public String getComponents() {
        return components;
    }

    public void setComponents(String components) {
        this.components = components;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
