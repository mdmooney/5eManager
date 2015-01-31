import javax.swing.*;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.xml.bind.annotation.*;
import java.awt.*;

/**
 * Class for spells, including all details covered for spells in the PHB.
 */

@XmlRootElement(name="spellblock")
@XmlSeeAlso(Power.class)
@XmlAccessorType(XmlAccessType.FIELD)

public class Spell extends Power implements LibraryMember {
    private String school;
    private String castTime;
    private String range;
    private String components;
    private String duration;
    private int level;
    private boolean ritual;

    public Spell() {}

    public Spell(String name, String desc) {
        super(name, desc);
    }
    public Spell(String name, String desc, String school, String castTime, String range, String components, String duration, int level, boolean ritual) {
        super(name, desc);
        this.school = TextFormat.pruneText(school);
        this.castTime = TextFormat.pruneText(castTime);
        this.range = TextFormat.pruneText(range);
        this.components = TextFormat.pruneText(components);
        this.duration = TextFormat.pruneText(duration);
        this.level = level;
        this.ritual = ritual;
    }

    public String getSchool() {
        return TextFormat.pruneText(school);
    }

    public void setSchool(String school) {
        this.school = TextFormat.pruneText(school);
    }

    public String getCastTime() {
        return TextFormat.pruneText(castTime);
    }

    public void setCastTime(String castTime) {
        this.castTime = TextFormat.pruneText(castTime);
    }

    public String getRange() {
        return TextFormat.pruneText(range);
    }

    public void setRange(String range) {
        this.range = TextFormat.pruneText(range);
    }

    public String getComponents() {
        return TextFormat.pruneText(components);
    }

    public void setComponents(String components) {
        this.components = TextFormat.pruneText(components);
    }

    public String getDuration() {
        return TextFormat.pruneText(duration);
    }

    public void setDuration(String duration) {
        this.duration = TextFormat.pruneText(duration);
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isRitual() {
        return ritual;
    }

    public void setRitual(boolean ritual) {
        this.ritual = ritual;
    }

    public JPanel getBlockPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JTextPane textPane = new JTextPane();
        textPane.setEditable(false);
        HTMLEditorKit kit = new HTMLEditorKit();
        HTMLDocument doc = new HTMLDocument();
        textPane.setEditorKit(kit);
        textPane.setDocument(doc);

        try {
            kit.insertHTML(doc, 0, "<b>" + this.getName().toUpperCase() + "</b><br>", 0, 0, HTML.Tag.B);
            kit.insertHTML(doc, doc.getLength(), "<i>" + getLevelString() + "</i><br>", 0, 0, HTML.Tag.I);
            kit.insertHTML(doc, doc.getLength(), "<br>", 0, 0, HTML.Tag.BR);
            addBasicLine(kit, doc, "Casting Time", castTime);
            addBasicLine(kit, doc, "Range", range);
            addBasicLine(kit, doc, "Components", components);
            addBasicLine(kit, doc, "Duration", duration);
            kit.insertHTML(doc, doc.getLength(), getDescription().replace("\n", "<br>").replace("At Higher Levels.", "   <b>At Higher Levels.</b>"), 0, 0, null);
            kit.insertHTML(doc, doc.getLength(), "<hr>", 0, 0, HTML.Tag.HR);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        JScrollPane textPaneScroll = new JScrollPane(textPane);
        panel.add(textPaneScroll);
        panel.setPreferredSize(new Dimension(350, 120));
        textPane.setCaretPosition(0);
        return panel;
    }

    private String getLevelString() {
        if (level == 0) return school + " " + "cantrip";
        else {
            String retString = "" + level;
            if (level == 1) retString += "st";
            else if (level == 2) retString += "nd";
            else if (level == 3) retString += "rd";
            else retString += "th";
            retString += "-level " + school.toLowerCase();
            return retString;
        }
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

}
