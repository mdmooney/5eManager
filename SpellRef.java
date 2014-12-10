import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class for spell references, used in PC stats to contain spell names, levels, and classes, even if no corresponding
 * spell entry exists in the database. A SpellRef object effectively includes the name of the class for which the spells are being stored,
 * the ability that class uses to cast spells, available slots by level in an integer array, and a list of spells by level in a ten-slot
 * String array (9 levels of spells + 1 for cantrips).
 */
@XmlRootElement(name="spellRef")

public class SpellRef {
    private String casterClass;
    private int castAbility; //ability score used for the spellcasting class in the standard format (0 = STR, 1 = DEX, 2 = CON...)
    private int[] slots = new int[9];
    private String[] spells = new String[10];

    public String getCasterClass() {
        return casterClass;
    }

    public void setCasterClass(String casterClass) {
        this.casterClass = casterClass;
    }

    public int getCastAbility() {
        return castAbility;
    }

    public void setCastAbility(int castAbility) {
        this.castAbility = castAbility;
    }

    public int[] getSlots() {
        return slots;
    }

    public void setSlots(int[] slots) {
        this.slots = slots;
    }

    public String[] getSpells() {
        return spells;
    }

    public void setSpells(String[] spells) {
        this.spells = spells;
    }

    public void testPrint() {
        System.out.println("Cantrips: " + spells[0]);
        for (int i = 0; i < 9; i++) {
            System.out.println("Level " + (i + 1) + " spells (" + slots[i] + " slots): ");
            System.out.println(spells[i+1]);
        }
    }
}
