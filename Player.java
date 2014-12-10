import javax.swing.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;

@XmlRootElement(name="pcblock")
@XmlType(propOrder = {"name", "classes", "ac", "hp", "speed", "level", "scores", "saves", "skills", "profs", "lang", "notes", "traitList", "actionList", "spellRefs"})
@XmlSeeAlso({Action.class, Attack.class, Reaction.class, SpellRef.class})

public class Player implements Fightable {
    private String name;
    private String classes;
    private String saves;
    private String skills;
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
    }

    public int getHp() {
        return 1;
    }
}
