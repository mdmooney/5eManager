import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.xml.bind.annotation.*;
import javax.xml.soap.Text;
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
    private ArrayList<Trait> traitList = new ArrayList<Trait>();
    private ArrayList<Action> actionList = new ArrayList<Action>();
    private ArrayList<SpellRef> spellRefs = new ArrayList<SpellRef>();
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
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JTabbedPane tabbedPane = new JTabbedPane();
        JTextPane spellPane = new JTextPane();
        JTextPane textPane = new JTextPane();
        textPane.setEditable(false);
        spellPane.setEditable(false);
        HTMLEditorKit kit = new HTMLEditorKit();
        HTMLDocument doc = new HTMLDocument();
        HTMLDocument spellDoc = new HTMLDocument();
        textPane.setEditorKit(kit);
        textPane.setDocument(doc);

        try {
            kit.insertHTML(doc, 0, "<b>" + name.toUpperCase() + "</b><br>", 0, 0, HTML.Tag.B);
            kit.insertHTML(doc, doc.getLength(), "<i>" + classes + "</i><br>", 0, 0, HTML.Tag.I);
            kit.insertHTML(doc, doc.getLength(), "<hr>", 0, 0, HTML.Tag.HR);
            addBasicLine(kit, doc, "AC", "" + ac);
            addBasicLine(kit, doc, "HP", "" + hp);
            addBasicLine(kit, doc, "Speed", speed);
            kit.insertHTML(doc, doc.getLength(), "<hr>", 0, 0, HTML.Tag.HR);
            kit.insertHTML(doc, doc.getLength(), abilityTable(), 0, 0, null);
            kit.insertHTML(doc, doc.getLength(), "<hr>", 0, 0, HTML.Tag.HR);
            kit.insertHTML(doc, doc.getLength(), "<hr>", 0, 0, HTML.Tag.HR);

            boolean castsSpells = false;
            for (Trait trait : traitList) {
                kit.insertHTML(doc, doc.getLength(), "<b><i>" + trait.getName() + ". </b></i>" + trait.getDescription().replace("\n", "<br>") + "<br>", 0, 0, HTML.Tag.B);
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
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        if (spellRefs.size() > 0) {
            try {
                spellPane.setEditorKit(kit);
                spellPane.setDocument(spellDoc);
                kit.insertHTML(spellDoc, 0, "<b>" + name.toUpperCase() + "</b><br>", 0, 0, HTML.Tag.B);
                kit.insertHTML(spellDoc, spellDoc.getLength(), "<i>" + classes + "</i><br>", 0, 0, HTML.Tag.I);
                kit.insertHTML(spellDoc, spellDoc.getLength(), "<hr>", 0, 0, HTML.Tag.HR);
                for (SpellRef spellRef : spellRefs) {
                    kit.insertHTML(spellDoc, spellDoc.getLength(), "<b><u>" + spellRef.getCasterClass() + "</u></b><br>", 0, 0, HTML.Tag.B);
                    addBasicLine(kit, spellDoc, "Casting Ability", ManagerConstants.LONG_ABILITY_NAMES[spellRef.getCastAbility()]);
                    addBasicLine(kit, spellDoc, "Spell Save DC", "" + (8 + proficiency + (scores[spellRef.getCastAbility()] / 2 - 5)));
                    addBasicLine(kit, spellDoc, "Spell Attack", "" +  (proficiency + (scores[spellRef.getCastAbility()] / 2 - 5)));
                    //add cantrips
                    if (!spellRef.getSpells()[0].isEmpty()) {
                        String[] splitSpells = spellRef.getSpells()[0].split("\n");
                        String spellString = "";
                        for (int j = 0; j < splitSpells.length; j++) {
                            String spell = splitSpells[j];
                            spellString += "<font color='blue'><i><a href='spell:" + spell + "'>"+ spell + "</a></i></font>";
                            if (j != splitSpells.length - 1) spellString += ", ";
                        }
                        kit.insertHTML(spellDoc, spellDoc.getLength(), "<i>Cantrips (at will): </i>" + spellString + "<br>", 0, 0, HTML.Tag.I);
                    }
                    //add levelled spells
                    for (int i = 0; i < 9; i++) {
                        if (!spellRef.getSpells()[i+1].isEmpty()) {
                            String[] splitSpells = spellRef.getSpells()[i+1].split("\n");
                            String spellString = "";
                            for (int j = 0; j < splitSpells.length; j++) {
                                String spell = splitSpells[j];
                                spellString += "<font color='blue'><i><a href='spell:" + spell + "'>"+ spell + "</a></i></font>";
                                if (j != splitSpells.length - 1) spellString += ", ";
                            }
                            kit.insertHTML(spellDoc, spellDoc.getLength(), "<i>Level " + (i + 1) + " (" + spellRef.getSlots()[i] + " slots): </i>" + spellString + "<br>", 0, 0, HTML.Tag.I);
                            //kit.insertHTML(spellDoc, spellDoc.getLength(), spellString, 0, 0, null);
                        }
                    }
                    kit.insertHTML(spellDoc, spellDoc.getLength(), "<hr>", 0, 0, HTML.Tag.HR);
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            spellPane.addHyperlinkListener(new HyperlinkListener() {
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
        }
        JScrollPane textPaneScroll = new JScrollPane(textPane, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        JScrollPane spellPaneScroll = new JScrollPane(spellPane, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        tabbedPane.add(textPaneScroll, "Main");
        if(spellPane.getText().length() != 0) { tabbedPane.add(spellPaneScroll, "Spellcasting"); }
        panel.add(tabbedPane);
        return panel;
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

    public String[] getMods() {
        String[] mods = new String[6];
        for (int i = 0; i < mods.length; i++) {
            int mod = scores[i] /2 - 5;
            mods[i] = mod > 0 ? "+" + mod : "" + mod;
        }
        return mods;
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
        this.name = TextFormat.pruneText(name);
    }

    public void setClasses(String classes) {
        this.classes = TextFormat.pruneText(classes);
    }

    public void setSaves(int[] saves) {
        this.saves = saves;
    }

    public void setProfs(String profs) {
        this.profs = TextFormat.pruneText(profs);
    }

    public void setLang(String lang) {
        this.lang = TextFormat.pruneText(lang);
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
        this.speed = TextFormat.pruneText(speed);
    }

    public void setNotes(String notes) {
        this.notes = TextFormat.pruneText(notes);
    }

    public void setInitiative(int initiative) {
        this.initiative = initiative;
    }

    public String getClasses() {
        return TextFormat.pruneText(classes);
    }

    public int[] getSaves() {
        return saves;
    }

    public String getProfs() {
        return TextFormat.pruneText(profs);
    }

    public String getLang() {
        return TextFormat.pruneText(lang);
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
        return TextFormat.pruneText(speed);
    }

    public String getNotes() {
        return TextFormat.pruneText(notes);
    }

    public int getLevel() {
        return level;
    }

    public String getBackground() {
        return TextFormat.pruneText(background);
    }

    public String getRace() {
        return TextFormat.pruneText(race);
    }

    public String getAlignment() {
        return TextFormat.pruneText(alignment);
    }

    public void setRace(String race) {
        this.race = TextFormat.pruneText(race);
    }

    public void setBackground(String background) {
        this.background = TextFormat.pruneText(background);
    }

    public void setAlignment(String alignment) {
        this.alignment = TextFormat.pruneText(alignment);
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
        return TextFormat.pruneText(this.name);
    }

    protected String pruneText(String entryString) {
        return TextFormat.pruneText(entryString);
    }
}
