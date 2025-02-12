package spiderman;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import org.w3c.dom.Node;

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
 * Read from the SpotInputFile with the format:
 * One integer
 *      i.    The dimensional number of the starting hub (int)
 * 
 * Step 4:
 * AnomaliesInputFile name is passed through the command line as args[3]
 * Read from the AnomaliesInputFile with the format:
 * 1. e (int): number of anomalies in the file
 * 2. e lines, each with:
 *      i.   The Name of the anomaly which will go from the hub dimension to their home dimension (String)
 *      ii.  The time allotted to return the anomaly home before a canon event is missed (int)
 * 
 * Step 5:
 * ReportOutputFile name is passed in through the command line as args[4]
 * Output to ReportOutputFile with the format:
 * 1. e Lines (one for each anomaly), listing on the same line:
 *      i.   The number of canon events at that anomalies home dimensionafter being returned
 *      ii.  Name of the anomaly being sent home
 *      iii. SUCCESS or FAILED in relation to whether that anomaly made it back in time
 *      iv.  The route the anomaly took to get home
 * 
 * @author Seth Kelley
 */

public class GoHomeMachine {
    
    public static void main(String[] args) {

        if ( args.length < 5 ) {
            StdOut.println(
                "Execute: java -cp bin spiderman.GoHomeMachine <dimension INput file> <spiderverse INput file> <hub INput file> <anomalies INput file> <report OUTput file>");
                return;
        }

        // Read Command Line Inputs
        String dimensionsInputFile = args[0];
        String spiderverseInputFile = args[1];
        String hubInputFile = args[2];
        String anomaliesInputFile = args[3];
        String outputFile = args[4];

        // Read First Line of Dimensions File
        StdIn.setFile(dimensionsInputFile);
        int numOfDimensions = StdIn.readInt();
        int tableSize = StdIn.readInt();
        double capacity = StdIn.readDouble();
        StdIn.readLine();

        LinkedList<Integer>[] clusters = new LinkedList[tableSize];
        clusters = Clusters.createHashTable(clusters, numOfDimensions, tableSize, capacity);
        
        StdIn.setFile(dimensionsInputFile);
        StdIn.readLine();
        HashMap<Integer, Dimension> dimensionHashMap = Clusters.createDimensionTable(numOfDimensions);

        HashMap<Integer, ArrayList<Integer>> adjacencyList = Collider.createAdjacencyList(clusters);

        //Read First Line of Spiderverse File
        StdIn.setFile(spiderverseInputFile);
        int numPeople = StdIn.readInt();
        StdIn.readLine();
        

        ArrayList<Person> spiderverseList = Collider.insertPeople(numPeople);
        HashMap<Integer, Person> spiders = new HashMap<Integer, Person>();

        

        //Read Hub File
        StdIn.setFile(hubInputFile);
        int hub = StdIn.readInt();

        //Read Anomalies File
        ArrayList<Anomalies> anomaliesList = new ArrayList<>();
        StdIn.setFile(anomaliesInputFile);
        int numAnomalies = StdIn.readInt();
        StdIn.readLine();
        for (int i = 0; i < numAnomalies; i++) {
            String name = StdIn.readString();
            int time = StdIn.readInt();
            StdIn.readLine();
            
            //Update current dimension to hub
            for (int j = 0; j < spiderverseList.size(); j++) {
                if (name.equals(spiderverseList.get(i).getName())) {
                    spiderverseList.get(i).setCurrentDimension(hub);
                }
            }

            Anomalies newAnomaly = new Anomalies(name, time);
            anomaliesList.add(newAnomaly);
        }

        StdOut.setFile(outputFile);
        
        //Return Anomalies to Signature Dimension
        for (Anomalies anomaly : anomaliesList) {
            
            int targetDimension = 0;
            for (int i = 0; i < spiderverseList.size(); i++) {
                if (anomaly.getAnomalyName().equals(spiderverseList.get(i).getName())) {
                    targetDimension = spiderverseList.get(i).getSignatureDimension();
                }
            }
            
            ArrayList<Integer> pathHome = dijkstra(adjacencyList, dimensionHashMap, hub, targetDimension);

            
            //Calculate Weights
            int totalWeight = 0;
            for (int i = 0; i < pathHome.size() - 1; i++) {
                int dimension1 = pathHome.get(i);
                int dimension2 = pathHome.get(i+1);
                int dimension1Weight = dimensionHashMap.get(dimension1).getDimensionWeight();
                int dimension2Weight = dimensionHashMap.get(dimension2).getDimensionWeight();
                totalWeight += dimension1Weight + dimension2Weight;
            }
        
            //Print CanonEvents
            int homeDimension = pathHome.get(pathHome.size()-1);
            
            if (anomaly.getTimeToReturn() >= totalWeight) {
                StdOut.print(dimensionHashMap.get(homeDimension).getCanonNumber() + " ");
                StdOut.print(anomaly.getAnomalyName());
                StdOut.print(" SUCCESS ");
            }
            else {
                StdOut.print(dimensionHashMap.get(homeDimension).getCanonNumber()-1 + " ");
                StdOut.print(anomaly.getAnomalyName());
                StdOut.print(" FAILED ");
            }
            for (int i = 0; i < pathHome.size(); i++) {
                StdOut.print(pathHome.get(i) + " ");
            }
            StdOut.println();
        }

        
    }
    
    public static ArrayList<Integer> dijkstra(HashMap<Integer, ArrayList<Integer>> adjacencyList,
                                              HashMap<Integer, Dimension> dimensionsHashMap,
                                              int source, int target) {
        HashMap<Integer, Integer> distances = new HashMap<>();
        HashMap<Integer, Integer> parents = new HashMap<>();
        HashSet<Integer> visited = new HashSet<>();
        PriorityQueue<Integer> pq = new PriorityQueue<>(Comparator.comparingInt(distances::get));

        for (int dimension : adjacencyList.keySet()) {
            distances.put(dimension, Integer.MAX_VALUE);
        }
        distances.put(source, 0);

        pq.offer(source);

        while (!pq.isEmpty()) {
            int currentDimension = pq.poll();
            visited.add(currentDimension);

            if (currentDimension == target) {
                return constructPath(parents, target);
            }

            for (int neighbor : adjacencyList.getOrDefault(currentDimension, new ArrayList<>())) {
                if (!visited.contains(neighbor)) {
                    int weight = dimensionsHashMap.get(currentDimension).getDimensionWeight() +
                            dimensionsHashMap.get(neighbor).getDimensionWeight();
                    int newDistance = distances.get(currentDimension) + weight;

                    if (newDistance < distances.get(neighbor)) {
                        distances.put(neighbor, newDistance);
                        parents.put(neighbor, currentDimension);
                        pq.offer(neighbor);
                    }
                }
            }
        }

        return new ArrayList<>();
    }

    private static ArrayList<Integer> constructPath(HashMap<Integer, Integer> parents, int target) {
        ArrayList<Integer> path = new ArrayList<>();
        for (int dimension = target; dimension != 0; dimension = parents.getOrDefault(dimension, 0)) {
            path.add(0, dimension);
        }
        return path;
    }


}
