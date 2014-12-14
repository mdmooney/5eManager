/**
 * Created by Michael on 09/11/2014.
 */

import java.util.*;

public class PickMonster extends PickFightable {
    
    protected void pickLibrary() {
        windowName = "Monster Library";
        libMan = new LibraryManager(LibraryManager.LibType.MONSTER);
    }

    protected void newEntry() {
        MonEditWindow mew = new MonEditWindow();
        mew.open(frame);
        Monster newMon = mew.getMon();
        if (newMon != null) {
            libMan.addToLibrary(newMon);
            refreshLibraryList();
            panel.revalidate();
        }
    }

    protected void editEntry() {
        try {
            int editMonIndex = ((LibEntry) libList.getSelectedValue()).getIndex();
            ArrayList<Integer> deleteIndex = new ArrayList<Integer>();
            deleteIndex.add(editMonIndex);
            Monster editMon = (Monster) libMan.loadMember(editMonIndex);
            MonEditWindow mew = new MonEditWindow(editMon);
            mew.open(frame);
            Monster newMon = mew.getMon();
            if (newMon != null) {
                int saveIndex = libList.getSelectedIndex();
                libMan.removeFromLibrary(deleteIndex);
                entryList = libMan.getEntryList();
                libMan.addToLibrary(newMon);
                refreshLibraryList();
                panel.revalidate();
                libList.setSelectedIndex(saveIndex);
            }
        }
        catch (NullPointerException nex) {
            newEntry();
        }
    }

    protected void copyEntry() {
        Monster copyMon = (Monster) libMan.loadMember(((LibEntry) libList.getSelectedValue()).getIndex());
        MonEditWindow mew = new MonEditWindow(copyMon);
        mew.open(frame);
        Monster newMon = mew.getMon();
        if (newMon != null) {
            libMan.addToLibrary(newMon);
            refreshLibraryList();
            panel.revalidate();
        }
    }

}