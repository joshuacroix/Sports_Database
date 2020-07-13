import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class GUI extends JFrame{

    // Top Panel
    private JList tableList;
    private JList columnList;
    private JButton submitBtn;
    private JButton saveFileBtn;
    private JButton addConditionBtn;
    private JButton removeConditionBtn;

    // Middle Panel
    private GridBagConstraints gbcConditionsContainer;
    private JPanel conditionContainer;
    private JScrollPane scrollPane;

    // Bottom Panel
    private JTextArea outputTextArea;

    private App app;

    public GUI(App app) {
        this.app = app;
        this.setSize(1000,1000);
        //this.getContentPane().setBackground(new Color(255,255,255));
        this.getContentPane().setLayout(new GridBagLayout());

        // Use this constraint when adding components directly to the main frame
        GridBagConstraints gbcMain = new GridBagConstraints();
        gbcMain.fill = GridBagConstraints.WEST;
        gbcMain.anchor = GridBagConstraints.WEST;
        gbcMain.ipady = 10;
        gbcMain.weightx = 1;
        gbcMain.weighty = 1;
        gbcMain.insets = new Insets(5,5,5,5);
        gbcMain.gridx = 0;
        gbcMain.gridy = 0;

        //Creating new panel for top content: Select Scroll Boxes
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbcTop = new GridBagConstraints();
        gbcTop.fill = GridBagConstraints.HORIZONTAL;
        gbcTop.anchor = GridBagConstraints.WEST;
        gbcTop.weightx = 1;
        gbcTop.weighty = 1;
        gbcTop.insets = new Insets(1,1,1,20);
        gbcTop.gridx = 0;
        gbcTop.gridy = 0;

            // Creating labels for the scrollable select boxes
            JLabel tableSelectLabel = new JLabel("Table Select");
            JLabel columnSelectLabel = new JLabel("Column Select");
            tableSelectLabel.setFont(new Font(Font.SERIF, Font.PLAIN, 24));
            columnSelectLabel.setFont(new Font(Font.SERIF, Font.PLAIN, 24));
            topPanel.add(tableSelectLabel,gbcTop);
            gbcTop.gridx = 1;
            topPanel.add(columnSelectLabel,gbcTop);

            // Creating Select Windows
            tableList = new JList();
            columnList = new JList();
            tableList.setLayoutOrientation(JList.VERTICAL);
            columnList.setLayoutOrientation(JList.VERTICAL);
            tableList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            columnList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            gbcTop.gridx = 0; gbcTop.gridy = 1;
            JScrollPane tableScroller = new JScrollPane(tableList);
            tableScroller.setPreferredSize(new Dimension(160,200));
            topPanel.add(tableScroller,gbcTop);
            gbcTop.gridx = 1; gbcTop.gridy = 1;
            JScrollPane columnScroller = new JScrollPane(columnList);
            columnScroller.setPreferredSize(new Dimension(400,200));
            topPanel.add(columnScroller,gbcTop);

            // Creating Submit/Save to file Button
            JPanel topButtonsPanel = new JPanel();
            topButtonsPanel.setLayout(new GridBagLayout());
            GridBagConstraints gbcTopButtons = new GridBagConstraints();
            gbcTopButtons.fill = GridBagConstraints.HORIZONTAL;
            gbcTopButtons.anchor = GridBagConstraints.CENTER;
            gbcTopButtons.weightx = 1;
            gbcTopButtons.weighty = 1;
            gbcTopButtons.insets = new Insets(1,1,1,1);
            gbcTopButtons.gridx = 0;
            gbcTopButtons.gridy = 0;
            submitBtn = new JButton("Submit");
            saveFileBtn = new JButton("Save to File");
            topButtonsPanel.add(submitBtn,gbcTopButtons);
            gbcTopButtons.gridy = 1;
            topButtonsPanel.add(saveFileBtn,gbcTopButtons);
            gbcTop.gridx = 2; gbcTop.gridy = 1;
            topPanel.add(topButtonsPanel,gbcTop);

        this.add(topPanel,gbcMain);
        //Top Panel is finished.

            // Mid Panel set up
            JPanel midPanel = new JPanel();
            midPanel.setLayout(new GridBagLayout());
            GridBagConstraints gbcMidPanel = new GridBagConstraints();
            gbcMidPanel.fill = GridBagConstraints.HORIZONTAL;
            gbcMidPanel.anchor = GridBagConstraints.CENTER;
            gbcMidPanel.weightx = 1;
            gbcMidPanel.weighty = 1;
            gbcMidPanel.insets = new Insets(1,1,1,1);
            gbcMidPanel.gridx = 0;
            gbcMidPanel.gridy = 0;

            // Conditions Label
            JLabel conditionsLabel = new JLabel("Conditions");
            midPanel.add(conditionsLabel, gbcMidPanel);

            // add/remove conditions buttons
            addConditionBtn = new JButton("+");
            removeConditionBtn = new JButton("-");
            gbcMidPanel.gridx = 1;
            gbcMidPanel.gridy = 0;
            midPanel.add(addConditionBtn, gbcMidPanel);
            gbcMidPanel.gridx = 2;
            gbcMidPanel.gridy = 0;
            midPanel.add(removeConditionBtn, gbcMidPanel);

            // Scroll Pane
            scrollPane = new JScrollPane();
            scrollPane.setPreferredSize(new Dimension(800,200));
            GridBagConstraints gbcScroll = new GridBagConstraints();
            gbcScroll.fill = GridBagConstraints.HORIZONTAL;
            gbcScroll.anchor = GridBagConstraints.CENTER;
            gbcScroll.weightx = 1;
            gbcScroll.weighty = 1;
            gbcScroll.insets = new Insets(1,1,1,1);
            conditionContainer = new JPanel();
            conditionContainer.setLayout(new GridBagLayout());
            conditionContainer.add(new JLabel("Click the + button to add a new condition"));
            gbcScroll.gridx = 0;
            gbcScroll.gridy = 0;

            // add the container to the pane
            scrollPane.getViewport().add(conditionContainer, gbcScroll);
            gbcConditionsContainer = gbcScroll;

            // add the pane to the midPanel
            gbcMidPanel.gridx = 0;
            gbcMidPanel.gridy = 1;
            gbcMidPanel.gridwidth = 3;
            midPanel.add(scrollPane, gbcMidPanel);

        gbcMain.gridy = 1;
        this.add(midPanel, gbcMain);

        // Creating Bottom Panel for displaying the query
        JPanel botPanel = new JPanel();
        botPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbcBot = new GridBagConstraints();
        gbcBot.fill = GridBagConstraints.HORIZONTAL;
        gbcBot.anchor = GridBagConstraints.WEST;
        gbcBot.weightx = 1;
        gbcBot.weighty = 1;
        gbcBot.insets = new Insets(1,1,1,1);
        gbcBot.gridx = 0;
        gbcBot.gridy = 0;
            // Creating components of outputPanel
            JLabel outputLabel = new JLabel("Output");
            outputTextArea = new JTextArea();
            JScrollPane scrollingOutputTextArea = new JScrollPane(outputTextArea);
            scrollingOutputTextArea.setPreferredSize(new Dimension(500,100));
            botPanel.add(outputLabel,gbcBot);
            gbcBot.gridy = 1;
            botPanel.add(scrollingOutputTextArea,gbcBot);
        gbcMain.gridy = 2;
        this.add(botPanel,gbcMain);

//        ArrayList<String> tableNames = new ArrayList<String>();
//        app.tables.forEach((v)->{
//            tableNames.add(v.toString());
//        });


        tableList.setListData(app.tables.toArray());

        // Setting up action listeners
        tableList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                columnList.removeAll();
                ArrayList<String> colsOfSelectedTables = new ArrayList<String>();
                for (Object tableNameObject : tableList.getSelectedValuesList()) {
                    String tableName = (String)tableNameObject.toString();
                    app.tableNameToColumnName.get(tableName).forEach((x)->{
                        colsOfSelectedTables.add(tableName+ "." + x);
                    });

                }
                ArrayList<String> queryables =  new ArrayList<String>();
                colsOfSelectedTables.forEach((col) -> {
                    queryables.add(col);
                    queryables.add("AVG(" + col + ")");
                    queryables.add("SUM(" + col + ")");
                    queryables.add("COUNT(" + col + ")");
                });
                columnList.setListData(queryables.toArray());
            }
        });

        columnList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (conditionContainer.getComponents().length == 1 && conditionContainer.getComponent(0) instanceof JLabel) {
                    // No condition Panels
                    return;
                }
                ArrayList<String> columnNames = new ArrayList<String>(columnList.getSelectedValuesList());
                for (Component condPane : conditionContainer.getComponents()) {
                    ((ConditionPanel)condPane).updateLists(columnNames);
                }
           }
        });

        submitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String result = app.makeBasicQuery((ArrayList<Table>) tableList.getSelectedValuesList(), Arrays.asList(columnList.getSelectedValues()), getConditions() , getOrderBy() , 30);
                outputTextArea.setText(result);
            }
        });

        saveFileBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame popup = new JFrame();
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Specify a file to save");
                int userSelection = fileChooser.showSaveDialog(popup);
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File fileToSave = fileChooser.getSelectedFile();
                    try {
                        FileWriter myWriter = new FileWriter(fileToSave.getAbsolutePath());
                        myWriter.write(app.makeBasicQuery((ArrayList<Table>) tableList.getSelectedValuesList(), Arrays.asList(columnList.getSelectedValues()), getConditions(), getOrderBy(), -1));
                        myWriter.close();
                    }
                    catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }
            }
        });

        addConditionBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (conditionContainer.getComponents().length == 1 && conditionContainer.getComponent(0) instanceof JLabel) {
                    // Remove the "click plus" label
                    conditionContainer.remove(0);
                }
                gbcConditionsContainer.gridy = gbcConditionsContainer.gridy + 1;
                conditionContainer.add(new ConditionPanel(), gbcConditionsContainer);
                // call the column selection listener
                columnList.getListSelectionListeners()[0].valueChanged(new ListSelectionEvent(addConditionBtn, 0, 0, false));
                revalidate();
                repaint();
            }
        });

        removeConditionBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gbcConditionsContainer.gridy = gbcConditionsContainer.gridy - 1;
                int index = conditionContainer.getComponentCount() - 1;
                if (index < 0) {
                    return;
                }
                conditionContainer.remove(index);

                if (index == 0) {
                    conditionContainer.add(new JLabel("Click the + button to add a new condition"));
                }
                revalidate();
                repaint();
            }
        });

        // Testing VictoryChainPanel
        VictoryChainPanel testingMyPanel = new VictoryChainPanel(app.conn,outputTextArea);
        gbcMain.gridx = 1;
        gbcMain.gridy = 0;
        this.add(testingMyPanel,gbcMain);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);

        // Testing VictoryChainPanel
        HomeAdvantage homeAdvantage = new HomeAdvantage(app.conn,outputTextArea);
        gbcMain.gridx = 1;
        gbcMain.gridy = 1;
        this.add(homeAdvantage,gbcMain);

        MostRushingYardsPanel rushYard = new MostRushingYardsPanel(app.conn, outputTextArea);
        gbcMain.gridx = 1;
        gbcMain.gridy = 2;
        this.add(rushYard, gbcMain);

        AveragePoints averagePoints = new AveragePoints(app.conn, outputTextArea);
        gbcMain.gridx = 1;
        gbcMain.gridy = 3;
        this.add(averagePoints, gbcMain);

    }

    public ArrayList<String> getConditions() {
        ArrayList<String> conditions = new ArrayList<String>();
        for (Component condition : this.conditionContainer.getComponents()) {
            if (condition instanceof JLabel) {
                continue;
            }
            ConditionPanel conditionPanel = (ConditionPanel) condition;
            try {
                conditions.add(conditionPanel.getCondition());
            } catch (Exception e) {
                continue;
            }
        }
        return conditions;
    }

    public ArrayList<String> getOrderBy() {
        ArrayList<String> ordersBy = new ArrayList<String>();
        for (Component condition : this.conditionContainer.getComponents()) {
            if (condition instanceof JLabel) {
                continue;
            }
            ConditionPanel conditionPanel = (ConditionPanel) condition;
            try {
                ordersBy.add(conditionPanel.getOrder());
            } catch (Exception e) {
                continue;
            }
        }
        return ordersBy;
    }

    public static void main(String[] args) {
        GUI main = new GUI(new App());
    }
}
