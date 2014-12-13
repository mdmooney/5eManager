import javax.swing.*;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;

@XmlRootElement(name="pcblock")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"name", "classes", "ac", "hp", "speed", "level", "scores", "saves", "skillMods", "profs", "lang", "notes", "traitList", "actionList", "spellRefs"})
@XmlSeeAlso({Action.class, Attack.class, Reaction.class, SpellRef.class})

public class Player implements Fightable, LibraryMember {
    private String name;
    private String classes;
    private int[] saves = new int[6];
    private String profs;
    private String lang;
    private ArrayList<Trait> traitList;
    private ArrayList<Action> actionList;
    private ArrayList<SpellRef> spellRefs;
    private int hp;
    private int ac;
    private int[] scores = new int[6];
    private int[] skillMods = new int[18];
    private String speed;
    private String notes;
    private int level;

    public JPanel getBlockPanel() {
        return new JPanel();
    } //todo: actually make this do something useful

    public int getHp() {
        return 1;
    }

    public String getName() {
        return pruneText(name);
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setClasses(String classes) {
        this.classes = classes;
    }

    public void setSaves(int[] saves) {
        this.saves = saves;
    }

    public void setProfs(String profs) {
        this.profs = profs;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public void setTraitList(ArrayList<Trait> traitList) {
        this.traitList = traitList;
    }

    public void setActionList(ArrayList<Action> actionList) {
        this.actionList = actionList;
    }

    public void setSpellRefs(ArrayList<SpellRef> spellRefs) {
        this.spellRefs = spellRefs;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public void setAc(int ac) {
        this.ac = ac;
    }

    public void setScores(int[] scores) {
        this.scores = scores;
    }

    public void setScore(int pos, int score) {
        this.scores[pos] = score;
    }

    public void setSkillMods(int[] skillMods) {
        this.skillMods = skillMods;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    protected String pruneText(String entryString) {
        return TextFormat.pruneText(entryString);
    }
}
