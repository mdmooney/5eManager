import javax.swing.*;

/**
 * Interface to ensure entries to go in the various libraries have the methods needed for library manipulation (esp. by the LibraryManager class)
 */
public abstract interface LibraryMember {
    public abstract String getName();

    public abstract JPanel getBlockPanel();
}
