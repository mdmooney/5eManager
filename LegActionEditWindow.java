import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Michael on 27/11/2014.
 */
public class LegActionEditWindow extends PowerEditWindow {
    private LegendaryAction legendaryAction;
    private JSpinner legCostSpin;
    private JList actionJList = new JList();
    private JScrollPane actionScroller;
    private ArrayList<LegendaryAction> legendaryActionArrayList = new ArrayList<LegendaryAction>();
    private ArrayList<LegendaryAction> returningArrayList;
    private JPanel leftPanel;
    JButton newActionButton;
    JButton delActionButton;

    public LegActionEditWindow(JDialog parent) {
        super(parent);
        setPower(this.legendaryAction);
    }

    public LegActionEditWindow(JDialog parent, ArrayList<LegendaryAction> legActionArrayList) {
        super(parent);
        for (int i = 0; i < legActionArrayList.size(); i++) {
            LegendaryAction legAct = legActionArrayList.get(i);
            LegendaryAction addAct = new LegendaryAction(legAct.getName(), legAct.getDescription(), legAct.getCost());
            this.legendaryActionArrayList.add(addAct);
        }
        refreshActionList();
    }

    public void open() {
        super.open();
    }

    protected void setupGui() {
        newActionButton = new JButton("New Action");
        delActionButton = new JButton("Delete Action");

        newActionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                legendaryAction = null;
                LegendaryAction newAction = new LegendaryAction("New Action", "");
                legendaryActionArrayList.add(newAction);
                legendaryAction = newAction;
                refreshActionList();
            }
        });

        delActionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                legendaryActionArrayList.remove(legendaryAction);
                legendaryAction = null;
                refreshActionList();
                blankFields();
            }
        });


        leftPanel = new JPanel(new GridBagLayout());

        windowTitle = "Edit Legendary Actions";
        legCostSpin = new JSpinner(new SpinnerNumberModel(1,1,10,1));

        actionJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        actionJList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!actionJList.isSelectionEmpty()) {
                    legendaryAction = (LegendaryAction) actionJList.getSelectedValue();
                    fillFields(legendaryAction);
                    name.setEnabled(true);
                    legCostSpin.setEnabled(true);
                    description.setEnabled(true);
                }
                else {
                    name.setEnabled(false);
                    legCostSpin.setEnabled(false);
                    description.setEnabled(false);
                }
            }
        });

        actionScroller = new JScrollPane(actionJList);
        actionScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        actionScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        super.setupGui();

        name.setEnabled(false);
        description.setEnabled(false);
        legCostSpin.setEnabled(false);

        name.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if(legendaryAction != null) {legendaryAction.setName(name.getText());}
            }
        });

        description.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if(legendaryAction != null) {legendaryAction.setDescription(description.getText());}
            }
        });

        legCostSpin.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if(legendaryAction != null) {legendaryAction.setCost((Integer) legCostSpin.getValue());}
            }
        });
    }

    protected void layoutGui() {
        GridBagConstraints cb = new GridBagConstraints();
        super.layoutGui();

//        c.gridy = 1;
//        c.gridx = 0;
//        c.gridwidth = 1;
//        editPanel.add(new JLabel("Cost: "), c);
//        c.gridx++;
//        c.fill=GridBagConstraints.NONE;
//        editPanel.add(legCostSpin, c);

        cb.gridx = 0;
        cb.gridy=0;
        cb.weightx=0;
        cb.weighty=0;
        leftPanel.add(newActionButton, cb);
        cb.weightx=1;
        cb.gridx++;
        leftPanel.add(delActionButton, cb);
        cb.gridx = 0;
        cb.gridy = 1;
        cb.fill = GridBagConstraints.BOTH;
        cb.gridwidth = GridBagConstraints.REMAINDER;
        cb.gridheight = GridBagConstraints.REMAINDER;
        cb.weighty=1;
        leftPanel.add(actionScroller, cb);

        dialog.add(BorderLayout.WEST, leftPanel);
    }

    protected JPanel getInsertPanel() {
        JPanel costPanel = new JPanel(new GridBagLayout());
        GridBagConstraints cb = new GridBagConstraints();
        cb.gridx=0;
        cb.gridy=0;
        cb.anchor = GridBagConstraints.WEST;
        //cb.weightx=0;
        cb.gridwidth = 1;
        costPanel.add(new JLabel("Cost:         "), cb);
        cb.gridx++;
        cb.weightx=1;
        cb.fill=GridBagConstraints.NONE;
        costPanel.add(legCostSpin, cb);
        return costPanel;
    }

    void saveData() {
        if (legendaryActionArrayList != null) {
            returningArrayList = new ArrayList<LegendaryAction>();
            returningArrayList.addAll(legendaryActionArrayList);
        }
        dialog.dispose();
    }

    private void blankFields() {
        name.setText("");
        description.setText("");
        legCostSpin.setValue(1);
    }

    private void fillFields(LegendaryAction legendaryAction) {
        name.setText(legendaryAction.getName());
        description.setText(legendaryAction.getDescription());
        legCostSpin.setValue(legendaryAction.getCost());
    }

    private void refreshActionList() {
        Collections.sort(legendaryActionArrayList);
        actionJList.setListData(legendaryActionArrayList.toArray());
        actionJList.revalidate();
        if (legendaryAction != null) actionJList.setSelectedValue(legendaryAction, true);
    }
    protected void nullPower() {
        this.legendaryAction = null;
        this.power = null;
    }

    private void updateAction() {
        if (legendaryAction != null) {
            if (!name.getText().equals("")) {
                legendaryAction.setName(name.getText());
                legendaryAction.setDescription(description.getText());
                legendaryAction.setCost((Integer) legCostSpin.getValue());
                refreshActionList();
            }
        }
    }

    public ArrayList<LegendaryAction> getLegendaryActionArrayList() {
        //return this.legendaryActionArrayList;
        return this.returningArrayList;
    }
}
