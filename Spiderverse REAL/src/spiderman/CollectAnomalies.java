package spiderman;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * Steps to implement this class main method:
 * 
 * Step 1:
 * DimensionInputFile name is passed through the command line as args[0]
 * Read from the DimensionsInputFile with the format:
 * 1. The first line with three numbers:
 *      i.    a (int): number of dimensions in the graph
 *      ii.   b (int): the initial size of the cluster table prior to rehashing
 *      iii.  c (double): the capacity(threshold) used to rehash the cluster table 
 * 2. a lines, each with:
 *      i.    The dimension number (int)
 *      ii.   The number of canon events for the dimension (int)
 *      iii.  The dimension weight (int)
 * 
 * Step 2:
 * SpiderverseInputFile name is passed through the command line as args[1]
 * Read from the SpiderverseInputFile with the format:
 * 1. d (int): number of people in the file
 * 2. d lines, each with:
 *      i.    The dimension they are currently at (int)
 *      ii.   The name of the person (String)
 *      iii.  The dimensional signature of the person (int)
 * 
 * Step 3:
 * HubInputFile name is passed through the command line as args[2]
 * Read from the HubInputFile with the format:
 * One integer
 *      i.    The dimensional number of the starting hub (int)
 * 
 * Step 4:
 * CollectedOutputFile name is passed in through the command line as args[3]
 * Output to CollectedOutputFile with the format:
 * 1. e Lines, listing the Name of the anomaly collected with the Spider who
 *    is at the same Dimension (if one exists, space separated) followed by 
 *    the Dimension number for each Dimension in the route (space separated)
 * 
 * @author Seth Kelley
 */

public class CollectAnomalies {
    
    public static void main(String[] args) {

        if ( args.length < 4 ) {
            StdOut.println(
                "Execute: java -cp bin spiderman.CollectAnomalies <dimension INput file> <spiderverse INput file> <hub INput file> <collected OUTput file>");
                return;
        }

        // Read Command Line Inputs
        String dimensionsInputFile = args[0];
        String spiderverseInputFile = args[1];
        String hubInputFile = args[2];
        String outputFile = args[3];

        // Read First Line of Dimensions File
        StdIn.setFile(dimensionsInputFile);
        int numOfDimensions = StdIn.readInt();
        int tableSize = StdIn.readInt();
        double capacity = StdIn.readDouble();
        StdIn.readLine();

        LinkedList<Integer>[] clusters = new LinkedList[tableSize];
        clusters = Clusters.createHashTable(clusters, numOfDimensions, tableSize, capacity);

        HashMap<Integer, ArrayList<Integer>> adjacencyList = Collider.createAdjacencyList(clusters);

        //Read First Line of Spiderverse File
        StdIn.setFile(spiderverseInputFile);
        int numPeople = StdIn.readInt();
        StdIn.readLine();

        ArrayList<Person> spiderverseList = Collider.insertPeople(numPeople);
        HashMap<Integer, Person> spiders = new HashMap<Integer, Person>();
    
        for (Person person : spiderverseList) {
            if (person.getCurrentDimension() == person.getSignatureDimension()) {
                int key = person.getCurrentDimension();
                if (spiders.containsKey(key)) {
                    //Do Nothing
                }
                else {
                    spiders.put(key, person);
                }
            }
        }

        StdIn.setFile(hubInputFile);
        int hub = StdIn.readInt();
        StdOut.setFile(outputFile);
        for (Person person : spiderverseList) {
            if (person.getCurrentDimension() == hub || person.getCurrentDimension() == person.getSignatureDimension()) {
                //IGNORE ANOMALIES IN THE HUB
            }
            else if (spiders.containsKey(person.getCurrentDimension())) {
                StdOut.print(person.getName() + " " + spiders.get(person.getCurrentDimension()).getName() + " ");
                ArrayList<Integer> pathToAnomaly = findPath(adjacencyList, hub, person.getCurrentDimension());
                for (int i = pathToAnomaly.size() - 1; i >= 0; i--) {
                    StdOut.print(pathToAnomaly.get(i) + " ");
                }
                StdOut.println();
            }
            else {
                StdOut.print(person.getName() + " ");
                ArrayList<Integer> pathToAnomaly = findPath(adjacencyList, hub, person.getCurrentDimension());  
                for (int i = 0; i < pathToAnomaly.size(); i++) {
                    StdOut.print(pathToAnomaly.get(i) + " ");
                }
                for (int i = pathToAnomaly.size() - 2; i >= 0; i--) {
                    StdOut.print(pathToAnomaly.get(i) + " ");
                }
                StdOut.println();
            }
        }
        
    }

    public static ArrayList<Integer> findPath(HashMap<Integer, ArrayList<Integer>> adjacencyList, int startVertex, int targetVertex) {
        Queue<Integer> queue = new LinkedList<>();
        HashMap<Integer, Integer> parentMap = new HashMap<>();
        HashSet<Integer> visited = new HashSet<>();    
        queue.add(startVertex);
        visited.add(startVertex);
        boolean found = false;
    
        while (!queue.isEmpty()) {
            int currentVertex = queue.poll();
    
            if (currentVertex == targetVertex) {
                found = true;
                break;
            }
    
            for (int neighbor : adjacencyList.getOrDefault(currentVertex, new ArrayList<>())) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                    parentMap.put(neighbor, currentVertex);
                }
            }
        }
        if (found) {
            ArrayList<Integer> path = new ArrayList<>();
            int current = targetVertex;
            while (current != startVertex) {
                path.add(current);
                current = parentMap.get(current);
            }
            path.add(startVertex);
            Collections.reverse(path);
            return path;
        }
        return null;
    }
}