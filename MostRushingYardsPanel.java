import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MostRushingYardsPanel extends JPanel {
    private JButton submitButton;
    private JComboBox selectionBox;
    private JTextArea answerArea;
    private Connection conn;

    public MostRushingYardsPanel(Connection conn, JTextArea answerArea) {

        this.answerArea = answerArea;
        this.conn = conn;

        this.setLayout(new GridBagLayout());
        GridBagConstraints gbcRYards = new GridBagConstraints();
        gbcRYards.fill = GridBagConstraints.HORIZONTAL;
        gbcRYards.anchor = GridBagConstraints.WEST;
        gbcRYards.weightx = .5;
        gbcRYards.insets = new Insets(1, 1, 1, 1);

        JLabel title = new JLabel("Most Rushing Yards Against A Given Team");
        gbcRYards.gridx = 0;
        gbcRYards.gridy = 0;
        this.add(title, gbcRYards);

        ArrayList<String> teamNames = new ArrayList<String>();
        try {
            ResultSet rs;
            Statement statement = conn.createStatement();
            String queryString = "select distinct name from team;";
            rs = statement.executeQuery(queryString);
            while (rs.next()) {
                teamNames.add(rs.getString("name"));
            }

        } catch (Exception e) {
            System.out.println(e);
        }

        selectionBox = new JComboBox(teamNames.toArray());
        gbcRYards.gridx = 0;
        gbcRYards.gridy = 1;
        this.add(selectionBox, gbcRYards);

        submitButton = new JButton("submit");
        gbcRYards.gridx = 0;
        gbcRYards.gridy = 2;
        this.add(submitButton, gbcRYards);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                try {
                    String teamName = (String)selectionBox.getSelectedItem();
                    ResultSet rs;
                    ResultSet givenTeamCodeResult;
                    Statement statement = conn.createStatement();
                    givenTeamCodeResult = statement.executeQuery("select distinct team_code from team where team.name = '"+teamName+"'");
                    givenTeamCodeResult.next();
                    int givenTeamCode = givenTeamCodeResult.getInt("team_code");
                    rs = statement.executeQuery("select versus.team_code, versus.rush_yard from team_game_statistics as versus inner join (select team_game_statistics.game_code, x.team_code from team_game_statistics  inner join  ( select distinct team_code from team where team.name = '"
                            + teamName + "') x  on x.team_code = team_game_statistics.team_code ) games on games.game_code = versus.game_code order by games.team_code");

                    HashMap<Integer, Integer> teamToYards = new HashMap<Integer, Integer>();
                    while(rs.next()) {
                        int team_code = rs.getInt("team_code");
                        if(team_code != givenTeamCode) {
                            Integer rushYard = rs.getInt("rush_yard");
                            if (teamToYards.containsKey(team_code)) {
                                teamToYards.put(team_code, teamToYards.get(team_code) + rushYard);
                            } else {
                                teamToYards.put(team_code, rushYard);
                            }
                        }
                    }
                    Pair<Integer, Integer> maxEntry = null;
                    Boolean first = true;
                    for (Map.Entry<Integer, Integer> entry : teamToYards.entrySet()) {
                        if (first) {
                            maxEntry = new Pair<Integer, Integer>(entry.getKey(), entry.getValue());
                            first = false;
                        } else if ((Integer)entry.getValue() > maxEntry.getValue()) {
                            maxEntry = new Pair<Integer, Integer>(entry.getKey(), entry.getValue());
                        }
                    }

                    rs = statement.executeQuery("select name from team where team_code = " + maxEntry.getKey().toString());
                    rs.next();
                    String mostYardsTeams = rs.getString("name");
                    answerArea.setText(mostYardsTeams);
                } catch (Exception e) {

                }
            }
        });
    }
}