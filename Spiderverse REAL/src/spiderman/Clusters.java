package spiderman;

import java.util.LinkedList;
import java.util.*;

// /**
//  * Steps to implement this class main method:
//  *
//  * Step 1:
//  * DimensionInputFile name is passed through the command line as args[0]
//  * Read from the DimensionsInputFile with the format:
//  * 1. The first line with three numbers:
//  * i. a (int): number of dimensions in the graph
//  * ii. b (int): the initial size of the cluster table prior to rehashing
//  * iii. c (double): the capacity(threshold) used to rehash the cluster table
//  *
//  * Step 2:
//  * ClusterOutputFile name is passed in through the command line as args[1]
//  * Output to ClusterOutputFile with the format:
//  * 1. n lines, listing all of the dimension numbers connected to
//  * that dimension in order (space separated)
//  * n is the size of the cluster table.
//  *
//  * @author Seth Kelley
//  */

public class Clusters {

    public static void main(String[] args) {
        if (args.length < 2) {
            StdOut.println("Usage: java Clusters <dimension input file> <cluster output file>");
            return;
        }
        
        // Read Command Line Inputs
        String inputFile = args[0];
        String outputFile = args[1];
        
        StdIn.setFile(inputFile);
        
        //Read First Line of Dimensions File
        int numOfDimensions = StdIn.readInt();
        int tableSize = StdIn.readInt();
        double capacity = StdIn.readDouble();
        StdIn.readLine();
        
        LinkedList<Integer>[] clusters = new LinkedList[tableSize];
        clusters = createHashTable(clusters, numOfDimensions, tableSize, capacity);

        //Write to Output File
        StdOut.setFile(outputFile);
        int count = 0;
        for (int i = 0; i < clusters.length; i++) {
            count = 0;
            for (Integer dimension : clusters[i]) {
                if (count == clusters[i].size() - 1) {
                    StdOut.print(dimension);
                }
                else {
                    StdOut.print(dimension + " ");
                }
                count++;
            }
            if (i != clusters.length - 1) {
                StdOut.println();
            }
        }
    }

    public static LinkedList<Integer>[] createHashTable(LinkedList<Integer>[] clusters, int numOfDimensions, int tableSize, double capacity) {
    
        int dimensionsAdded = 0;
        for (int i = 0; i < numOfDimensions; i++) {

            // Read Line
            int dimensionNum = StdIn.readInt();
            int canonNum = StdIn.readInt();
            int dimensionWeight = StdIn.readInt();
            StdIn.readLine();

            // Check the Capacity
            if ((double) (dimensionsAdded / tableSize) >= capacity) {
                clusters = rehash(clusters);
            }

            tableSize = clusters.length;
            int index = dimensionNum % tableSize;

            if (clusters[index] == null) {
                clusters[index] = new LinkedList<>();
                clusters[index].add(dimensionNum);
            } else {
                clusters[index].addFirst(dimensionNum);
            }
            dimensionsAdded++;
        }

        // Connect the Dimensions
        for (int i = 0; i < clusters.length; i++) {
            // if i = 0, then get last index head node and last but one index head node
            // else if i = 1, then get i=0 index head node and last index head node
            // else get i-1 head node and i - 2 head node
            int previous1 = 0;
            int previous2 = 0;
            if (i == 0) {
                previous1 = clusters[clusters.length - 1].getFirst();
                previous2 = clusters[clusters.length - 2].getFirst();
            } else if (i == 1) {
                previous1 = clusters[i - 1].getFirst();
                previous2 = clusters[clusters.length - 1].getFirst();
            } else {
                previous1 = clusters[i - 1].getFirst();
                previous2 = clusters[i - 2].getFirst();
            }

            // Add previou1 and previous2 to end of list
            clusters[i].addLast(previous1);
            clusters[i].addLast(previous2);
        }
        return clusters;
    }

    public static HashMap<Integer, Dimension> createDimensionTable(int numOfDimensions) {
    
        HashMap<Integer, Dimension> dimensionHashMap = new HashMap<>();
        for (int i = 0; i < numOfDimensions; i++) {

            // Read Line
            int dimensionNum = StdIn.readInt();
            int canonNum = StdIn.readInt();
            int dimensionWeight = StdIn.readInt();
            StdIn.readLine();

            //Create Dimension Object
            Dimension newDimension = new Dimension(dimensionNum, canonNum, dimensionWeight);
            dimensionHashMap.put(dimensionNum, newDimension);
        }

        return dimensionHashMap;
    }
    
    public static LinkedList<Integer>[] rehash(LinkedList<Integer>[] currTable) {

        int currSize = currTable.length;
        int newSize = currSize * 2;
        LinkedList<Integer>[] newTable = new LinkedList[newSize];

        for (int i = 0; i < currSize; i++) {
            for (Integer dimension : currTable[i]) {
                int newIndex = dimension % newSize;
                if (newTable[newIndex] == null) {
                    newTable[newIndex] = new LinkedList<>();
                    newTable[newIndex].add(dimension);
                } else {
                    newTable[newIndex].addFirst(dimension);
                }
            }
        }
        return newTable;
    }
}