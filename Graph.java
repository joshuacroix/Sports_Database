
import javax.crypto.NullCipher;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Queue;

public class Graph<T,V> {
    private ArrayList<Pair<T,Boolean>> vertices; // Indexes and isVisited
    private ArrayList<ArrayList<V>> edges; //Adjacency Matrix with weighted edges

    Graph(ArrayList<T> _vertices) {
        vertices = new ArrayList<Pair<T,Boolean>>();
        for (T vertex:_vertices) {
            vertices.add(new Pair<T,Boolean>(vertex,false));
        }
        edges = new ArrayList<ArrayList<V>>(vertices.size());
        for(int i = 0; i < vertices.size(); i++) {
            ArrayList<V> list = new ArrayList<V>(vertices.size());
            for(int j = 0; j < vertices.size(); j++) {
                list.add(j,null);
            }
            edges.add(i,list);
        }
    }

    private void setUnvisited() {
        for (int i = 0; i < vertices.size(); i++) {
            vertices.get(i).setValue(false);
        }
    }

    public boolean addEdge(T fromVertex, T toVertex, V weight) {
        Pair<T,Boolean> vertex1 = new Pair<T,Boolean>(fromVertex,false);
        Pair<T,Boolean> vertex2 = new Pair<T,Boolean>(toVertex,false);
        int fromIndex = vertices.indexOf(vertex1);
        int toIndex = vertices.indexOf(vertex2);
        if(fromIndex == -1 || toIndex == -1) {
            return false;
        }
        edges.get(fromIndex).set(toIndex,weight);
        return true;
    }

    public ArrayList<Pair<Integer,V>> findShortestPath(T startVertex, T endVertex) {
        Pair<T,Boolean> vertex1 = new Pair<T,Boolean>(startVertex,false);
        Pair<T,Boolean> vertex2 = new Pair<T,Boolean>(endVertex,false);
        setUnvisited();
        int startIndex = vertices.indexOf(vertex1);
        int endIndex = vertices.indexOf(vertex2);
        if(startIndex == -1 || endIndex == -1) {
            System.out.println("Returning null 1"  + startIndex + " " + endIndex + " " + vertices);
            return null;
        }
        int[] pred = new int[vertices.size()];
        int[] distance = new int[vertices.size()];
        setUnvisited();

        for(int i = 0; i < vertices.size(); i++) {
            pred[i] = -1;
            distance[i] = 1000000000;
        }

        ArrayList<Integer> queue = new ArrayList<Integer>();
        int front = 0;
        queue.add(startIndex);
        // Visited first node
        vertices.get(startIndex).setValue(true);
        distance[startIndex] = 1;

        while(front != queue.size() && queue.get(front) != endIndex) {
            for(int i = 0; i < vertices.size(); i++) {
                if(edges.get(queue.get(front)).get(i) != null && vertices.get(i).getValue() == false) {
                    //Vertex is adjacent and not visited
                    queue.add(i);
                    vertices.get(i).setValue(true);
                    distance[i] = distance[queue.get(front)]+1;
                    pred[i] = queue.get(front);
                }
            }
            front++;
        }
        if(front == queue.size()){
            System.out.println("return null at 2");
            return null;
        }
        // Find shortest path
        int currentIndex = endIndex;
        ArrayList<Pair<Integer,V>> path = new ArrayList<Pair<Integer,V>>();
        while(pred[currentIndex] != -1){
            path.add(new Pair<Integer, V>(currentIndex,edges.get(pred[currentIndex]).get(currentIndex)));
            currentIndex = pred[currentIndex];
        }
        System.out.println("Path: " + path);
        return path;
    }
}
