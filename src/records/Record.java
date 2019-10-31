package records;

public class Record implements Comparable<Record>{

    private double probabilityOfA;
    private double probabilityOfB;
    private double probabilityOfUnion;

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
        return Double.compare(intersectionProbability(), record.intersectionProbability());
    }

    private double intersectionProbability(){
        return probabilityOfA + probabilityOfB - probabilityOfUnion;
    }
}
