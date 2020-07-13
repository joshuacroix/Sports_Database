import java.util.Objects;

public class Team {
    private int team_code;
    private String team_name;
    private boolean visited;
    public Team(int code, String name) {
        team_code = code;
        team_name = name;
        visited = false;
    }

    public int getTeam_code() {
        return team_code;
    }

    public String getTeam_name() {
        return team_name;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return team_code == team.team_code &&
                visited == team.visited &&
                Objects.equals(team_name, team.team_name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(team_code, team_name, visited);
    }
}
