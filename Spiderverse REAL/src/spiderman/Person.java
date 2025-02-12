package spiderman;

public class Person {
    
    //Attributes
    int currentDimension;
    String personName;
    int dimensionalSignature;
    
    //Constructor
    public Person(int dimension, String name, int signature) {
        currentDimension = dimension;
        personName = name;
        dimensionalSignature = signature;
    }

    //Getters
    public int getCurrentDimension() {
        return currentDimension;
    }

    public String getName() {
        return personName;
    }

    public int getSignatureDimension() {
        return dimensionalSignature;
    }

    //Setters
    public void setCurrentDimension(int newDimension){
        currentDimension = newDimension;
    }
}
