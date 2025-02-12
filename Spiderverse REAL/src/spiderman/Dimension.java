package spiderman;

public class Dimension {

    //Attributes
    int dimensionNum;
    int canonNum;
    int dimensionWeight;
    
    //Constructor
    public Dimension(int dimension, int canon, int weight) {
        dimensionNum = dimension;
        canonNum = canon;
        dimensionWeight = weight;
    }

    //Getters
    public int getDimensionNumber() {
        return dimensionNum;
    }

    public int getCanonNumber() {
        return canonNum;
    }

    public int getDimensionWeight() {
        return dimensionWeight;
    }

    //Setters
    public void setCanonNumber() {
        canonNum--;
    }
}
