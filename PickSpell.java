import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Created by Michael on 11/12/2014.
 */
public class PickSpell {
    private LibraryManager libMan = new LibraryManager(LibraryManager.LibType.SPELL);
    private ArrayList<Spell> spellArrayList;
    private ArrayList<LibEntry> entryList;
    private JList libList;
    private JDialog dialog;
    private JTextField searchField = new JTextField(18);
    private final Dimension LIST_DIMENSION = new Dimension(280, 180);
    private JPanel spellInfoPanel = new JPanel();


    public void open (Window parent) {
        entryList = libMan.getEntryList();

        dialog = new JDialog(parent, "Spell Library", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        spellInfoPanel.setLayout(new BoxLayout(spellInfoPanel, BoxLayout.X_AXIS)); //this panel works well with a box layout since it's really just a big block of text
        JPanel libPanel = new JPanel(new GridBagLayout());
        JPanel menuPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints(); //constraints for the library panel
        GridBagConstraints c2 = new GridBagConstraints(); //constraints for the menu (button) panel

        //create buttons for the library menu panel, add listeners, put them in the panel
        JButton newButton = new JButton("New");
        JButton editButton = new JButton("Edit");
        JButton copyButton = new JButton("Copy");
        JButton delButton = new JButton("Delete");
        menuPanel.add(newButton, c2);
        menuPanel.add(editButton, c2);
        menuPanel.add(copyButton, c2);
        menuPanel.add(delButton, c2);
        newButton.addActionListener(new NewSpellListener());
        //todo: add listeners

        //set up list and scroller for library JList
        libList = new JList();
        JScrollPane libScroller = new JScrollPane(libList);
        libScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        libScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        libScroller.setPreferredSize(LIST_DIMENSION);

        //refresh the library list
        refreshLibraryList();

        //add stuff to the library panel
        c.gridx=0;
        c.gridy=0;
        c.anchor=GridBagConstraints.NORTHWEST;
        libPanel.add(menuPanel,c);
        c.gridy++;
        c.gridwidth=GridBagConstraints.REMAINDER;
        c.fill=GridBagConstraints.HORIZONTAL;
        libPanel.add(libScroller,c);

        //add panels to the dialog
        dialog.add(BorderLayout.WEST, libPanel);
        dialog.add(BorderLayout.EAST, spellInfoPanel);

        //pack and display the dialog
        dialog.pack();
        dialog.setMinimumSize(dialog.getSize());
        dialog.setVisible(true);
    }

    private void refreshLibraryList() {
        entryList = libMan.getEntryList();
        searchField.setText("");
        libList.setListData(entryList.toArray());
        libList.revalidate();
    }

    class NewSpellListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            SpellEditWindow sew = new SpellEditWindow(dialog);
            sew.open();
            Spell newSpell = sew.getSpell();
            if (newSpell != null) {
                libMan.addToLibrary(newSpell);
                refreshLibraryList();
                spellInfoPanel.revalidate();
            }
        }
    }
}
