import javax.swing.*;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;

@XmlRootElement(name="pcblock")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"name", "race", "classes", "background", "alignment", "ac", "hp", "speed", "initiative", "level", "proficiency", "scores", "saves", "skillMods", "profs", "lang", "notes", "traitList", "actionList", "spellRefs"})
@XmlSeeAlso({Action.class, Attack.class, Reaction.class, SpellRef.class})

public class Player implements Fightable, LibraryMember {
    private String name;
    private String classes;
    private String race;
    private String background;
    private String alignment;
    private int[] saves = new int[6];
    private String profs;
    private String lang;
    private ArrayList<Trait> traitList;
    private ArrayList<Action> actionList;
    private ArrayList<SpellRef> spellRefs;
    private int hp;
    private int ac;
    private int proficiency;
    private int[] scores = new int[6];
    private int[] skillMods = new int[18];
    private int initiative;
    private String speed;
    private String notes;
    private int level;

    public JPanel getBlockPanel() {
        return new JPanel();
    } //todo: actually make this do something useful

    public int getHp() {
        return hp;
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

    public int getScore(int pos) {
        return scores[pos];
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

    public void setInitiative(int initiative) {
        this.initiative = initiative;
    }

    public String getClasses() {
        return classes;
    }

    public int[] getSaves() {
        return saves;
    }

    public String getProfs() {
        return profs;
    }

    public String getLang() {
        return lang;
    }

    public ArrayList<Trait> getTraitList() {
        return traitList;
    }

    public ArrayList<Action> getActionList() {
        return actionList;
    }

    public ArrayList<SpellRef> getSpellRefs() {
        return spellRefs;
    }

    public int getAc() {
        return ac;
    }

    public int[] getScores() {
        return scores;
    }

    public int[] getSkillMods() {
        return skillMods;
    }

    public String getSpeed() {
        return speed;
    }

    public String getNotes() {
        return notes;
    }

    public int getLevel() {
        return level;
    }

    public String getBackground() {
        return background;
    }

    public String getRace() {
        return race;
    }

    public String getAlignment() {
        return alignment;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public void setAlignment(String alignment) {
        this.alignment = alignment;
    }

    public int getInitiative() {
        return initiative;
    }


    public int getProficiency() {
        return proficiency;
    }

    public void setProficiency(int proficiency) {
        this.proficiency = proficiency;
    }


    public String toString() {
        return this.name;
    }

    protected String pruneText(String entryString) {
        return TextFormat.pruneText(entryString);
    }
}
