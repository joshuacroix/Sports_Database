import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class AveragePoints extends  JPanel {

    private JTextField teamName;
    private JButton runButton;
    private ArrayList<Integer> differences;


    AveragePoints(Connection conn, JTextArea outputTextArea) {
        String name;
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbcVic = new GridBagConstraints();
        gbcVic.fill = GridBagConstraints.HORIZONTAL;
        gbcVic.anchor = GridBagConstraints.WEST;
        gbcVic.weightx = .5;
        gbcVic.insets = new Insets(1, 1, 1, 1);
        gbcVic.gridx = 0;
        gbcVic.gridy = 0;


        JLabel titleLabel = new JLabel("On average, how many many more or less points does that team score?");
        titleLabel.setFont(new Font(Font.SERIF, Font.PLAIN, 18));
        this.add(titleLabel, gbcVic);

        gbcVic.gridy = 1;
        teamName = new JTextField("UC Davis", 10);
        this.add(teamName, gbcVic);


        gbcVic.gridx = 0;
        gbcVic.gridy = 2;
        runButton = new JButton("Submit");
        this.add(runButton, gbcVic);

        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {

                    //Getting the Team Code of the given team

                    ResultSet ts;
                    int teamCodeQuery;
                    Statement stmt2 = conn.createStatement();
                    String tcQuery = "SELECT DISTINCT team_code FROM team WHERE name = '" + teamName.getText() + "'";
                    ts = stmt2.executeQuery(tcQuery);
                    ts.next();
                    teamCodeQuery = ts.getInt("team_code");

                    ResultSet rs;
                    Statement stmt = conn.createStatement();
                    String Query = "select versus.team_code, versus.game_code, versus.points from team_game_statistics as versus\n" +
                            "inner join " +
                            " (select team_game_statistics.game_code, x.team_code from team_game_statistics " +
                            " inner join " +
                            "( select distinct team_code from team where team.name ='" + teamName.getText() + "' )x " +
                            " on x.team_code = team_game_statistics.team_code " +
                            " ) games " +
                            "on games.game_code = versus.game_code order by games.game_code";

                    rs = stmt.executeQuery(Query);

                    //ArrayList<Integer> teamPoints; // contains the points of the given team
                    //ArrayList<Integer> otherTeamPoint; // contains the points of other team
                    differences = new ArrayList<Integer>();

                    // keeping track of the number of games
                    int teamGivenCode = 0; // holds given team's score
                    int otherTeamCode = 0; // holds other teams's score
                    int teamPoints = 0; // holds the given team's points
                    int otherTeamPoints = 0; // holds the other team's points
                    int differencePoints = 0;
                    //String gameCode;

                    while (rs.next()) {
                        teamGivenCode = rs.getInt(1);
                        teamPoints = rs.getInt(3);
                        rs.next();

                        otherTeamCode = rs.getInt(1);
                        otherTeamPoints = rs.getInt(3);

                        if (teamGivenCode != teamCodeQuery) {
                            differencePoints = otherTeamPoints - teamPoints;
                        } else {
                            differencePoints = teamPoints - otherTeamPoints;
                        }

                        differences.add(differencePoints);

                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                String Result = findAveragePoints();
                outputTextArea.setText(Result);
            }
        });
    }
    //Get the Games codes

    private String findAveragePoints() {
        int numerator = 0;
        int denominator = differences.size();
        Float average = new Float(0);

        for (int i = 0; i < differences.size(); i++) {
            numerator += differences.get(i);
        }

        average = (float)numerator / (float)denominator;
        String averageString = Float.toString(average);
        return averageString;
    }

}
