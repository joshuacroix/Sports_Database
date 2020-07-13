import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class ConditionPanel extends JPanel {

    private JList conditionList;
    private JList sortByList;
    private JComboBox compareValueComboBox;
    private JRadioButton greaterThanBtn;
    private JRadioButton lessThanBtn;
    private JRadioButton equalToBtn;
    private JRadioButton ascBtn;
    private JRadioButton descBtn;
    public ButtonGroup operatorsBGroup;
    public ButtonGroup sortBGroup;

    public ConditionPanel() {
        //Creating Mid Panel
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbcMid = new GridBagConstraints();
        gbcMid.fill = GridBagConstraints.HORIZONTAL;
        gbcMid.anchor = GridBagConstraints.WEST;
        gbcMid.weightx = .5;
        gbcMid.insets = new Insets(1, 1, 1, 1);
        gbcMid.gridx = 0;
        gbcMid.gridy = 0;

        // Creating labels for Mid
        JLabel specialLabel = new JLabel("Special");
        specialLabel.setFont(new Font(Font.SERIF, Font.PLAIN, 24));
        JLabel conditionLabel = new JLabel("Condition");
        conditionLabel.setFont(new Font(Font.SERIF, Font.PLAIN, 24));
        JLabel sortByLabel = new JLabel("Sort By");
        sortByLabel.setFont(new Font(Font.SERIF, Font.PLAIN, 24));
        this.add(specialLabel, gbcMid);
        gbcMid.gridy = 1;
        this.add(conditionLabel, gbcMid);
        gbcMid.gridx = 2;
        this.add(sortByLabel, gbcMid);

        // Creating scroll box for special
        conditionList = new JList();
        conditionList.setLayoutOrientation(JList.VERTICAL);
        conditionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        sortByList = new JList();
        sortByList.setLayoutOrientation(JList.VERTICAL);
        sortByList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        gbcMid.gridx = 0;
        gbcMid.gridy = 2;
        JScrollPane conditionScroller = new JScrollPane(conditionList);
        conditionScroller.setPreferredSize(new Dimension(100, 100));
        this.add(conditionScroller, gbcMid);
        gbcMid.gridx = 2;
        JScrollPane sortByScroller = new JScrollPane(sortByList);
        sortByScroller.setPreferredSize(new Dimension(100, 100));
        this.add(sortByScroller, gbcMid);

        // Creating Radio Button Panels
        JPanel condRadioButtonPanel = new JPanel();
        JPanel sortRadioButtonPanel = new JPanel();
        GridBagConstraints gbcRadioPanel = new GridBagConstraints();
        gbcRadioPanel.fill = GridBagConstraints.HORIZONTAL;
        gbcRadioPanel.anchor = GridBagConstraints.WEST;
        gbcRadioPanel.weightx = .5;
        gbcRadioPanel.insets = new Insets(1, 1, 1, 1);
        gbcRadioPanel.gridx = 0;
        gbcRadioPanel.gridy = 0;
        condRadioButtonPanel.setLayout(new GridBagLayout());
        sortRadioButtonPanel.setLayout(new GridBagLayout());

        // Creating Radio Buttons for cond Panel
        operatorsBGroup = new ButtonGroup();
        greaterThanBtn = new JRadioButton(">");
        greaterThanBtn.setActionCommand(">");

        equalToBtn = new JRadioButton("=");
        equalToBtn.setActionCommand("=");

        lessThanBtn = new JRadioButton("<");
        lessThanBtn.setActionCommand("<");

        operatorsBGroup.add(greaterThanBtn);
        operatorsBGroup.add(equalToBtn);
        operatorsBGroup.add(lessThanBtn);
        compareValueComboBox = new JComboBox();
        JLabel compareValueLabel = new JLabel("number or word in single quotes");
        compareValueComboBox.setEditable(true);
        condRadioButtonPanel.add(greaterThanBtn, gbcRadioPanel);
        gbcRadioPanel.gridy = 1;
        condRadioButtonPanel.add(equalToBtn, gbcRadioPanel);
        gbcRadioPanel.gridy = 2;
        condRadioButtonPanel.add(lessThanBtn, gbcRadioPanel);
        gbcRadioPanel.gridx = 1;
        gbcRadioPanel.gridy = 0;
        condRadioButtonPanel.add(compareValueLabel, gbcRadioPanel);
        gbcRadioPanel.gridx = 1;
        gbcRadioPanel.gridy = 1;
        condRadioButtonPanel.add(compareValueComboBox, gbcRadioPanel);
        gbcMid.gridx = 1;
        gbcMid.gridy = 2;
        this.add(condRadioButtonPanel, gbcMid);

        // Creating Radio Buttons for sort Panel
        sortBGroup = new ButtonGroup();
        ascBtn = new JRadioButton("Ascending");
        ascBtn.setActionCommand("ASC");
        descBtn = new JRadioButton("Descending");
        descBtn.setActionCommand("DESC");
        sortBGroup.add(ascBtn);
        sortBGroup.add(descBtn);
        gbcRadioPanel.gridx = 0;
        gbcRadioPanel.gridy = 0;
        sortRadioButtonPanel.add(ascBtn, gbcRadioPanel);
        gbcRadioPanel.gridy = 1;
        sortRadioButtonPanel.add(descBtn, gbcRadioPanel);
        gbcMid.gridx = 3;
        gbcMid.gridy = 2;
        this.add(sortRadioButtonPanel, gbcMid);
    }

    public void updateLists(ArrayList<String> columnNames) {
        conditionList.removeAll();
        sortByList.removeAll();
        conditionList.setListData(columnNames.toArray());
        sortByList.setListData(columnNames.toArray());
    }

    public String getCondition() throws Exception {
        if (operatorsBGroup.getSelection() == null) {
            throw new Exception();
        }
        if (conditionList.getSelectedValue() == null) {
            throw new Exception();
        }
        if (compareValueComboBox.getSelectedItem() == null || compareValueComboBox.getSelectedItem().equals("")) {
            // get selected Item will not retrieve entered text if the combobox has focus
            throw new Exception();
        }
        // If its not a string of at most 20 alphanumeric characters surrounded by single quotes, then its an integer
        if (!Pattern.matches("^\'([0-9]|[a-z]|[A-Z]){0,20}\'$", (String)compareValueComboBox.getSelectedItem())) {
            try {
                Integer.parseInt((String) compareValueComboBox.getSelectedItem());
            } catch (Exception e) {
                throw e;
            }
        }
        return String.format("%s %s %s", conditionList.getSelectedValue(), operatorsBGroup.getSelection().getActionCommand(), compareValueComboBox.getSelectedItem());
    }

    public String getOrder() throws Exception {
        if (sortBGroup.getSelection() == null) {
            throw new Exception();
        }
        if (sortByList.getSelectedValue() == null) {
            throw new Exception();
        }
        return String.format("%s %s", sortByList.getSelectedValue(), sortBGroup.getSelection().getActionCommand());
    }
}
