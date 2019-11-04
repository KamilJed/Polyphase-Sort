import disk.Tape;
import generator.DataSetGenerator;
import records.Record;
import sorter.Sorter;


public class AppMain {
    public static void main(String[] args) {

        DataSetGenerator generator = new DataSetGenerator("dataSet");
        generator.generateDataSet(10);

        Sorter sorter = new Sorter("dataSet", 10*8, true);
        sorter.sort();
    }
}
