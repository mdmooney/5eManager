import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Created by Michael on 2014-12-12.
 */
public class PickPlayer {
    private LibraryManager libMan = new LibraryManager(LibraryManager.LibType.PLAYER);
    private ArrayList<Participant> participants;
    private ArrayList<LibEntry> entryList;
    private JList libList;
    private JDialog dialog;
    private JPanel panel;

    public void open(Window parent) {
        entryList = libMan.getEntryList();
        JPanel menuPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        //create buttons for the library menu panel, add listeners, put them in the panel
        JButton newButton = new JButton("New");
        JButton editButton = new JButton("Edit");
        JButton copyButton = new JButton("Copy");
        JButton delButton = new JButton("Delete");
        menuPanel.add(newButton, c);
        menuPanel.add(editButton, c);
        menuPanel.add(copyButton, c);
        menuPanel.add(delButton, c);
        newButton.addActionListener(new NewPcListener());
        //editButton.addActionListener(new EditPcListener());
        //copyButton.addActionListener(new CopyPcListener());
        //delButton.addActionListener(new DelPcListener());

        dialog = new JDialog(parent, "Player Library", Dialog.ModalityType.APPLICATION_MODAL);

        panel = new JPanel(new GridBagLayout());

        libList = new JList();
        JScrollPane libScroller = new JScrollPane(libList, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        //refresh the library list by getting a list of PCs in the database
        refreshLibraryList();

        c.gridx=0;
        c.gridy=0;
        panel.add(menuPanel, c);
        c.gridy++;
        panel.add(libScroller,c);

        dialog.add(BorderLayout.CENTER, panel);
        dialog.pack();
        dialog.setVisible(true);
    }

    private void refreshLibraryList() {
        entryList = libMan.getEntryList();
        libList.setListData(entryList.toArray());
        libList.revalidate();
    }

    class NewPcListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            PlayerEditWindow pew = new PlayerEditWindow();
            pew.open(dialog);
            Player newPc = pew.getPlayer();
            if (newPc != null) {
                libMan.addToLibrary(newPc);
                refreshLibraryList();
                panel.revalidate();
            }
        }
    }
}
