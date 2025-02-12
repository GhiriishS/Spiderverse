package spiderman;

import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Steps to implement this class main method:
 * 
 * Step 1:
 * DimensionInputFile name is passed through the command line as args[0]
 * Read from the DimensionsInputFile with the format:
 * 1. The first line with three numbers:
 * i. a (int): number of dimensions in the graph
 * ii. b (int): the initial size of the cluster table prior to rehashing
 * iii. c (double): the capacity(threshold) used to rehash the cluster table
 * 2. a lines, each with:
 * i. The dimension number (int)
 * ii. The number of canon events for the dimension (int)
 * iii. The dimension weight (int)
 * 
 * Step 2:
 * SpiderverseInputFile name is passed through the command line as args[1]
 * Read from the SpiderverseInputFile with the format:
 * 1. d (int): number of people in the file
 * 2. d lines, each with:
 * i. The dimension they are currently at (int)
 * ii. The name of the person (String)
 * iii. The dimensional signature of the person (int)
 * 
 * Step 3:
 * ColliderOutputFile name is passed in through the command line as args[2]
 * Output to ColliderOutputFile with the format:
 * 1. e lines, each with a different dimension number, then listing
 * all of the dimension numbers connected to that dimension (space separated)
 * 
 * @author Seth Kelley
 */

public class Collider {

    public static void main(String[] args) {
        if (args.length < 3) {
            StdOut.println(
                    "Usage: java Collider <dimension input file> <spiderverse input file> <collider output file>");
            return;
        }

        // Read Command Line Inputs
        String dimensionsInputFile = args[0];
        String spiderverseInputFile = args[1];
        String outputFile = args[2];

        // Read First Line of Dimensions File
        StdIn.setFile(dimensionsInputFile);
        int numOfDimensions = StdIn.readInt();
        int tableSize = StdIn.readInt();
        double capacity = StdIn.readDouble();
        StdIn.readLine();

        LinkedList<Integer>[] clusters = new LinkedList[tableSize];
        clusters = Clusters.createHashTable(clusters, numOfDimensions, tableSize, capacity);

        HashMap<Integer, ArrayList<Integer>> adjacencyList = createAdjacencyList(clusters);

        // Write to File
        StdOut.setFile(outputFile);
        for (HashMap.Entry<Integer, ArrayList<Integer>> entry : adjacencyList.entrySet()) {
            StdOut.print(entry.getKey() + " ");
            for (int i = 0; i < entry.getValue().size(); i++) {
                StdOut.print(entry.getValue().get(i) + " ");
            }
            StdOut.println();
        }

    }

    public static HashMap<Integer, ArrayList<Integer>> createAdjacencyList(LinkedList<Integer>[] clusters) {

        HashMap<Integer, ArrayList<Integer>> adjacencyList = new HashMap<>();

        for (int i = 0; i < clusters.length; i++) {
            int firstDimension = clusters[i].getFirst();
            for (Integer dimension : clusters[i]) {
                if (dimension == firstDimension) {
                    if (adjacencyList.containsKey(dimension)) {
                        continue;
                    } else {
                        ArrayList<Integer> newArrayList = new ArrayList<>();
                        adjacencyList.put(firstDimension, newArrayList);
                    }
                } else {
                    if (adjacencyList.containsKey(dimension)) {
                        adjacencyList.get(dimension).add(firstDimension);
                        adjacencyList.get(firstDimension).add(dimension);
                    } else {
                        ArrayList<Integer> newArrayList = new ArrayList<>();
                        adjacencyList.put(dimension, newArrayList);
                        adjacencyList.get(dimension).add(firstDimension);
                        adjacencyList.get(firstDimension).add(dimension);
                    }
                }
            }
        }
        return adjacencyList;
    }

    public static ArrayList<Person> insertPeople(int numPeople) {

        ArrayList<Person> spiderverseList = new ArrayList<>();

        for (int i = 0; i < numPeople; i++) {
            int dimension = StdIn.readInt();
            String name = StdIn.readString();
            int signature = StdIn.readInt();
            StdIn.readLine();

            Person newPerson = new Person(dimension, name, signature);

            spiderverseList.add(newPerson);
        }

        return spiderverseList;
    }
}