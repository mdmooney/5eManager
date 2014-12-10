/**
 * Created by Michael on 29/11/2014.
 */
public class StatLibEntry implements Comparable<StatLibEntry> {
    private String name;
    private int index;

    public StatLibEntry(String name, int index) {
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

    public int compareTo(StatLibEntry entry) {
        return (this.getName().compareToIgnoreCase(entry.getName()));
    }
}
