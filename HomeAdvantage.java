//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.*;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class HomeAdvantage extends JPanel {
    private JTextField conference_name;
    private JButton runButton;
    private ArrayList<Team> teams = new ArrayList();
    private HashMap<String, Integer> conferences = new HashMap();



    public HomeAdvantage(Connection conn, final JTextArea outputTextArea) {

        this.setLayout(new GridBagLayout());
        GridBagConstraints gbcVic = new GridBagConstraints();
        gbcVic.fill = 2;
        gbcVic.anchor = 17;
        gbcVic.weightx = 0.5D;
        gbcVic.insets = new Insets(1, 1, 1, 1);
        gbcVic.gridx = 0;
        gbcVic.gridy = 0;
        JLabel titleLabel = new JLabel("Home Advantage");
        titleLabel.setFont(new Font("Serif", 0, 24));
        this.add(titleLabel, gbcVic);
        gbcVic.gridy = 1;
        this.conference_name = new JTextField("Atlantic Coast Conference");
        this.add(this.conference_name, gbcVic);
        gbcVic.gridx = 1;
        gbcVic.gridx = 2;
        gbcVic.gridx = 0;
        gbcVic.gridy = 2;
        this.runButton = new JButton("Submit");
        this.add(this.runButton, gbcVic);
        this.runButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //Team Lookups
                try {
                    Statement stmt = conn.createStatement();
                    String query = "SELECT DISTINCT team_code, name FROM team";
                    ResultSet rs = stmt.executeQuery(query);

                    while(rs.next()) {
                        teams.add(new Team(rs.getInt(1), rs.getString(2)));
                    }
                } catch (SQLException var11) {
                    var11.printStackTrace();
                }

                HashMap<Integer, Team> lookupTeams = new HashMap();
                Iterator var13 = teams.iterator();

                while(var13.hasNext()) {
                    Team team = (Team)var13.next();
                    lookupTeams.put(team.getTeam_code(), team);
                }

                //Conference Lookups
                try {
                    Statement stmt = conn.createStatement();
                    String query = "SELECT * FROM conference;";
                    ResultSet rs = stmt.executeQuery(query);

                    while(rs.next()) {
                        conferences.put(rs.getString(2), rs.getInt(1));
                    }
                } catch (SQLException var11) {
                    var11.printStackTrace();
                }

                Integer conference_code_holder = conferences.get(conference_name.getText());
                if (conference_code_holder == null)
                {
                    outputTextArea.setText("Enter a correct conference name");
                    return;
                }
                //MY CODE BELOW


                try {
                    Statement stmt = conn.createStatement();
                    String query = "SELECT DISTINCT " +
                            "conference_code, team.team_code, game.game_code, points, home_team_code " +
                            "FROM " +
                            "team " +
                            "INNER JOIN " +
                            "team_game_statistics ON team_game_statistics.team_code = team.team_code " +
                            "INNER JOIN " +
                            "game ON game.game_code = team_game_statistics.game_code " +
                            "WHERE " +
                            "team.conference_code = '" + conference_code_holder + "' " +
                            "ORDER BY " +
                            "game_code;";
                    ResultSet rs = stmt.executeQuery(query);
                    int point1 = 0; //Holder of the points of 1
                    int point2 = 0; //Holder of the points of 2
                    String game_code1 = "";
                    String game_code2 = "";
                    int team = 0;
                    String result = "";

                    HashMap<Integer, Integer> Home_Wins = new HashMap<Integer, Integer>();
                    HashMap<Integer, Integer> Home_Losses = new HashMap<Integer, Integer>();
                    HashMap<Integer, Integer> Scores = new HashMap<Integer, Integer>();


                    while (rs.next()) {
                        if (rs.getInt(2) == rs.getInt(5)) {
                            team = rs.getInt(2); //Assigns the home team
                        }
                        point1 = rs.getInt(4);
                        game_code1 = rs.getString(3);
                        if (rs.next()) {
                            if (rs.getInt(2) == rs.getInt(5)) {
                                team = rs.getInt(2); //Assigns the home team
                            }
                            point2 = rs.getInt(4);
                            game_code2 = rs.getString(3);
                            if (game_code1.equals(game_code2)) { // Never True
                                if (point1 > point2) {
                                    if (Home_Wins.containsKey(team)) {
                                        //Increment the value of Home_Wins by 1
                                        Home_Wins.put(team, Home_Wins.get(team) + 1);
                                    } else {
                                        Home_Wins.put(team, 1);
                                    }
                                } else {
                                    if (Home_Losses.containsKey(team)) {
                                        //Increment the value of Home_Wins by 1
                                        Home_Losses.put(team, Home_Losses.get(team) + 1);
                                    } else {
                                        Home_Losses.put(team, 1);
                                    }
                                }
                            }
                        }
                    }
                    //This is where I start on the score hashmap stuff
                    for (Map.Entry mapElement : Home_Wins.entrySet()) {
                        Integer key = (Integer) mapElement.getKey();
                        Integer wins = (Integer)mapElement.getValue();
                        Integer losses =  Home_Losses.get(key);
                        float score = 0;
                        if (losses == null) {
                            losses = 0;
                        }
                        if (wins == null) {
                            wins = 0;
                        }
                        score =(float) wins / (losses + wins);
                        System.out.println("Wins: "  + wins + "Losses: " + losses);
                        String team_name = "";
                        team_name = lookupTeams.get(key).getTeam_name();
                        result = result + team_name + ": " + score + "\n";
                    }
                    if (result == ""){
                        result = "No data";
                    }
                    outputTextArea.setText(result);

                }
                catch (SQLException var10) {
                    var10.printStackTrace();
                }
            }
        });
    }


}
