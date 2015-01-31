/**
 * The LibEntry class exists to coordinate library entries (chiefly monsters and spells) with their indices in their respective lists.
 * It stores names as Strings which are returned by the toString() method, so that when displayed, only the entry's name is shown.
 * This class is used in place of entry names in the library lists to enable the dynamic searching used in those libraries (to ensure
 * that indexes are kept straight so that, even when the list is displaying limited information, selecting the entry will still pull
 * the correct entry from the external XML file appropriate to that library).
 */
public class LibEntry implements Comparable<LibEntry> {
    private String name;
    private int index;

    /**
     * Sole constructor for the LibEntry class requires a name by which the entry is to be identified and an index, referring to its
     * position in the library.
     * @param name The String for the name of the LibEntry's referred object.
     * @param index The index of the actual object in its respective library.
     */
    public LibEntry(String name, int index) {
        this.name = name;
        this.index = index;
    }

    /**
     * Getter for the LibEntry's name.
     * @return The LibEntry's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for the LibEntry's index.
     * @return The LibEntry's index.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Setter for the LibEntry's index.
     * @param index The LibEntry's index.
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * The toString() method called commonly by Java returns, for the LibEntry class, just the name.
     * The index is not relevant to any situation in which the LibEntry would need to be referred to as a String.
     * @return The LibEntry's name.
     */
    @Override
    public String toString() {
        return this.getName();
    }

    /**
     * Override for comparison to other LibEntry objects. Essentially just compares the name.
     * Mainly for alphabetical sorting, which is generally done in the Pick(X) windows, where (X) is the type of object
     * to which LibEntry objects can refer to, such as the PickMonster class.
     * @param entry The LibEntry to be compared.
     * @return A positive, negative, or 0 integer for greater, lower, or equal comparison, respectively. Equivalent to compareToIgnoreCase called on the names of both LibEntry objects.
     */
    @Override
    public int compareTo(LibEntry entry) {
        return (this.getName().compareToIgnoreCase(entry.getName()));
    }
}
