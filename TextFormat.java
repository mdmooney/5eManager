/**
 * Created by Michael on 12/12/2014.
 */
public class TextFormat {

    /** Cleans simple HTML tags from a string.
     *  Used to ensure the user doesn't break everything by using HTML formatting, as without this, virtually any HTML would work as written
     *  when displayed in the HTML-formatted block panels (easy way to wreck the program at runtime).
     *  @param entryString The String to be pruned.
     *  @return The String without HTML tags.
     */
    public static String pruneText(String entryString) {
        String prunedString = "";
        if (entryString != null){
            prunedString = entryString.replaceAll("<.*?>", "");
        }
        return prunedString;
    }

}
