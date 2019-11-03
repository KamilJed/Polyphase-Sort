package records;

import java.util.Random;

public class Record implements Comparable<Record>{

    private double probabilityOfA;
    private double probabilityOfB;
    private double probabilityOfUnion;

    public Record(){
        generateRandomProbs();
    }

    public Record(double pA, double pB, double pAB){
        probabilityOfA = pA;
        probabilityOfB = pB;
        probabilityOfUnion = pAB;
    }

    public double getProbabilityOfA() {
        return probabilityOfA;
    }

    public double getProbabilityOfB() {
        return probabilityOfB;
    }

    public double getProbabilityOfUnion() {
        return probabilityOfUnion;
    }

    @Override
    public int compareTo(Record record) {
        if(record == null)
            return 1;
        return Double.compare(intersectionProbability(), record.intersectionProbability());
    }

    @Override
    public String toString(){
        return  Double.toString(intersectionProbability());
    }

    private void generateRandomProbs(){
        Random random = new Random();
        do{
            probabilityOfA = random.nextDouble();
            probabilityOfB = random.nextDouble();
            probabilityOfUnion = random.nextDouble();
            probabilityOfUnion = random.nextDouble();
        }
        while(intersectionProbability() > 1 || intersectionProbability() < 0);
    }

    private double intersectionProbability(){
        return probabilityOfA + probabilityOfB - probabilityOfUnion;
    }
}
