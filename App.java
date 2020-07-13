import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.sql.*;
import java.util.*;
import java.util.List;

public class App {


    public Connection conn = null;
    public HashMap<String,ArrayList<String>> tableNameToColumnName = new HashMap<String, ArrayList<String>>();
    public ArrayList<Table> tables = new ArrayList<Table>();

    public App() {
        String URL = "jdbc:postgresql://csce-315-db.engr.tamu.edu/money_foot_ball";
        String USER = "joshuacroix";
        String PASS = "Gateway12!";

        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(URL, USER, PASS);
            ArrayList<String> tableNames = new ArrayList<String>();
            DatabaseMetaData dbmd = conn.getMetaData();
            ResultSet db = dbmd.getTables(null, null, "%", null);
            while (db.next()) {
                if (db.getString(4) != null && db.getString(4).equals("TABLE")) {
                    tableNames.add(db.getString(3));
                }
            }

            for (String tableName : tableNames) {
                ResultSet rs;
                tableNameToColumnName.put(tableName, new ArrayList<String>());
                Statement stmt = conn.createStatement();

                String sqlStatement = "SELECT * FROM " + tableName + " LIMIT 10";
                rs = stmt.executeQuery(sqlStatement);
                ResultSetMetaData rsmd = rs.getMetaData();
                int colCount = rsmd.getColumnCount();

                for (int i = 1; i <= colCount; ++i) {
                    tableNameToColumnName.get(tableName).add(rsmd.getColumnName(i));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        /*
        * This may be dumb, but this is a map of joinable tables AKA the tables that have identical column names, this may come in use later when forming inner joins
        * This can also be used for dynamically changing the table select possibly.
        * */
        tableNameToColumnName.forEach((k,v)->{
            tables.add(new Table(k,v,tableNameToColumnName));
        });


        // MATTHEWS SPACE
        // TREVORS SPACE

    }

    public String makeBasicQuery(ArrayList<Table> tableName, List<Object> columnList, ArrayList<String> conditions, ArrayList<String> orderBy, int limit) {
        String output = "";
        try {
            Statement stmt = conn.createStatement();
            String sqlStatement = buildQueryString(tableName, columnList, conditions, orderBy, limit);
            System.out.println(sqlStatement);
            ResultSet rs = stmt.executeQuery(sqlStatement);
            ResultSetMetaData rsmd = rs.getMetaData();
            return formatQueryResult(rs, rsmd);
        }
        catch (SQLException e) {
            e.printStackTrace();
            return "Something went wrong, Error is:\n\n" + e.getMessage();
        }
    }

    public String buildQueryString(ArrayList<Table> tableList, List<Object> columnList, ArrayList<String> conditions, ArrayList<String> orderBy, int limit) {
        String sqlStatement = "SELECT";
        for (int i = 0; i < columnList.size(); i++) {
            sqlStatement += " " + columnList.get(i);
            if( i != columnList.size() - 1) {
                sqlStatement += ",";
            }
        }
        sqlStatement += " FROM ";
        // Joining tables
        sqlStatement += tableList.get(0).toString();

        for (int i = 1; i < tableList.size(); i++) {
            sqlStatement += " INNER JOIN " + tableList.get(i).toString() + " ON " + tableList.get(0).toString() + "." +
                    tableList.get(0).mergeOn(tableList.get(i).toString()) + " = " + tableList.get(i).toString() + "." +
                    tableList.get(i).mergeOn(tableList.get(0).toString());
        }

        if (conditions.size() > 0) {
            sqlStatement += " WHERE";
            for (int i = 0; i < conditions.size(); ++i) {
                sqlStatement += " " + conditions.get(i);
                if (i != conditions.size() - 1) {
                    sqlStatement += " AND";
                }
            }
        }

        ArrayList<String> aggregateColumns = new ArrayList<String>();
        for (Object column : columnList) {
            if (
                    ((String)column).startsWith("AVG(") ||
                    ((String)column).startsWith("SUM(") ||
                    ((String)column).startsWith("COUNT(")
            ) {
                aggregateColumns.add((String) column);
            }
        }
        if (aggregateColumns.size() > 0) {
            sqlStatement += " GROUP BY";
        }
        for (int i = 0; i < aggregateColumns.size(); ++i) {
            String aggregate = aggregateColumns.get(i);
            int endOfColName = aggregate.length() - 1;
            if (aggregate.startsWith("AVG(")) {
                sqlStatement += " " + aggregate.substring(4, endOfColName);
            } else if (aggregate.startsWith("SUM(")) {
                sqlStatement += " " + aggregate.substring(4, endOfColName);
            } else if (aggregate.startsWith("COUNT(")) {
                sqlStatement += " " + aggregate.substring(6, endOfColName);
            }
            if (i != aggregateColumns.size() - 1) {
                sqlStatement += ",";
            }
        }

        if (orderBy.size() > 0) {
            sqlStatement += " ORDER BY";
            for (int i = 0; i < orderBy.size(); ++i) {
                sqlStatement += " " + orderBy.get(i);
                if (i != orderBy.size() - 1) {
                    sqlStatement += ",";
                }
            }
        }

        if (limit != -1)
            sqlStatement += " LIMIT 30";

        return sqlStatement + ";";
    }

    public String formatQueryResult(ResultSet resultSet, ResultSetMetaData resultSetMetaData) throws SQLException {
        String output = "";
        try {
            int columnsNumber = resultSetMetaData.getColumnCount();
            for (int i = 1; i <= columnsNumber; i++) {
                output += resultSetMetaData.getColumnName(i) + "\t";
            }
            output += "\n";
            while (resultSet.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    String columnValue = resultSet.getString(i);
                    output += columnValue + "\t";
                }
                output += "\n";
            }
        } catch (SQLException e) {
            throw e;
        }
        return output;
    }


}