import java.awt.event.*;
import javax.swing.*;
import java.awt.*;

/**
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

    public PowerEditWindow(JDialog parent) {
        this.parent = parent;
    }


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


    protected abstract JPanel getInsertPanel();

    abstract void saveData();

    protected abstract void nullPower();

    public Power getPower() {
        return power;
    }

    public void setPower(Power power) {
        this.power = power;
    }

}

