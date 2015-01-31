import java.awt.event.*;
import javax.swing.*;
import java.awt.*;

/**
 * A Swing GUI dialog for editing generic Powers. This is an abstract class, because under no circumstances for a generic
 * untyped power be actually created by the user.
 * Instead, this provides a framework for creating editing windows for all Power subclasses. It creates a window with a field
 * for a name and a TextArea for a description (the minimum inheritable attributes from the Power class), but also provides
 * for a subpanel containing any unique GUI elements for Power subclass-specific attributes being accounted for.
 * Created by Michael on 19/11/2014.
 */

public abstract class PowerEditWindow {
    protected JDialog dialog;
    protected JTextField name;
    protected JPanel editPanel;
    protected JPanel buttonPanel;
    protected JTextArea description;
    private JDialog parent;
    protected Power power;
    protected JScrollPane descScroll;
    protected JButton saveButton;
    protected JButton cancelButton;
    protected GridBagConstraints c;
    protected String windowTitle;

    /**
     * Default constructor. Defines a parent window (mainly for modality purposes, also relative positioning) but nothing else.
     * @param parent
     */
    public PowerEditWindow(JDialog parent) {
        this.parent = parent;
    }


    /**
     * Method called when the window is opened. Calls for GUI setup, and then sets up the dialog's basic parameters
     * (elementary layout, size, packing, close operation, etc.) before setting everything to visible.
     */
    public void open() {
        setupGui();
        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent e) {
                nullPower(); //make the power null on exiting so it isn't added to the library if it's undesired
            }
        });
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.add(BorderLayout.CENTER, editPanel);
        dialog.add(BorderLayout.SOUTH, buttonPanel);
        dialog.setMinimumSize(new Dimension(300, 300));
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }

    /**
     * Method for setting up the Swing GUI. Called by the open() method to actually create the GUI itself.
     * This method mostly just creates pertinent elements. They are placed in the dialog by the layoutGui() method,
     * which is called at the end of this method.
     */
    protected void setupGui() {
        dialog = new JDialog(parent, windowTitle, Dialog.ModalityType.APPLICATION_MODAL);
        editPanel = new JPanel();
        buttonPanel = new JPanel();
        editPanel.setLayout(new GridBagLayout());

        name = new JTextField(22);
        description = new JTextArea(20, 30);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        descScroll = new JScrollPane(description);
        descScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        descScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        descScroll.setMinimumSize(new Dimension(250, 250));
        descScroll.setPreferredSize(new Dimension(330, 330));

        saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveData();
            }
        });

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                nullPower();
                dialog.dispose();
            }
        });

        layoutGui();
    }

    /**
     * Method for laying out the GUI in a GridBagLayout. Sets up GridBagConstraints and puts everything in its place.
     * Adds an "Insert Panel" if one is available, which is basically to allow inheriting subclasses to create a subpanel
     * with any subclass-specific GUI elements needed for a window of that particular type. This Insert Panel is placed
     * in between the name field and the description Text Area if it exists.
     */
    protected void layoutGui() { //separate in order to allow being overridden by subclasses more easily
        c = new GridBagConstraints();
        c.insets = new Insets(2,10,0,10);
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.NORTHWEST;
        editPanel.add(new JLabel("Name:"),c);
        c.gridx=1;
        c.gridwidth=GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.HORIZONTAL;
        editPanel.add(name, c);
        c.gridy=1;
        c.gridx=0;
        c.gridwidth=2;
        editPanel.add(getInsertPanel(),c);
        c.gridy=2;
        c.gridx=0;
        c.gridwidth=GridBagConstraints.REMAINDER;
        editPanel.add(new JLabel("Description:"),c);
        c.gridwidth = 2;
        c.gridy=3;
        editPanel.add(descScroll, c);
        c.gridwidth=1;
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        if(this.getPower() != null) {
            name.setText(power.getName());
            description.setText(power.getDescription());
        }
    }


    /**
     * Get the "Insert Panel" if one is available, which is basically a JPanel to allow inheriting subclasses to create
     * a subpanelwith any subclass-specific GUI elements needed for a window of that particular type. This Insert Panel
     * is placed in between the name field and the description Text Area if it exists.
     * Abstract because every subclass must define one, even if it is just null (otherwise layout won't work correctly).
     * @return The JPanel to be inserted in the middle of things.
     */
    protected abstract JPanel getInsertPanel();

    /**
     * Each window inheriting from this one has a "Save Data" button, which, when clicked, must do something.
     * This will vary by Power subclass type, so this is left abstract.
     */
    abstract void saveData();

    /**
     * Every Power Edit Window must have a way of nullifying the current Power (or Action or Attack or Trait or whatever)
     * to avoid it being kept in memory if the window is closed.
     * This will vary by Power subclass type, so this is left abstract.
     */
    protected abstract void nullPower();

    /**
     * Get the Power being edited. Generally, this is undesirable, and subclass windows will have a get(X) for whatever
     * subclass of Power they represent, as opposed to this method, which will normally return the same thing but as a
     * generic power.
     * @return The reference power for the PowerEditWindow.
     */
    public Power getPower() {
        return power;
    }

    /**
     * Set the reference power. Most subclass windows will set this to be a reference to whatever subclass object they're
     * creating or editing (such as an Action or Trait)
     * @param power The reference power for the PowerEditWindow.
     */
    public void setPower(Power power) {
        this.power = power;
    }

}

