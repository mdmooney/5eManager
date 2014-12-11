/**
 * The LibEntry class exists to coordinate library entries (chiefly monsters and spells) with their indexes in their respective lists.
 * It stores names as Strings which are returned by the toString() method, so that when displayed, only the entry's name is shown.
 * This class is used in place of entry names in the library lists to enable the dynamic searching used in those libraries (to ensure
 * that indexes are kept straight so that, even when the list is displaying limited information, selecting the entry will still pull
 * the correct entry from the external XML file appropriate to that library).
 */
public class LibEntry implements Comparable<LibEntry> {
    private String name;
    private int index;

    public LibEntry(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String toString() {
        return this.getName();
    }

    public int compareTo(LibEntry entry) {
        return (this.getName().compareToIgnoreCase(entry.getName()));
    }
}
