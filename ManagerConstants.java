import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Michael on 24/11/2014.
 */

//test comment to ensure git is working correctly so far
//second test comment to try coordinating git... again

public final class ManagerConstants {

    final static private String path = MainWindow.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    final static private String shortPath = path.substring(0, path.lastIndexOf("/") + 1);

    public static final String MONSTER_XML = shortPath + "/monster.5em";
    public static final String MONSTER_TEMP_XML = shortPath + "/monster.temp";
    public static final String PLAYER_XML = shortPath + "/player.5em";
    public static final String PLAYER_TEMP_XML = shortPath + "/player.temp";
    public static final String SPELL_XML = "./spells.5em";
    public static final String SPELL_TEMP_XML = "./spells.temp";

    public static final String[] DICE_TYPES = {"d4", "d6", "d8", "d10", "d12"};
    public static final String[] SPELL_SCHOOLS = {"Abjuration", "Conjuration", "Divination", "Enchantment", "Evocation", "Illusion", "Necromancy", "Transmutation"};

    public static final String[] SHORT_ABILITY_NAMES = {"STR", "DEX", "CON", "INT", "WIS", "CHA"};

    static final Map<Double, Integer> XP_VALS;
    static {
        Map<Double, Integer> aMap = new HashMap<Double, Integer>();
        aMap.put(0.0, 10);
        aMap.put(0.125, 25);
        aMap.put(0.25, 50);
        aMap.put(0.5, 100);
        aMap.put(1.0, 200);
        aMap.put(2.0, 450);
        aMap.put(3.0, 700);
        aMap.put(4.0, 1100);
        aMap.put(5.0, 1800);
        aMap.put(6.0, 2300);
        aMap.put(7.0, 2900);
        aMap.put(8.0, 3900);
        aMap.put(9.0, 5000);
        aMap.put(10.0, 5900);
        aMap.put(11.0, 7200);
        aMap.put(12.0, 8400);
        aMap.put(13.0, 10000);
        aMap.put(14.0, 11500);
        aMap.put(15.0, 13000);
        aMap.put(16.0, 15000);
        aMap.put(17.0, 18000);
        aMap.put(18.0, 20000);
        aMap.put(19.0, 22000);
        aMap.put(20.0, 25000);
        aMap.put(21.0, 33000);
        aMap.put(22.0, 41000);
        aMap.put(23.0, 50000);
        aMap.put(24.0, 62000);
        aMap.put(25.0, 75000);
        aMap.put(26.0, 90000);
        aMap.put(27.0, 10500);
        aMap.put(28.0, 12000);
        aMap.put(29.0, 135000);
        aMap.put(30.0, 155000);
        XP_VALS = Collections.unmodifiableMap(aMap);
    }

}