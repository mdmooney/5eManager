import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by Michael on 2014-12-12.
 */
public class PickPlayer extends PickFightable {

    HashMap<Participant, Integer> addedPlayers;

    public PickPlayer(HashMap<Participant, Integer> addedPlayers) {
        this.addedPlayers = addedPlayers;
    }

    protected void pickLibrary() {
        windowName = "Player Library";
        libMan = new LibraryManager(LibraryManager.LibType.PLAYER);
    }

    protected void newEntry() {
        PlayerEditWindow pew = new PlayerEditWindow();
        pew.open(frame);
        Player newPlayer = pew.getPlayer();
        if (newPlayer != null) {
            libMan.addToLibrary(newPlayer);
            refreshLibraryList();
            panel.revalidate();
        }
    }

    protected void editEntry() {
        int editPlayerIndex = ((LibEntry) libList.getSelectedValue()).getIndex();
        ArrayList<Integer> deleteIndex = new ArrayList<Integer>();
        deleteIndex.add(editPlayerIndex);
        Player editPlayer = (Player) libMan.loadMember(editPlayerIndex);
        PlayerEditWindow pew = new PlayerEditWindow(editPlayer);
        pew.open(frame);
        Player newPlayer = pew.getPlayer();
        if (newPlayer != null) {
            int saveIndex = libList.getSelectedIndex();
            libMan.removeFromLibrary(deleteIndex);
            entryList = libMan.getEntryList();
            libMan.addToLibrary(newPlayer);
            refreshLibraryList();
            panel.revalidate();
            libList.setSelectedIndex(saveIndex);
        }
    }

    protected void copyEntry() {
        Player copyPlayer = (Player) libMan.loadMember(((LibEntry) libList.getSelectedValue()).getIndex());
        PlayerEditWindow pew = new PlayerEditWindow(copyPlayer);
        pew.open(frame);
        Player newPlayer = pew.getPlayer();
        if (newPlayer != null) {
            libMan.addToLibrary(newPlayer);
            refreshLibraryList();
            panel.revalidate();
        }
    }

    @Override
    protected void addSelection() {
        List<Participant> addParticipant = new ArrayList<Participant>();
        for(int i = 0; i < libList.getSelectedValuesList().size(); i++) {
            int index = ((LibEntry) libList.getSelectedValuesList().get(i)).getIndex();
            if (!addedPlayers.containsValue(index)) {
                Participant adding = new Participant((Fightable) libMan.loadMember(index));
                addParticipant.add(adding);
                addedPlayers.put(adding, index);
            }
        }
        for (Participant part : addParticipant) {
            //addingList.add(fight);
            addingList.add(part);
        }
        updateAddList();
    }

    private void makeParticipants() {
        Collections.sort(addingList);
        participants = addingList;
    }

    @Override
    protected void addToEncounter() {
        frame.dispose();
    }

    @Override
    protected void removeSelection() {
        List<Participant> remParts = encList.getSelectedValuesList();
        for (Participant part : remParts) {
            addingList.remove(part);
            addedPlayers.remove(part);
        }
        updateAddList();
        makeParticipants();
    }

    @Override
    protected void updateAddList() {
        if (addingList.size() == 0 && !addedPlayers.isEmpty()) {
            System.out.println(addedPlayers.values());
            for (int i = 0; i < addedPlayers.keySet().size(); i++) {
                int loadIndex = (Integer) addedPlayers.values().toArray()[i];
                Participant adding = (Participant) addedPlayers.keySet().toArray()[i];
                //addingList.add(adding);
                addingList.add(adding);
            }
            //Collections.sort(addingList, new FightableCompare());
            Collections.sort(addingList);
            updateAddList();
        }
        else {
            Collections.sort(addingList);
            super.updateAddList();
        }
        makeParticipants();
    }

    private class FightableCompare implements Comparator<Fightable> {
        public int compare(Fightable one, Fightable two) {
            return one.getName().compareToIgnoreCase(two.getName());
        }
    }

}
