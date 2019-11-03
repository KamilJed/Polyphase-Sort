import disk.Tape;
import generator.DataSetGenerator;
import records.Record;
import sorter.Sorter;


public class AppMain {
    public static void main(String[] args) {
//        Tape tape = new Tape("dataSet", 3*8);
//        Record r = new Record(.5, .3, .7);
//        tape.saveRecord(r);
//        r = new Record(.5, .3, .5);
//        tape.saveRecord(r);
//
//        r = new Record(.5, .3, .6);
//        tape.saveRecord(r);
//        r = new Record(.5, .3, .4);
//        tape.saveRecord(r);
//
//        r = new Record(.5, .3, .45);
//        tape.saveRecord(r);
//        r = new Record(.5, .3, .1);
//        tape.saveRecord(r);
//
//        r = new Record(.5, .3, .6);
//        tape.saveRecord(r);
//        r = new Record(.5, .3, .2);
//        tape.saveRecord(r);
//
//        r = new Record(.5, .3, .45);
//        tape.saveRecord(r);
//        r = new Record(.5, .3, .2);
//        tape.saveRecord(r);
//
//        r = new Record(.5, .3, .7);
//        tape.saveRecord(r);
//        r = new Record(.5, .3, .3);
//        tape.saveRecord(r);
//
//        r = new Record(.5, .3, .5);
//        tape.saveRecord(r);
//        r = new Record(.5, .3, .1);
//        tape.saveRecord(r);
//
//        tape.flush();

        DataSetGenerator generator = new DataSetGenerator("dataSet");
        generator.generateDataSet(100);

        Sorter sorter = new Sorter("dataSet", 3*8, true);
        sorter.sort();

        Sorter sorter1 = new Sorter("dataSet", 3*8, true);
        sorter1.sort();
    }
}
