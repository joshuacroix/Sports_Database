import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Table {
    private String name;
    private ArrayList<Pair<String,String>> joinable;  // Table name and column to join on
    private ArrayList<String> columns;

    public Table(String _name, ArrayList<String> _columns, HashMap<String,ArrayList<String>> columnsMap) {
        name = _name;
        columns = _columns;
        joinable = new ArrayList<Pair<String, String>>();
        for (Map.Entry<String,ArrayList<String>> pair : columnsMap.entrySet()) {
            if (!name.equals(pair.getKey())) {
                for (String colName : pair.getValue()) {
                    if(!colName.equals("year") && columns.contains(colName)) {
                        joinable.add(new Pair<String,String>(pair.getKey(),colName));
                        break;
                    }
                }
            }
        }
    }

    public ArrayList<String> getPossibleMerges() {
        ArrayList<String> tableNames = new ArrayList<String>();
        for (Pair<String,String> pair : joinable) {
            tableNames.add(pair.getKey());
        }
        return tableNames;
    }

    public String mergeOn(String other) {
        for(Pair<String,String> pair : joinable) {
            if(pair.getKey() == other)
                return pair.getValue();
        }
        return null;
    }

    public String toString() {
        return name;
    }


}
