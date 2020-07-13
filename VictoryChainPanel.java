

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class VictoryChainPanel extends JPanel {
    private JTextField team1Tf;
    private JTextField team2Tf;
    private JButton runButton;
    private ArrayList<Team> teams;
    private Graph<Team,Integer> victoryGraph; // Team and Year as weight of edges

    VictoryChainPanel(Connection conn, JTextArea outputTextArea) {
        // Initaiizing variables
        teams = new ArrayList<Team>();
        // Setting up JPanel
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbcVic = new GridBagConstraints();
        gbcVic.fill = GridBagConstraints.HORIZONTAL;
        gbcVic.anchor = GridBagConstraints.WEST;
        gbcVic.weightx = .5;
        gbcVic.insets = new Insets(1, 1, 1, 1);
        gbcVic.gridx = 0;
        gbcVic.gridy = 0;

        JLabel titleLabel = new JLabel("Victory Chain");
        titleLabel.setFont(new Font(Font.SERIF, Font.PLAIN, 24));
        this.add(titleLabel,gbcVic);

        gbcVic.gridy = 1;
        team1Tf = new JTextField("Texas A&M",10);
        this.add(team1Tf,gbcVic);

        gbcVic.gridx = 1;
        JLabel beatsLabel = new JLabel("beats");
        this.add(beatsLabel,gbcVic);

        gbcVic.gridx = 2;
        team2Tf = new JTextField("Marshall",10);
        this.add(team2Tf,gbcVic);

        gbcVic.gridx = 0;
        gbcVic.gridy = 2;
        runButton = new JButton("Submit");
        this.add(runButton,gbcVic);

        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String result = findVictoryChain(team1Tf.getText(),team2Tf.getText());
                outputTextArea.setText(result);
            }
        });
        try {
            // Add all teams to teams list with team codes
            ResultSet rs;
            Statement stmt = conn.createStatement();
            String query = "SELECT DISTINCT team_code, name FROM team";
            rs = stmt.executeQuery(query);
            while (rs.next()){
                teams.add(new Team(rs.getInt(1),rs.getString(2)));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        // Create lookup table between Teams and team codes for adding edges from wins
        HashMap<Integer,Team> lookupTeams = new HashMap<Integer, Team>();
        for(Team team : teams){
            lookupTeams.put(team.getTeam_code(),team);
        }
        // Initialize the graph with all the vertices
        victoryGraph = new Graph<Team, Integer>(teams);
        // Add edges for victories
        try {
            // Add all teams to teams list with team codes
            ResultSet rs;
            Statement stmt = conn.createStatement();
            String query = "SELECT home_team_code, visit_team_code, home.points, away.points, game.year FROM " +
                    "((game INNER JOIN team_game_statistics as home ON game.home_team_code = home.team_code AND home.game_code = game.game_code) " +
                    "INNER JOIN team_game_statistics as away ON game.visit_team_code = away.team_code AND away.game_code = game.game_code) ";
            rs = stmt.executeQuery(query);
            while (rs.next()){
                if(rs.getInt(3) > rs.getInt(4))
                    victoryGraph.addEdge(lookupTeams.get(rs.getInt(1)),lookupTeams.get(rs.getInt(2)),rs.getInt(5));
                else
                    victoryGraph.addEdge(lookupTeams.get(rs.getInt(2)),lookupTeams.get(rs.getInt(1)),rs.getInt(5));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String findVictoryChain(String team1, String team2) {
        String result = "";
        System.out.println(team1 + " " + team2);
        Team winner = null;
        Team loser = null;
        for (int i = 0; i < teams.size(); i++) {
            if(teams.get(i).getTeam_name().equals(team1)) {
                winner = teams.get(i);
            }
            if(teams.get(i).getTeam_name().equals(team2)) {
                loser = teams.get(i);
            }
            if(winner != null && loser != null) {
                break;
            }
        }
        if (winner == null) {
            result += "Team 1 not found\n";
        }
        if (loser == null){
            result += "Team 2 not found\n";
        }
        if (result != "")
            return result;

        ArrayList<Pair<Integer,Integer>> path = victoryGraph.findShortestPath(winner,loser);
        if (path == null) {
            result += "Victory chain not found";
            return result;
        }
        result += team1;
        for(int i = path.size()-1; i >= 0; i--) {
            if(i != 0){
            result += " beat " + teams.get(path.get(i).getKey()).getTeam_name() +
                    " " + path.get(i).getValue() + ". " +
                    teams.get(path.get(i).getKey()).getTeam_name();
            }
            else{
                result += " beat " + teams.get(path.get(i).getKey()).getTeam_name() +
                        " " + path.get(i).getValue() + ". ";
            }
        }

        return result;
    }
}
