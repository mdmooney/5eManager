import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.xml.bind.annotation.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Michael on 03/11/2014.
 */

@XmlRootElement(name="statblock")
@XmlType(propOrder = {"name", "type", "ac", "hp", "hitDiceArray", "speed", "challenge", "legActionCount", "scores", "saves", "skills", "vuln", "resist", "immu", "conImmu", "sense", "lang", "traitList", "actionList", "legActionList"})
@XmlSeeAlso({Action.class, Attack.class, Reaction.class})

public class Monster implements Comparable<Monster>, Fightable, LibraryMember {
    private String name;
    private String type;
    private String saves;
    private String skills;
    private String vuln;
    private String resist;
    private String immu;
    private String conImmu;
    private String sense;
    private String lang;
    private ArrayList<Trait> traitList;
    private ArrayList<Action> actionList;
    private ArrayList<LegendaryAction> legActionList;
    private int hp;
    private int ac;
    private int[] hitDiceArray = new int[3];
    private int[] scores = new int[6];
    private String speed;
    private double challenge;
    private int legActionCount;

    @XmlElementWrapper(name = "traitList")
    @XmlElement(name = "trait")
    public ArrayList<Trait> getTraitList() {
        return traitList;
    }

    public void addTrait(Trait trait) {
        traitList.add(trait);
    }

    public void removeTrait(Trait trait) {
        traitList.remove(trait);
    }

    public void setTraitList(ArrayList<Trait> traitList) {
        this.traitList = traitList;
    }

    public void addAction(Action action) {
        actionList.add(action);
    }

    public void removeAction(Action action) {
        actionList.remove(action);
    }

    @XmlElementWrapper(name = "actionList")
    @XmlAnyElement(lax=true)
    public ArrayList<Action> getActionList() {
        return actionList;
    }

    public void setActionList(ArrayList<Action> actionList) {this.actionList = actionList;}

    @XmlElementWrapper(name = "legActionList")
    @XmlElement(name = "legendaryAction")
    public ArrayList<LegendaryAction> getLegActionList() {
        return legActionList;
    }

    public void setLegActionList(ArrayList<LegendaryAction> legActionList) {
        this.legActionList = legActionList;
    }

    public Monster() {
        this.name = "Nameless";
        this.hp = 10;
        this.ac = 10;
    }

    public Monster(String name, int hp, int ac) {
        this.name = name;
        this.hp = hp;
        this.ac = ac;
    }

    public int getAc() {
        return ac;
    }

    public void setAc(int ac) {this.ac = ac;}

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) { this.hp = hp;}

    public String getName() {
        return TextFormat.pruneText(name);
    }

    public void setName(String name) { this.name = TextFormat.pruneText(name);}

    public String getType() { return TextFormat.pruneText(type);
    }

    public void setType(String type) { this.type = TextFormat.pruneText(type);
    }

    public String getSaves() {
        return TextFormat.pruneText(saves);
    }

    public void setSaves(String saves) {this.saves = TextFormat.pruneText(saves);    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = TextFormat.pruneText(skills);
    }

    public String getVuln() {
        return TextFormat.pruneText(vuln);
    }

    public void setVuln(String vuln) {
        this.vuln = TextFormat.pruneText(vuln);
    }

    public String getResist() {
        return TextFormat.pruneText(resist);
    }

    public void setResist(String resist) {
        this.resist = TextFormat.pruneText(resist);
    }

    public String getImmu() {
        return TextFormat.pruneText(immu);
    }

    public void setImmu(String immu) {
        this.immu = TextFormat.pruneText(immu);
    }

    public String getConImmu() {
        return TextFormat.pruneText(conImmu);
    }

    public void setConImmu(String conImmu) { this.conImmu = TextFormat.pruneText(conImmu); }

    public String getSense() {
        return TextFormat.pruneText(sense);
    }

    public void setSense(String sense) {
        this.sense = TextFormat.pruneText(sense);
    }

    public String getLang() {
        return TextFormat.pruneText(lang);
    }

    public void setLang(String lang) {
        this.lang = TextFormat.pruneText(lang);
    }

    public void setScore(int i, int newVal) {
        this.scores[i] = newVal;
    }

    public String getSpeed() {
        return TextFormat.pruneText(speed);
    }

    public void setSpeed(String speed) {
        this.speed = TextFormat.pruneText(speed);
    }

    public void setScores(int[] scores) {
        this.scores = scores;
    }

    public int[] getScores() {
        return scores;
    }

    public int[] getHitDiceArray() {
        return hitDiceArray;
    }

    public void setHitDiceArray(int[] hitDiceArray) {
        this.hitDiceArray = hitDiceArray;
    }

    public double getChallenge() {
        return challenge;
    }

    public int getLegActionCount() {
        return legActionCount;
    }

    public void setLegActionCount(int legActionCount) {
        this.legActionCount = legActionCount;
    }

    public void setChallenge(double challenge) {
        this.challenge = challenge;
    }

    public int getScore(int index) {return scores[index];}

    public String[] getMods() {
        String[] mods = new String[6];
        for (int i = 0; i < mods.length; i++) {
            int mod = scores[i] /2 - 5;
            mods[i] = mod > 0 ? "+" + mod : "" + mod;
        }
        return mods;
    }

    public String toString() {
        return this.getName();
    }

    public JPanel getBlockPanel() {
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JTextPane textPane = new JTextPane();
        textPane.setEditable(false);
        HTMLEditorKit kit = new HTMLEditorKit();
        HTMLDocument doc = new HTMLDocument();
        textPane.setEditorKit(kit);
        textPane.setDocument(doc);

        try {
            kit.insertHTML(doc, 0, "<b>" + name.toUpperCase() + "</b><br>", 0, 0, HTML.Tag.B);
            kit.insertHTML(doc, doc.getLength(), "<i>" + type + "</i><br>", 0, 0, HTML.Tag.I);
            kit.insertHTML(doc, doc.getLength(), "<hr>", 0, 0, HTML.Tag.HR);
            addBasicLine(kit, doc, "AC", ac);
            addBasicLine(kit, doc, "HP", hpToString());
            addBasicLine(kit, doc, "Speed", speed);
            kit.insertHTML(doc, doc.getLength(), "<hr>", 0, 0, HTML.Tag.HR);
            kit.insertHTML(doc, doc.getLength(), abilityTable(), 0, 0, null);
            kit.insertHTML(doc, doc.getLength(), "<hr>", 0, 0, HTML.Tag.HR);
            addBasicLine(kit, doc, "Saving Throws", saves);
            addBasicLine(kit, doc, "Skills", skills);
            addBasicLine(kit, doc, "Damage Vulnerabilities", vuln);
            addBasicLine(kit, doc, "Damage Resistances", resist);
            addBasicLine(kit, doc, "Damage Immunities", immu);
            addBasicLine(kit, doc, "Condition Immunities", conImmu);
            addBasicLine(kit, doc, "Senses",sense);
            addBasicLine(kit, doc, "Languages", lang);
            addBasicLine(kit, doc, "Challenge", this.challengeToString());
            kit.insertHTML(doc, doc.getLength(), "<hr>", 0, 0, HTML.Tag.HR);

            boolean castsSpells = false;
            for (Trait trait : traitList) {

                if (trait.getName().equalsIgnoreCase("spellcasting")) {
                    kit.insertHTML(doc, doc.getLength(), "<b><i>" + trait.getName() + ". </b></i>" + getFormattedSpells(trait).replace("\n", "<br>"), 0, 0, HTML.Tag.B);
                    castsSpells = true;
                }

                else if (trait.getName().toLowerCase().startsWith("innate spellcasting")) {
                    kit.insertHTML(doc, doc.getLength(), "<b><i>" + trait.getName() + ". </b></i>" + getFormattedInnateSpells(trait).replace("\n", "<br>"), 0, 0, HTML.Tag.B);
                    castsSpells = true;
                }

                else kit.insertHTML(doc, doc.getLength(), "<b><i>" + trait.getName() + ". </b></i>" + trait.getDescription().replace("\n", "<br>") + "<br>", 0, 0, HTML.Tag.B);
            }

            if (castsSpells) textPane.addHyperlinkListener(new HyperlinkListener() {
                @Override
                public void hyperlinkUpdate(HyperlinkEvent e) {
                    if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                        if (e.getDescription().startsWith("spell:")) { //tag to help protect against false spell tags added by the user
                            String spellName = e.getDescription();
                            PickSpell ps = new PickSpell();
                            ps.jumpTo(spellName.substring(6, spellName.length()), SwingUtilities.windowForComponent(panel));
                        }
                    }
                }
            });


            boolean hasReaction = false;

            if (actionList.size() > 0) {
                kit.insertHTML(doc, doc.getLength(), "<br><b>ACTIONS</b>", 0, 0, HTML.Tag.BR);
                kit.insertHTML(doc, doc.getLength(), "<hr>", 0, 0, HTML.Tag.HR);
                for (int i = 0; i < actionList.size(); i++) {
                    if (actionList.get(i) instanceof Attack) {
                        Attack atk = (Attack) actionList.get(i);
                        kit.insertHTML(doc, doc.getLength(), "<b><i>" + atk.getName() + ". </b></i><i>" + atk.getType() + " Attack: </i> +" + atk.getBonus() + " to hit. " + atk.getDescription().replace("\n", "<br>").replace("Hit:", "<i>Hit:</i>")
                                + "<br><br>", 0, 0, HTML.Tag.B);
                    } else if (actionList.get(i) instanceof Reaction) {
                        hasReaction = true;
                    } else if (!(actionList.get(i) instanceof Reaction)) {
                        kit.insertHTML(doc, doc.getLength(), "<b><i>" + actionList.get(i).getName() + ". </b></i>" + actionList.get(i).getDescription().replace("\n", "<br>") + "<br><br>", 0, 0, HTML.Tag.B);
                    }
                }
            }



            if (hasReaction) {
                kit.insertHTML(doc, doc.getLength(), "<b>REACTIONS</b>", 0, 0, null);
                kit.insertHTML(doc, doc.getLength(), "<hr>", 0, 0, HTML.Tag.HR);
                for (int i = 0; i < actionList.size(); i++) {
                    if (actionList.get(i) instanceof Reaction) {
                        kit.insertHTML(doc, doc.getLength(), "<b><i>" + actionList.get(i).getName() + ". </b></i>" + actionList.get(i).getDescription().replace("\n", "<br>") + "<br>", 0, 0, HTML.Tag.B);
                    }
                }
            }


            if (legActionCount > 0) {
                kit.insertHTML(doc, doc.getLength(), "<br><b>LEGENDARY ACTIONS</b>", 0, 0, null);
                kit.insertHTML(doc, doc.getLength(), "<hr>", 0, 0, HTML.Tag.HR);
                kit.insertHTML(doc, doc.getLength(), "The " + name + " can take " + legActionCount + " legendary actions, choosing from the options below. ", 0, 0, null);
                kit.insertHTML(doc, doc.getLength(), "Only one legendary action option can be used at a time and only at the end of another creature's turn. ", 0, 0, null);
                kit.insertHTML(doc, doc.getLength(), "The " + name + " regains spent legendary actions at the start of its turn. <br><br>", 0, 0, null);
                for (LegendaryAction legendaryAction : legActionList) {
                    kit.insertHTML(doc, doc.getLength(), "<b><i>" + legendaryAction.getName() + (legendaryAction.getCost() > 1 ? " (Costs " + legendaryAction.getCost() + " Actions)" : "")
                            + ". </b></i>" + legendaryAction.getDescription().replace("\n","<br>") + "<br>", 0, 0, HTML.Tag.B);
                }
            }

        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        JScrollPane textPaneScroll = new JScrollPane(textPane);
        panel.add(textPaneScroll);
        return panel;
    }

    private String abilityTable() {
        String[] mods = getMods();
        String retString = "<table cellpadding='3' style='font-size: 11'><tr><td><b>STR</b></td>" +
                "<td><b>DEX</b></td>" +
                "<td><b>CON</b></td>" +
                "<td><b>INT</b></td>" +
                "<td><b>WIS</b></td>" +
                "<td><b>CHA</b></td></tr>";
        retString += "<tr><td>" + scores[0] + " (" + mods[0] + ")</td>";
        retString += "<td>" + scores[1] + " (" + mods[1] + ")</td>";
        retString += "<td>" + scores[2] + " (" + mods[2] + ")</td>";
        retString += "<td>" + scores[3] + " (" + mods[3] + ")</td>";
        retString += "<td>" + scores[4] + " (" + mods[4] + ")</td>";
        retString += "<td>" + scores[5] + " (" + mods[5] + ")</td>";
        retString += "</tr></table><br>";
        return retString;
    }

    private String getFormattedSpells(Trait trait) {
        String desc = trait.getDescription();
        String formatSpells = "";
        String[] lineBrokenSpells = desc.split("\n");
        for (String str : lineBrokenSpells) {
            if (str.startsWith("Cantrips")) {
                formatSpells += str.substring(0,str.indexOf("): ") + 3);
                String spellList[] = str.substring(str.indexOf("): ") + 3, str.length()).split(", ");
                for (int i = 0; i < spellList.length; i++) {
                    formatSpells += "<font color='blue'><i><a href='spell:" + spellList[i] + "'>"+ spellList[i] + "</a></i></font>";
                    if (i != spellList.length - 1) formatSpells += ", ";
                }
                formatSpells += "<br>";
            }
            else if (str.toLowerCase().contains("level (")) {
                formatSpells += str.substring(0,str.indexOf("): ") + 3);
                String spellList[] = str.substring(str.indexOf("): ") + 3, str.length()).split(", ");
                for (int i = 0; i < spellList.length; i++) {
                    formatSpells += "<font color='blue'><i><a href='spell:" + spellList[i] + "'>"+ spellList[i] + "</a></i></font>";
                    if (i != spellList.length - 1) formatSpells += ", ";
                }
                formatSpells += "<br>";
            }
            else formatSpells += str + "<br>";
        }
        return formatSpells;
    }

    private String getFormattedInnateSpells(Trait trait) {
        String desc = trait.getDescription();
        String formatSpells = "";
        String[] lineBrokenSpells = desc.split("\n");
        for (String str : lineBrokenSpells) {
            if (str.toLowerCase().contains("will: ")) {
                formatSpells += str.substring(0,str.indexOf("will: ") + 6);
                String spellList[] = str.substring(str.indexOf("will: ") + 6, str.length()).split(", ");
                for (int i = 0; i < spellList.length; i++) {
                    String spellName = spellList[i];
                    String spellNote = "";
                    if (spellName.contains("(")) {
                        spellName = spellList[i].substring(0,spellList[i].indexOf("("));
                        spellNote = " " + spellList[i].substring(spellList[i].indexOf("("),spellList[i].length());
                    }
                    formatSpells += "<font color='blue'><i><a href='spell:" + spellName.trim() + "'>"+ spellName.trim() + "</a></i></font> " + spellNote;
                    if (i != spellList.length - 1) formatSpells += ", ";
                }
                formatSpells += "<br>";
            }
            else if (str.toLowerCase().contains("each: ")) {
                formatSpells += str.substring(0,str.indexOf("each: ") + 6);
                String spellList[] = str.substring(str.indexOf("each: ") + 6, str.length()).split(", ");
                for (int i = 0; i < spellList.length; i++) {
                    String spellName = spellList[i];
                    String spellNote = "";
                    if (spellName.contains("(")) {
                        spellName = spellList[i].substring(0,spellList[i].indexOf("("));
                        spellNote = " " + spellList[i].substring(spellList[i].indexOf("("),spellList[i].length());
                    }
                    formatSpells += "<font color='blue'><i><a href='spell:" + spellName.trim() + "'>"+ spellName.trim() + "</a></i></font>" + spellNote;
                    if (i != spellList.length - 1) formatSpells += ", ";
                }
                formatSpells += "<br>";
            }
            else formatSpells += str + "<br>";
        }
        return formatSpells;
    }

    private void addBasicLine(HTMLEditorKit kit, HTMLDocument doc, String name, String variable) {
        if (variable != null) {
            try {
                kit.insertHTML(doc, doc.getLength(), "<b>" + name + ": </b>" + variable + "<br>", 0, 0, HTML.Tag.B);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private String challengeToString() {
        String retString = "";
        if (challenge >= 1.0 || challenge == 0) {
            retString += (int) challenge;
        }

        else retString += challenge;

        retString += " (" + ManagerConstants.XP_VALS.get(challenge) + " XP)";

        return retString;
    }

    private String hpToString() {
        String retString = "" + this.hp;
        retString += " (" + hitDiceArray[0] + "d" + hitDiceArray[1] + " + " + hitDiceArray[2] + ")";
        return retString;
    }

    public int getRandomHp() {
        Random rand = new Random();
        int randTotal = 0;
        for (int i = 0; i < hitDiceArray[0]; i++) {
            randTotal += 1 + rand.nextInt(hitDiceArray[1] - 1);
        }
        return randTotal + hitDiceArray[2];

    }

    public int getInitiative() {
        return this.scores[1] / 2 - 5;
    }

    private void addBasicLine(HTMLEditorKit kit, HTMLDocument doc, String name, int variable) {
        addBasicLine(kit, doc, name, "" + variable);
    }

    public int compareTo(Monster mon) {
        return name.compareToIgnoreCase(mon.getName());
    }
}
