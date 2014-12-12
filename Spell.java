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
